package speciesDelimitation;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import jebl.evolution.graphs.Node;
import jebl.evolution.trees.Tree;

public class Coalescence{
	/************************************************************/
	//Calculates the probability that a putative species appears to be 
	//a distinct clade due to the random process of the coalescent.
	/************************************************************/
	Node root;
	int[] coalescentRank;
	ArrayList<Node> sortedNodes;
	private Tree tree;
	private GeneticDistances geneticDistances;
	
	public Coalescence(Tree tree, GeneticDistances geneticDistances){
		this.geneticDistances = geneticDistances;
		this.tree = tree;
		this.root = findTreeRoot();
		ArrayList<Node> unsortedNodes = new ArrayList<Node>();
		unsortedNodes.addAll(tree.getInternalNodes());		
		sortedNodes = sortNodesByDist(root,unsortedNodes);
		coalescentRank = findRank(sortedNodes);
	}
	
	public double prob(TreeNodeSet putativeSpecies, double m){   		
		if(putativeSpecies.getNumIndividuals()==1){
			return 0;
		}
		
		int indexA = sortedNodes.indexOf(putativeSpecies.getAncestor());
		int a = coalescentRank[indexA];
		int indexX = sortedNodes.indexOf(putativeSpecies.getRoot());
		int x = coalescentRank[indexX];
		int n = tree.getExternalNodes().size();
		if(n>40){
			n=40;
			if(x>40){
    			x=40;
    		}
		}
		//TERM1
		BigInteger term1 = BigInteger.ONE;
		for(int i=a+1; i<=n; i++){
			BigInteger lambda = lambda(i);
			term1 = term1.multiply(lambda);
		}   		
		//LOOP
		BigDecimal outerLoop = BigDecimal.ZERO;
		for(int j=a+1; j<=x; j++){
			BigDecimal innerLoop = BigDecimal.ZERO;
			for(int h=x+1; h<=n; h++){
				innerLoop = innerLoop.add(ratioTerm(m,a,j,h,x,n));
			}
			
			outerLoop = outerLoop.add(innerLoop);
		}
		BigDecimal term1Dec = new BigDecimal(term1);
		BigDecimal result = term1Dec.multiply(outerLoop);
		return result.doubleValue();
	}
	
	public Node findTreeRoot(){
		Set<Node> treeNodes = new HashSet<Node>();
		treeNodes = tree.getNodes();
		Node root =null;
		for(Node n: treeNodes){
			if(n.getDegree()==2){
				root = n;
				break;
			}
		}
		if(root==null){
			for(Node n: treeNodes){
				root = n;
				break;
			}
		}
		return root;
	}
	
	public double round(double value, int decimalPlace){
	    double power_of_ten = 1;
	    while (decimalPlace-- > 0){
	       power_of_ten *= 10.0;
	    }
	    return Math.round(value * power_of_ten) / power_of_ten;
	}

	
	public int[] findRank(ArrayList<Node> sortedNodes){

		int[] rank = new int[sortedNodes.size()];
		double latestPath =round(geneticDistances.path(root,sortedNodes.get(0)),5);
		rank[0]=1;
		int nextRank=2;
		int multiStart=0;		
		for(int i=1; i<sortedNodes.size(); i++){
			double path = round(geneticDistances.path(root,sortedNodes.get(i)),5);
			String shorterPath = ""+path;
			if(shorterPath.length()>8){
				shorterPath = shorterPath.substring(0,8);
			}
			path = Double.parseDouble(shorterPath);
			if(path!=latestPath){
				multiStart=i;
				rank[i]= nextRank;
				nextRank++;
				latestPath=path;
			}else{
				if(i==sortedNodes.size()-1){
					for(int l=multiStart; l<=i; l++){
						rank[l]=sortedNodes.size();
					}
					break;
				}
				Set<Node> multiAdjs = new HashSet<Node>();
				for(int j=multiStart; j<=i; j++){
					multiAdjs.addAll(tree.getAdjacencies(sortedNodes.get(j)));
				}
				for(int k=multiStart; k<=i; k++){
					multiAdjs.remove(sortedNodes.get(k));
				}
				int result = multiAdjs.size()-2;
				for(int l=multiStart; l<=i; l++){
					rank[l]=multiStart+result;
				}
				nextRank=multiStart+result+1;
			}
		}
		return rank;
	}
	
	private BigInteger lambda(int r){
		BigInteger res = BigInteger.valueOf((long)r);
		res = res.multiply(BigInteger.valueOf((long)(r-1)));
		return res;
	}

	private BigDecimal ratioTerm(double m, int a, int j, int h, int x, int n){
		BigDecimal top = new BigDecimal(m);
		BigDecimal divisor = new BigDecimal(m);
		divisor = divisor.multiply(new BigDecimal(lambda(h)));
		divisor = divisor.add(new BigDecimal(lambda(j)));
		divisor = divisor.multiply(new BigDecimal(lambda(j)));
		BigInteger t2 = BigInteger.ONE;
		for(int k=a+1; k<=x; k++){
			if(k!=j){
				BigInteger t2lamda = lambda(k);
				t2lamda = t2lamda.subtract(lambda(j));
				t2 = t2.multiply(t2lamda);
			}
		}
		BigInteger t3 = BigInteger.ONE;
		for(int q=x+1; q<=n; q++){
			if(q!=h){
				BigInteger t3lamda = lambda(q);
				t3lamda=t3lamda.subtract(lambda(h));
				
				t3 = t3.multiply(t3lamda);
			}
		}
		BigInteger t23 = t2.multiply(t3);
		BigDecimal t23Dec = new BigDecimal(t23);
		divisor = divisor.multiply(t23Dec);
		top = top.divide(divisor,100,BigDecimal.ROUND_HALF_EVEN);
		return top;
	}
	
	private ArrayList<Node> sortNodesByDist(Node root, ArrayList<Node> nodes){
		int size = nodes.size();
		
		ArrayList<Node> sortedNodes = new ArrayList<Node>();
		boolean complete=false;
		while(!complete){
			if(size==sortedNodes.size()){
				complete=true;
				break;
			}
			double smallestDist=999999;
			int indexSmallestDist=0;
			for(int i=0; i<nodes.size();i++){
				Node n = nodes.get(i);
				if(!sortedNodes.contains(n)){
					double dist = geneticDistances.path(root,n);
					if(dist<smallestDist){
						smallestDist=dist;
						indexSmallestDist=i;
					}
				}
			}
			sortedNodes.add(nodes.get(indexSmallestDist));
		}
		return sortedNodes;
	}
}