/************************************************************/
//Computes all the SpeciesDelimitation Information and 
//provides methods to display it to the user.
/************************************************************/

package speciesDelimitation;


import com.biomatters.geneious.publicapi.components.GComboBox;
import com.biomatters.geneious.publicapi.components.GPanel;
import jebl.evolution.graphs.Node;
import jebl.evolution.trees.RootedTree;
import jebl.evolution.trees.RootedTreeUtils;
import jebl.evolution.trees.Tree;
import jebl.evolution.trees.TreeSelectionChangeEvent;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigInteger;
import java.util.*;
import java.util.List;

public class ResultsTable{
	private String[][] results;
	private ArrayList<TreeNodeSet> set;
	private final int ROW_SIZE;
	private final int COL_SIZE;
	private TreeNodeSet closest;
	private TreeNodeSet curSet;
	private JButton closestGroup;
	private double closestDist=0.0;
	private GPanel resultsPanel;
	private JPanel detailsPanel;
	private GComboBox groupList;
	private Coalescence coalescence;
	private GeneticDistances geneticDistances;
	private SpeciesDelimitation speciesDelimitation;
	public ResultsTable(ArrayList<TreeNodeSet> set, SpeciesDelimitation sd){
		//geneticDistances = new GeneticDistances(tree);
		this.speciesDelimitation = sd;
		this.geneticDistances = new GeneticDistances(sd.getTree());
		this.ROW_SIZE = set.size()+1;
		this.COL_SIZE = 13;
		results = new String[ROW_SIZE][COL_SIZE];
		this.set=set;
		computeResults(sd);
		resultsPanel = new GPanel(new GridLayout(1,2));
  	}
	
	public void setUp(){
		results[0][0]="Group";
		for(int i=1; i<ROW_SIZE; i++){
			results[i][0]=""+i;
		}
	}

	public String toTable(){
		StringBuffer buftable= new StringBuffer();
		buftable.append("<table BORDER=1>");
		for(int i=0; i< ROW_SIZE; i++){
			buftable.append("<tr>");
			for(int j=0; j< COL_SIZE-1; j++){ //-1
				buftable.append("<td><CENTER>"+results[i][j]+"</td>");
   			}
			buftable.append("</tr>");
		}
		buftable.append("</table>");
		String table = buftable.toString();
		return table;
	}
	
	public ArrayList<String> resultsSet(){
		ArrayList<String> resultsSet = new ArrayList<String>();
		for(int group=1; group<=set.size(); group++){
			String table="<table BORDER=1><tr>";
   			for(int i=0; i<COL_SIZE-1; i++){ //-1
	    			table+="<td><CENTER>"+results[group][i]+"</td>";
	    			table+="</tr>";
   			}
    		table+="</table>";
    		resultsSet.add(table);
			
		}
		return resultsSet;
	}
	
	public String toTable(int group){
		String table="<table BORDER=1>";
			for(int i=0; i<COL_SIZE-1; i++){ //-1
				if(i!=0&&i!=2){
   				table+="<tr>";
    			table+="<td><CENTER>"+results[0][i]+"</td>";
    			table+="<td><CENTER>"+results[group][i]+"</td>";
    			table+="</tr>";
				}
			}
		table+="</table>";
		return table;
	}
	
	public JTable toJTable(int group){
		String[] col = new String[2];
			col[0]= "";
			col[1]= "";
		String[][] data = new String[COL_SIZE-2][2]; 
		for(int k=2; k<COL_SIZE-1; k++){ //-1
				data[k-2][0]= results[0][k];
				data[k-2][1]= results[group+1][k];
		}
		JTable jtable = new JTable(new DefaultTableModel(data,col){
    		final static long serialVersionUID = 0;
			public boolean isCellEditable(int row, int col){
		    	return false;
		    }
		});
		jtable.setColumnSelectionAllowed(true);
		return jtable;
	}
	
	public String round(double value, int decimalPlace){
		int dec=decimalPlace;
		double power_of_ten = 1;
	    while (decimalPlace-- > 0){
	       power_of_ten *= 10.0;
	    }
	    String res = ""+(Math.round(value * power_of_ten) / power_of_ten);
	    while(res.length()<(dec+2)){
	    	res+="0";
	    }
   	    return res;
	}
	
	private String truncate(double d, int dp){
		double d2=d;
		int cutOff=dp+1;
		long mult = 1;
		String result =""+d;
		for(int i=0; i<dp; i++){
			mult = mult*10;
		}
		int index = result.indexOf("E");
		if(index==-1 && (((int)(d*mult*10))%10)>4){
 			d2=((d*mult)+1)/mult;
 			result = ""+d2;
		}
				
		if(((int)(d2*mult))>0 && index==-1){
			if(result.length()>(cutOff+1)){
				return result.substring(0,cutOff+1);
			}else{
				return result;
			}
		}else{
    		if(index!=-1){
    			String result1 = result.substring(0, cutOff );
    			String result2 = result.substring(index, result.length());
    			String result3 = result1+result2;
    			return result3;
    		}else{
    			Formatter fmt = new Formatter();
    			String newresult = fmt.format("%.2E", d).toString();
    			return newresult;
    		}
		}
	}

	public void computeResults(SpeciesDelimitation sd){
		this.speciesDelimitation = sd;
		
		results[0][0] = "Species"; 
		results[0][1] = "Closest Species"; 
		//Whether the group is defined as Monophyletic, Polyphyletic or Paraphyletic.
		results[0][2] = "Monophyletic?"; 
//		results[0][2] = "Structure"; 
		//The groups intraspecific distance based on the average distances between taxa within that group. A smaller distance indicates that individuals within this group are more similar than greater distances.
		results[0][3] = "Intra Dist"; ;
		//The interspecific distance not between all nodes but just between taxa of the genetically closest group.
		results[0][4] = "Inter Dist - Closest"; 
		results[0][5] = "Intra/Inter";
		//Given strict species grouping factors, the probability that this group is distinct in that individuals in this group should be assigned to this group based in the genetic distinctiveness of this group compared to the closest group.
		results[0][6] = "P ID(Strict)";
		//Given liberal species grouping factors, the probability that this group is distinct in that individuals in this group should be assigned to this group based in the genetic distinctiveness of this group compared to the closest group.
		results[0][7] = "P ID(Liberal)";
		//The Average distance between the MRCA of a group and its children.
		results[0][8] = "Av(MRCA-tips)";
		//Probability that monophyletic group appears distinct due to chance factors in coalescence. 
		results[0][9] = "P(Randomly Distinct)"; 
		//Bootstrap Support if it is available.
		results[0][10] = "Clade Support"; 
		coalescence = new Coalescence(speciesDelimitation.getTree(), geneticDistances);
		//Rosenberg's P(AB) probability that A and B are reciprocally monophyletic
		results[0][11] = "Rosenberg's P(AB)";
//		results[0][12] = "Rosenberg's P(AB)";
		
		for(int i=0; i<set.size(); i++){
			
			try{
			TreeNodeSet putative = set.get(i);
			results[i+1][0] = putative.getName();
			double intraSpecificDist = putative.intraSpecificDist(geneticDistances);
			interSpecificDist(putative);
			results[i+1][1] = closest.getName();
//			results[i+1][2] = putative.getPhyly();
			if(RootedTreeUtils.isMonophyletic((RootedTree) sd.getTree(), putative.getLeafNodes())){
				results[i+1][2] = "yes";
			}
			else{
				results[i+1][2] = "no";
			}
			results[i+1][3] = ""+truncate(intraSpecificDist,3);
			results[i+1][4] = truncate(closestDist,3);
			double intraInterRatio = intraInterRatio(intraSpecificDist);
			results[i+1][5] = truncate(intraInterRatio,2);
			int n = putative.getNumIndividuals();
			String probCorId =  truncate(probCorrectIDStrict(intraInterRatio,n),2);
			if(probCorId.equals("1.0")){
				probCorId+="0";
			}
			String probCorIdlower =  truncate(probCorrectIDStrictLower(intraInterRatio,n),2);
			probCorId+=" (";
			probCorId+=probCorIdlower;
			probCorId+=", ";
			String probCorIdupper =  truncate(probCorrectIDStrictUpper(intraInterRatio,n),2);
			probCorId+=probCorIdupper;
			probCorId+=")";
			results[i+1][6] = probCorId;

			probCorId =  truncate(probCorrectIDLiberal(intraInterRatio,n),2);
			if(probCorId.equals("1.0")){
				probCorId+="0";
			}
			probCorIdlower =  truncate(probCorrectIDLiberalLower(intraInterRatio,n),2);
			probCorId+=" (";
			probCorId+=probCorIdlower;
			probCorId+=", ";
			probCorIdupper =  truncate(probCorrectIDLiberalUpper(intraInterRatio,n),2);
			probCorId+=probCorIdupper;
			probCorId+=")";
			results[i+1][7] =probCorId;

			results[i+1][8] = truncate(MRCA(putative),4);
			double m = (MRCA(putative)/ancestorToRootPath(putative));
//			if(putative.getPhyly().equals("Monophyletic")){
			if(RootedTreeUtils.isMonophyletic((RootedTree) sd.getTree(), putative.getLeafNodes())){
				double p = coalescence.prob(putative,m);
				if(p>0.05){
					results[i+1][9] = round(p,2);
				}else{
					if(m>0.00009){
						results[i+1][9] = "<0.05";
					}else{
						results[i+1][9] = "NA";
					}
				}
			}else{
				results[i+1][9] = "NA";
			}
			if(putative.hasConsensusSupport(sd, putative)){
				results[i+1][10] = (""+putative.getConsensusSupportValue()+"00000").substring(0,5)+"%";
			}
			else if(putative.hasBootstrap(sd, putative)){
				results[i+1][10] = (""+putative.getBootstrapValue()+"00000").substring(0,5);
			}
			else if(putative.hasLabel(sd, putative)){
				results[i+1][10] = (""+putative.getLabelValue()+"00000").substring(0,5);
			}
			else if(putative.hasCladesupport(sd, putative)){
				results[0][10] = "Clade Support";
				results[i+1][10] = (""+putative.getCladesupport()+"00000").substring(0,5)+"%";
			}else{
				results[i+1][10] = "NA";
			}

			//Rosenberg's P(AB)
			if(RootedTreeUtils.isMonophyletic((RootedTree) sd.getTree(), putative.getLeafNodes())){
//			if(putative.getPhyly().equals("Monophyletic")){
//				if (putative.getNumIndividuals() > 1){
//					double r_ab = Rosenberg_PAB(putative, sd);
//					results[i+1][11] = truncate(r_ab,2);
//				}
//				else {
//					results[i+1][11] = "NA";
//				}
				double r_ab = Rosenberg_PAB(putative, sd);
				results[i+1][11] = truncate(r_ab,2);
			}
			else {
				results[i+1][11] = "NA";
			}
						
		
			}catch(Exception e){
				for(int j=0; j<COL_SIZE; j++){
					results[i+1][j]="";
				}
				
			}
			
		}		
		
	}
	
	public String groupInfo(int group){
		String table="";
		for(int j=0; j< COL_SIZE-1; j++){ //-1
			if(j!=0||j!=2){
				table+=results[group][j]+"\t\t";
			}
		}
		table+="<br>";
		return table;
	}
	
	public double intraInterRatio(double intra){
		if(intra!=0 && closestDist!=0){
			return intra/closestDist;
		}else{
			return 0;
		}
	}
	
	public double MRCA(TreeNodeSet putative){
		Node root = putative.getRoot();
		if(root!=null){
			double MRCADist =0;
			int count=0;
			for(Node n: putative.getLeafNodes()){
				MRCADist+= geneticDistances.path(root,n);
				count++;
			}
			return MRCADist/count;
		}else{
			return Double.NaN;
		}
	}
	
	public double ancestorToRootPath(TreeNodeSet putative){
		Node root = putative.getRoot();
		Node ancestor = putative.getAncestor();
   		if(root!=null && ancestor!=null){
   			return geneticDistances.path(root,ancestor);
   		}else{
   			return Double.NaN;
   		}
	}
	
	private double getInterSpecificDist(TreeNodeSet putative,TreeNodeSet comparisonSet){
 		Set<Node> leafNodes = putative.getLeafNodes();
 		Set<Node> comparisoLeafNodes = comparisonSet.getLeafNodes();
 		double interSpecificDist=0.0;
 		int count=0;
   		for(Node leaf: leafNodes){
 			for(Node compLeaf: comparisoLeafNodes){
 				interSpecificDist+=geneticDistances.path(leaf, compLeaf);
 				count++;
 			}
 		}
   		return interSpecificDist/count;
	}

	public void interSpecificDist(TreeNodeSet putative){
		closestDist=999999;
		for(int i=0; i<set.size(); i++){
			TreeNodeSet compare = set.get(i);
			if(!putative.isEqual(compare)){
				double dist = getInterSpecificDist(putative,compare);
				if(dist<closestDist){
					closestDist =getInterSpecificDist(putative,compare);
					closest = compare;
				}
			}
		}
	}
	private double probCorrectIDStrict(double Dist, int n){
    	//Probability of making a Correct ID when species present in reference data set	 
		double CorId=0;
		if(n==1){CorId = 0;}
		else if(n==2) {CorId = 0.59072-0.51034*(Dist);}
		else if(n==3) {CorId = 0.79205-0.66971*(Dist);}
		else if(n==4) {CorId = 0.86990-0.68018*(Dist);}
		else if(n==5||n==6) {CorId = 0.93485-0.67434*(Dist);}
		else if(n==7||n==8) {CorId = 0.94545-0.42677*(Dist)-0.18031*(Dist)*(Dist);}
		else if(n>=9 && n<=11) {CorId = 0.98731-0.68009*(Dist)+0.57825*(Dist)*(Dist)-0.45767*(Dist)*(Dist)*(Dist);}
		else if(n>=12 && n<=15) {CorId = 0.99074-0.476254*(Dist)+0.443549*(Dist)*(Dist)-0.449814*(Dist)*(Dist)*(Dist);}
		else {CorId = 0.998269-0.456527*(Dist)+0.562927*(Dist)*(Dist)-0.531258*(Dist)*(Dist)*(Dist);}

	   	if(CorId>1){CorId = 1;}
    	if(CorId<0){CorId = 0;}
    	return CorId;
    }
	
	private double probCorrectIDStrictUpper(double Dist, int n){
    	//Probability of making a Correct ID when species present in reference data set	 
		double UpperCorId=0;
		if(n==1){UpperCorId = 0;}
		else if(n==2) {UpperCorId = 0.7402226-0.5010747*(Dist);}
		else if(n==3) {UpperCorId = 0.9668638-0.658875*(Dist);}
		else if(n==4) {UpperCorId = 1.0107295-0.6714501*(Dist);}
		else if(n==5||n==6) {UpperCorId = 1.0595772-0.6703516*(Dist);}
		else if(n==7||n==8) {UpperCorId = 1.0522371-0.4324328*(Dist)-0.1722499*(Dist)*(Dist);}
		else if(n>=9 && n<=11) {UpperCorId = 1.0723675-0.6714207*(Dist)+0.5599466*(Dist)*(Dist)-0.4456586*(Dist)*(Dist)*(Dist);}
		else if(n>=12 && n<=15) {UpperCorId = 1.0580101-0.4710178*(Dist)+0.4324783*(Dist)*(Dist)-0.4425519*(Dist)*(Dist)*(Dist);}
		else {UpperCorId = 1.0505259-0.4524591*(Dist)+0.5543274*(Dist)*(Dist)-0.525616*(Dist)*(Dist)*(Dist);}

	   	if(UpperCorId>1){UpperCorId = 1;}
    	if(UpperCorId<0){UpperCorId = 0;}
    	return UpperCorId;
    }
	private double probCorrectIDStrictLower(double Dist, int n){
    	//Probability of making a Correct ID when species present in reference data set	 
		double LowerCorId=0;
		if(n==1){LowerCorId = 0;}
		else if(n==2) {LowerCorId = 0.4412211-0.5196075*(Dist);}
		else if(n==3) {LowerCorId = 0.6172424-0.6805452*(Dist);}
		else if(n==4) {LowerCorId = 0.7290775-0.6889075*(Dist);}
		else if(n==5||n==6) {LowerCorId = 0.8101282-0.6783376*(Dist);}
		else if(n==7||n==8) {LowerCorId = 0.8386673-0.4210996*(Dist)-0.1883635*(Dist)*(Dist);}
		else if(n>=9 && n<=11) {LowerCorId = 0.9022586-0.6887675*(Dist)+0.5965607*(Dist)*(Dist)-0.4696852*(Dist)*(Dist)*(Dist);}
		else if(n>=12 && n<=15) {LowerCorId = 0.9234697-0.4814906*(Dist)+0.4546198*(Dist)*(Dist)-0.4570766*(Dist)*(Dist)*(Dist);}
		else {LowerCorId = 0.9460115-0.4605947*(Dist)+0.5715275*(Dist)*(Dist)-0.5368992*(Dist)*(Dist)*(Dist);}

	   	if(LowerCorId>1){LowerCorId = 1;}
    	if(LowerCorId<0){LowerCorId = 0;}
    	return LowerCorId;
	}
	private double probCorrectIDLiberal(double Dist, int n){
    	//Probability of making a Correct ID when species present in reference data set	 
		double CorId=0;
		if(n==1){CorId = 0.96484-0.74274*(Dist);}
		else if(n==2) {CorId = 0.98258-0.61679*(Dist);}
		else if(n==3) {CorId = 1.00687-0.56573*(Dist);}
		else if(n==4) {CorId = 0.98043-0.1738*(Dist)-0.30003*(Dist)*(Dist);}
		else if(n==5||n==6) {CorId = 0.97808-0.05551*(Dist)-0.33913*(Dist)*(Dist);}
		else if(n==7||n==8) {CorId = 1.009651-0.442216*(Dist)+0.707384*(Dist)*(Dist)-0.590013*(Dist)*(Dist)*(Dist);}
		else if(n>=9 && n<=11) {CorId = 1.010236-0.367638*(Dist)+0.616946*(Dist)*(Dist)-0.519988*(Dist)*(Dist)*(Dist);}
		else if(n>=12 && n<=15) {CorId = 1.007992-0.284746*(Dist)+0.560303*(Dist)*(Dist)-0.485715*(Dist)*(Dist)*(Dist);}
		else {CorId = 1.007161-0.257062*(Dist)+0.568809*(Dist)*(Dist)-0.488766*(Dist)*(Dist)*(Dist);}
    	if(CorId>1){CorId = 1;}
    	if(CorId<0){CorId = 0;}
    	return CorId;
    }
	
	private double probCorrectIDLiberalUpper(double Dist, int n){
    	//Probability of making a Correct ID when species present in reference data set	 
		double UpperCorId=0;
		if(n==1){UpperCorId = 1.0961686-0.7348063*(Dist);}
		else if(n==2) {UpperCorId = 1.1331151-0.6074602*(Dist);}
		else if(n==3) {UpperCorId = 1.1493754-0.5569018*(Dist);}
		else if(n==4) {UpperCorId = 1.091192-0.184369*(Dist)-0.2847648*(Dist)*(Dist);}
		else if(n==5||n==6) {UpperCorId = 1.0800616-0.0609263*(Dist)-0.3314327*(Dist)*(Dist);}
		else if(n==7||n==8) {UpperCorId = 1.0721892-0.4329678*(Dist)+0.6879245*(Dist)*(Dist)-0.5772358*(Dist)*(Dist)*(Dist);}
		else if(n>=9 && n<=11) {UpperCorId = 1.0629776-0.3622593*(Dist)+0.6055943*(Dist)*(Dist)-0.5125381*(Dist)*(Dist)*(Dist);}
		else if(n>=12 && n<=15) {UpperCorId = 1.0502264-0.2814586*(Dist)+0.5533521*(Dist)*(Dist)-0.4811558*(Dist)*(Dist)*(Dist);}
		else {UpperCorId = 1.0346647-0.2549213*(Dist)+0.5642822*(Dist)*(Dist)-0.4857968*(Dist)*(Dist)*(Dist);}
    	if(UpperCorId>1){UpperCorId = 1;}
    	if(UpperCorId<0){UpperCorId = 0;}
    	return UpperCorId;
    }
	private double probCorrectIDLiberalLower(double Dist, int n){
    	//Probability of making a Correct ID when species present in reference data set	 
		double LowerCorId=0;
		if(n==1){LowerCorId = 0.8335041-0.7506823*(Dist);}
		else if(n==2) {LowerCorId = 0.8320471-0.626121*(Dist);}
		else if(n==3) {LowerCorId = 0.86437-0.574567*(Dist);}
		else if(n==4) {LowerCorId = 0.8696708-0.1632215*(Dist)-0.315303*(Dist)*(Dist);}
		else if(n==5||n==6) {LowerCorId = 0.87609353-0.05010266*(Dist)-0.3468219*(Dist)*(Dist);}
		else if(n==7||n==8) {LowerCorId = 0.947112-0.4514639*(Dist)+0.7268426*(Dist)*(Dist)-0.60278968*(Dist)*(Dist)*(Dist);}
		else if(n>=9 && n<=11) {LowerCorId = 0.9574939-0.3730159*(Dist)+0.6282986*(Dist)*(Dist)-0.5274369*(Dist)*(Dist)*(Dist);}
		else if(n>=12 && n<=15) {LowerCorId = 0.9657585-0.288033*(Dist)+0.5672532*(Dist)*(Dist)-0.4902748*(Dist)*(Dist)*(Dist);}
		else {LowerCorId = 0.9796573-0.2592032*(Dist)+0.5733348*(Dist)*(Dist)-0.4917353*(Dist)*(Dist)*(Dist);}
    	if(LowerCorId>1){LowerCorId = 1;}
    	if(LowerCorId<0){LowerCorId = 0;}
    	return LowerCorId;
	}

	private double Rosenberg_PAB(TreeNodeSet putativeSpecies, SpeciesDelimitation sd) {

		int a = putativeSpecies.getNumIndividuals();
		Node ancestor = putativeSpecies.getAncestor();

		this.speciesDelimitation = sd;
		Tree tree = sd.getTree();
		Set<Node> allnodes = new HashSet<Node>();
		allnodes = RootedTreeUtils.getDescendantTips((RootedTree) tree, ancestor);
		int n = allnodes.size();
		int b = n - a;
		double r_ab = (2 * factorial(a) * factorial(b))/(factorial(a+b) * (a+b-1));
		return r_ab;
	}

	//based on faulty logic
	@SuppressWarnings("unused")
	private double oldRosenberg_PAB(TreeNodeSet putativeSpecies, SpeciesDelimitation sd) {

		int a = putativeSpecies.getNumIndividuals();
		int b = 0;
		double r_ab = 0;
		
		Node ancestor = putativeSpecies.getAncestor();

		this.speciesDelimitation = sd;
		RootedTree tree = (RootedTree) sd.getTree();

		// Get a list of the members of the putative species
//		Set<Node> in_nodes = new HashSet<Node>();
//		in_nodes = RootedTreeUtils.getDescendantTips(tree, ancestor);

		// Get a list of the ancestor's children
		List<Node> childnodes = new ArrayList<Node>();
		childnodes = tree.getChildren(ancestor);

		if (childnodes.size() > 2){ //Not a binary split
			return Double.NaN;
		}
		else {
			Node putative_root = putativeSpecies.getRoot();
			for(Node c : childnodes){
				if(!c.equals(putative_root)){
					// child node is not putative species
					Set<Node> descendants = new HashSet<Node>();
					descendants = RootedTreeUtils.getDescendantTips((RootedTree) tree, c);
					for(int i=0; i<set.size(); i++){
						TreeNodeSet compare = set.get(i);
						Set<Node> species_leafNodes = compare.getLeafNodes();
						if(descendants.equals(species_leafNodes)){
							b = descendants.size();
						}
					}
					if (b < 1){
						return Double.NaN;
					}
					else{
						//calculate Rosenberg's PAB
						r_ab = (2 * factorial(a) * factorial(b))/(factorial(a+b) * (a+b-1));
					}
				}
			}
		}
		return r_ab;
	}
	
	// Evaluate n!
    public static double factorial( int n )
    {
        //-- BigInteger solution.
        BigInteger nfact = BigInteger.ONE;
        for (int i=1; i<=n; i++) {
            nfact = nfact.multiply(BigInteger.valueOf(i));
        }
        double nfactorial = nfact.doubleValue();
        return nfactorial;
    }
    
    
	public JPanel resultsPanel(Dimension dim){
		//ThreadUtilities.sleep(2000);
		detailsPanel = new JPanel(new BorderLayout());
    	detailsPanel.add(toJTable(0));
    	resultsPanel = new GPanel();
    	resultsPanel.setOpaque(true);
    	resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
    	JPanel putativeSpeciesGroup = new JPanel();
    	putativeSpeciesGroup.setLayout(new GridLayout(1,2));
    	putativeSpeciesGroup.add(new JLabel("Results for Species: "));
    	putativeSpeciesGroup.setBackground(Color.white);
    	putativeSpeciesGroup.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    	JPanel closestToPutativeGroup = new JPanel();
    	closestToPutativeGroup.setLayout(new GridLayout(1,2));
    	closestToPutativeGroup.setBackground(Color.white);
    	JLabel closestLabel = new JLabel("Closest Species: ");
    	closestLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    	closestToPutativeGroup.add(closestLabel);    	
    	
    	String[] groups = new String[set.size()];
    	for(int j=0; j< groups.length; j++){
			groups[j]=(set.get(j).getName());
    	}
    	
    	groupList = new GComboBox(groups);
    	final int i=groupList.getSelectedIndex();
    	final JTable groupResult;
    	if(i>0){
    		groupResult = toJTable(i);
    	}else{
    		groupResult = toJTable(0);
    	}
		groupResult.setName("groupResult");
       	groupList.setForeground(new Color(25,25,25));
    	groupList.setBackground(set.get(0).getColor());
    	groupList.setToolTipText(set.get(0).toTaxonNames(true));
    	//speciesDelimitation.fireSelection(new TreeSelectionChangeEvent(set.get(0).getNodes()));
    	groupList.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
           		try{
           			int curIndex=groupList.getSelectedIndex();
           		
	        		if(curIndex!=-1){
		           		curSet = set.get(curIndex);
		        		detailsPanel.removeAll();
		        		detailsPanel.add(toJTable(curIndex));
		        		speciesDelimitation.fireSelection(new TreeSelectionChangeEvent(curSet.getNodes()));
		        		groupList.setBackground(curSet.getColor());
		        		updateClosest(curSet);
		        		groupList.setFocusable(false);
		        		groupList.setToolTipText(curSet.toTaxonNames(true));
	        		}
           		}catch(Exception exc){
           			speciesDelimitation.restart();
           		}
        	}
		});
    
    	ComboBoxRenderer renderer2 = new ComboBoxRenderer(set);
    	groupList.setRenderer(renderer2);
    	putativeSpeciesGroup.add(groupList);
    	
    	TreeNodeSet putative = set.get(i);  
    	curSet=putative;
		interSpecificDist(putative);		
		closestGroup = new JButton(closest.getName());
		closestGroup.setBorder(BorderFactory.createLineBorder(Color.black));
		closestGroup.setBackground(closest.getColor());
		closestGroup.setToolTipText(closest.toTaxonNames(true));
		closestGroup.setForeground(new Color(25,25,25));
		closestGroup.setFocusable(false);
		closestGroup.addMouseListener(new MouseListener(){
			public void mousePressed(MouseEvent e){
				TreeNodeSet closestSet = set.get(closest.getGroupIndex()-1);
        		speciesDelimitation.fireSelection(new TreeSelectionChangeEvent(closestSet.getNodes()));
			}
			public void mouseReleased(MouseEvent e){}
			public void mouseEntered(MouseEvent e){}
			public void mouseExited(MouseEvent e){}
			public void mouseClicked(MouseEvent e){}
		});   	
		closestToPutativeGroup.add(closestGroup);
		resultsPanel.add(putativeSpeciesGroup);
		resultsPanel.add(closestToPutativeGroup);
		resultsPanel.add(detailsPanel);
		return resultsPanel;
    }
    public void updateClosest(TreeNodeSet putative){
    	interSpecificDist(putative);    	
    	closestGroup.setText(closest.getName());
		closestGroup.setBackground(closest.getColor());
		closestGroup.setToolTipText(closest.toTaxonNames(true));
    }
       
    public String toHTML(){
    	String html="";
    	html+= toTable()+"\n";
    	return html;
    }
    
    public GeneticDistances getGeneticDistances(){
    	if(geneticDistances!=null){
    		return geneticDistances;
    	}else{
    		return null;
    	}
    }
    
    public void displayGroup(int selectedGroup){
    	if(selectedGroup>-1){
    		groupList.setSelectedIndex(selectedGroup);
    	}
    }
	
}