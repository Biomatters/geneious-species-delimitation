package speciesDelimitation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import jebl.evolution.graphs.Node;
import jebl.evolution.taxa.Taxon;
import jebl.evolution.trees.RootedTree;
import jebl.evolution.trees.RootedTreeUtils;
import jebl.evolution.trees.Tree;

public class TreeNodeSet{
	/************************************************************/
	//Tree Node Set
	//Contains a set of nodes and information about a putative species.
	//When processing is carried out these groups are created as they are
	//easier to interact with than the Selection Set.
	/************************************************************/
	private Set<Node> set;
	private int index;
	private Color color;
	public String colorAttribute;
	public String name;
	private Tree tree;
	
 	public TreeNodeSet(Tree tree, Set<Node> set, int index, String attributeColor, String name){
		this.tree =tree;
 		this.colorAttribute= attributeColor;
		this.name = name;
		String[] colorTemp = null;
		colorTemp = colorAttribute.split(",");
		color = new Color(Integer.parseInt(colorTemp[0]),Integer.parseInt(colorTemp[1]),Integer.parseInt(colorTemp[2]));
 		this.set= new HashSet<Node>();
 		add(set);
		this.index=index;
	}
 	
 	public TreeNodeSet(Tree tree, int index, String attributeColor, String name){
		this.tree =tree;
 		this.colorAttribute= attributeColor;
		this.name = name;
		String[] colorTemp = null;
		colorTemp = colorAttribute.split(",");
		color = new Color(Integer.parseInt(colorTemp[0]),Integer.parseInt(colorTemp[1]),Integer.parseInt(colorTemp[2]));
 		this.set= new HashSet<Node>();
		this.index=index;
	}
 	
 	public int getIndex(){
 		return index;
 	}
 	
 	public void setIndex(int i){
 		this.index = i;
 	}
 	
 	public void updateNodeRef(Tree tree){
 		this.tree=tree;
	
 		Set<Node> testSet = new HashSet<Node>();
 		for(Node n: tree.getNodes()){
			if(n.getAttributeNames().contains("nodeColor")){
	 			if(n.getAttribute("nodeColor").equals(colorAttribute)){
					testSet.add(n);
				}
 			}
 		}
 		set.clear();
 		set.addAll(testSet);	
 	}
 	
 	public void add(Node n){
 		n.setAttribute("nodeColor", colorAttribute);
 		n.setAttribute("Grouping", name);
 		set.add(n);
 	}
 	
 	public void add(Set<Node> nodeSet){
 		for(Node n: nodeSet){
 			n.setAttribute("nodeColor", colorAttribute);
 			n.setAttribute("Grouping", name);
 			set.add(n);
 		}
 		
 	}

	public void setDelimitationResult(String res){
		for(Node node: set){
			node.setAttribute("DelimitationResult", res);
		}
	}

	public void setNumberedName(String name){
		this.name= name;
		for(Node n: set){
			n.setAttribute("Grouping", name);
		}
		setIndex(nameIndex());		
	}
	
	public void setName(String name){
		if(name.length()>0){
			this.name= (index)+": "+name;
			for(Node n: set){
				n.setAttribute("Grouping", this.name);
			}	
		}else{
			this.name= ""+(index);
			for(Node n: set){
				n.setAttribute("Grouping", this.name);
			}	
		}
	}

	public boolean hasConsensusSupport(SpeciesDelimitation sd, TreeNodeSet putative){
//		if(getPhyly().equals("Monophyletic") && (getRoot().getAttributeNames().contains("Consensus support(%)") || getRoot().getAttributeNames().contains("label"))){
//			if(getPhyly().equals("Monophyletic")){
		if(RootedTreeUtils.isMonophyletic((RootedTree) sd.getTree(), putative.getLeafNodes())){
			if(getRoot().getAttributeNames().contains("Consensus support(%)")){
				return true;
			}
		}
		return false;
	}

	public boolean hasBootstrap(SpeciesDelimitation sd, TreeNodeSet putative){
//		if(getPhyly().equals("Monophyletic") && (getRoot().getAttributeNames().contains("Consensus support(%)") || getRoot().getAttributeNames().contains("label"))){
//			if(getPhyly().equals("Monophyletic")){
		if(RootedTreeUtils.isMonophyletic((RootedTree) sd.getTree(), putative.getLeafNodes())){
			if(getRoot().getAttributeNames().contains("bootstrap proportion")){
				return true;
			}
		}
		return false;
	}

	public boolean hasLabel(SpeciesDelimitation sd, TreeNodeSet putative){
//		if(getPhyly().equals("Monophyletic")){
		if(RootedTreeUtils.isMonophyletic((RootedTree) sd.getTree(), putative.getLeafNodes())){
			if(getRoot().getAttributeNames().contains("label")){
				return true;
			}
		}
		return false;
	}

	public boolean hasCladesupport(SpeciesDelimitation sd, TreeNodeSet putative){
//		if(getPhyly().equals("Monophyletic") && getRoot().getAttributeNames().contains("Clade Support")){
		if(RootedTreeUtils.isMonophyletic((RootedTree) sd.getTree(), putative.getLeafNodes()) && getRoot().getAttributeNames().contains("Clade Support")){
			return true;
		}
		return false;
	}
	
	public double getCladesupport(){
		return Double.parseDouble(""+(getRoot().getAttribute("Clade Support")));	
	}
	
	public double getLabelValue(){
		return Double.parseDouble(""+(getRoot().getAttribute("label")));
	}
		
	public double getBootstrapValue(){
		return Double.parseDouble(""+(getRoot().getAttribute("bootstrap proportion")));
	}
	
	public double getConsensusSupportValue(){
		return Double.parseDouble(""+(getRoot().getAttribute("Consensus support(%)")));
	}
	
	public int getGroupIndex(){
		return index;
	}

	public int getSize(){
		return set.size();
	}

	public String getDisplayName(){
		int i = name.indexOf(":");
		if(i>-1){
			return name.substring(i+2);
		}else{
			return "";
		}
 	}
	
	public String getName(){
		return name;
 	}
 		 	
 	public int getNumIndividuals(){
 		Set<Node> ext = tree.getExternalNodes();
 		ext.retainAll(set);
 		return ext.size();
 	}
 	
 	public Node getRoot(){
		if(set.size()==1){
			for(Node one:set){
				return one;
			}
		}
		Set<Node> inv=getInverse();
		ArrayList<Node> roots = new ArrayList<Node>();
		for(Node node: set){
			for(Node adj: tree.getAdjacencies(node)){
				if(inv.contains(adj)){
					roots.add(node);
				}
			}
		}
		if(roots.size()>1){
			int lastDegree=((Node)roots.get(0)).getDegree();
			Node bestRoot = ((Node)roots.get(0));
			for(int i=1; i<roots.size(); i++){
				int degree = ((Node)roots.get(i)).getDegree();
				if(degree>lastDegree){
					lastDegree=degree;
					bestRoot = roots.get(i);
				}
			}
			return bestRoot;
		}else{
			return (Node)roots.get(0);
		}
		
	}

	public ArrayList<Node> getRoots(){
		Set<Node> inv=getInverse();
		ArrayList<Node> roots = new ArrayList<Node>();
		for(Node node: set){
			for(Node adj: tree.getAdjacencies(node)){
				if(inv.contains(adj)){
					roots.add(node);
				}
			}
		}
		return roots;
	}

	public Color getColor(){
 		return color;
 	}
 	public String getColorAttribute(){
 		return colorAttribute;
 	}
 	
 	public String getColorHex(){
 		return Integer.toHexString(color.getRGB() & 0x00ffffff);
 	}
 	
 	private Set<Node> getInverse(){
		Set<Node> invNodes = tree.getNodes();
		invNodes.removeAll(set);
		return invNodes;
	}

	public Set<Node> getNodes(){
		return set;
	}

	public Set<Node> getLeafNodes(){
		Set<Node> leafNodes = new HashSet<Node>();
		for(Node node: set){
			if(node.getDegree()==1){
				leafNodes.add(node);
			}
		}
		return leafNodes;
	}

	public Node getAncestor(){
		Node root = getRoot();
		if(root!=null){
			ArrayList<Node> ancestor = new ArrayList<Node>();
			ancestor.addAll(tree.getAdjacencies(root));
			ancestor.removeAll(set);
			return ancestor.get(0);			
		}
		return null;
	}

	public String getPhyly(){
		ArrayList<Node> roots = getRoots();
		//roots.removeAll(tree.getExternalNodes());
		if(roots.size()==1){
			if(getNumIndividuals()==1){
				return "Single Individual";
			}else{
				//Check Monophyly
				Set<Node> checkNodes = new HashSet<Node>();
				checkNodes.addAll(set);
				checkNodes.remove(roots.get(0));
				boolean monophyly =true;
				for(Node n: checkNodes){
					if(!set.containsAll(tree.getAdjacencies(n))){
						monophyly=false;
						break;
					}
				}
				if(monophyly){
					return "Monophyletic";
				}else{
					return "Paraphyletic";
				}
			}
		}else{
			//Check Paraphyly
//			boolean isParaphyly =false;
//			Set<Node> adjMatch = new HashSet<Node>();
//			adjMatch.addAll(tree.getAdjacencies(roots.get(0)));
//			for(int i=1; i<roots.size(); i++){
//				adjMatch.retainAll(tree.getAdjacencies(roots.get(i))); //addAll
//			}
//			if(adjMatch.size()>0){
//				isParaphyly = true;
//			}
//			if(isParaphyly){
//				return "Paraphyletic";
//			}else{
//				return "Polyphyletic";
//			}
//		}
			//Check Paraphyly
			boolean isParaphyly =false;
			Set<Node> adjMatch = new HashSet<Node>();
			adjMatch.addAll(tree.getNodes());
			for(int i=0; i<roots.size(); i++){
				adjMatch.retainAll(tree.getAdjacencies(roots.get(i))); //addAll
			}
			if(adjMatch.size()>0){
				isParaphyly = true;
			}
			if(isParaphyly){
				return "Paraphyletic";
			}else{
				return "Polyphyletic";
			}
		}	
	}
	
	public void removeAttributes(){
 		for(Node n: set){
 			n.removeAttribute("nodeColor");
 			n.removeAttribute("Grouping");
 			n.removeAttribute("DelimitationResult");
 		}
	}
	
 	public void remove(Set<Node> remove){
		set.removeAll(remove);
	}

	public void removeGroupings(){
		for(Node node: set){
			node.setAttribute("Grouping","");
		}
	}

	public void removeDelimitationResult(){
		for(Node node: set){
			node.removeAttribute("DelimitationResult");
		}
	}

	public double intraSpecificDist(GeneticDistances geneticDistances){
		if(set.size()==1){
 			return 0;
 		}
 		Set<Node> leafNodes = getLeafNodes();
 		Set<Node> leafNodesChecked = getLeafNodes();
 		double intraSpecificDist=0.0;
 		int count=0;
 		for(Node leaf: leafNodes){
 			leafNodesChecked.remove(leaf);
 			for(Node leaves: leafNodesChecked){
 				intraSpecificDist+=geneticDistances.path(leaf, leaves);
 				count++;
 			}
 		}
 		return (intraSpecificDist/count);	
 	}
	
 	public String groupColored(){
 		return "<font color="+getColorHex()+">"+index+"</font>";
 	}
 	
 	public String namesInSet(){
 		String names="";
 		for(Node node:getLeafNodes()){
 			names+=tree.getTaxon(node).getName()+"\n";
 		}
 		return names;
 	}
	
	public String toTaxonNames(boolean isSummary){
		String groupContents="<html> <b>This group contains:</b> ";
		int charCount = groupContents.length();
		int charMax = 100;
		for(Node node: set){	
			Taxon t=null;
			try{
				t= tree.getTaxon(node);
				if(t!=null){
					if(charCount<charMax){
						String taxonName = t.getName();
						charCount+= taxonName.length();
						groupContents+=taxonName+", ";
					}else{
						groupContents+="<br>";
						charCount=t.getName().length();
						groupContents+=t.getName();
					}
				}
			}catch(Exception e){
			}
		}
		return groupContents+"</html>";
	}
	
	public boolean isEqual(TreeNodeSet ns){
		return set.containsAll(ns.set);
	}
	
	public String toAttributes(){
		String result="";
		for(Node n:set){
			for(String s: n.getAttributeNames()){
				result+="Name:"+s+",Type:"+n.getAttribute(s).toString();				
			}
		}
	return result;
	}

	public boolean indexIsLowerThan(TreeNodeSet lowest) {
		if(nameIndex()<lowest.nameIndex()){
			return true;
		}else{
			return false;
		}
	}
	
	private int nameIndex(){
		if(name.indexOf(":")>-1){
			return Integer.parseInt(name.substring(0,name.indexOf(":")));
		}else{
			return Integer.parseInt(name);
		}
	}
}