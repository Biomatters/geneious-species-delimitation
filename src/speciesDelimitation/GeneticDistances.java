/************************************************************/
//Genetic Distances
//Traverse an entire tree document in order to find every node-to-node distance.
/************************************************************/
package speciesDelimitation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import jebl.evolution.graphs.Node;
import jebl.evolution.trees.Tree;

public class GeneticDistances{
	private ArrayList<Node> nodes;
	private double[][] dist;
	private Tree tree;
	private int n;
	
	public GeneticDistances(GeneticDistances gd){
		this.nodes = gd.nodes;
		this.dist = gd.dist;
		this.tree = gd.tree;
		this.n = gd.n;
	}

	public GeneticDistances(Tree tree){		
		this.tree=tree;
		nodes = new ArrayList<Node>();
		nodes.addAll(tree.getNodes());
		n = nodes.size();
		dist = new double[n][n];
		Node node = nodes.get(0);
		Set<Node> seen = new HashSet<Node>();
		seen.add(node);
		calculateConnectedPaths(node, seen);
		for(int i =0; i<n; i++){	
			Node node2 = nodes.get(i);
			Set<Node> frontier = new HashSet<Node>();
			frontier.add(node2);
			Set<Node> seen2 = new HashSet<Node>();
			calculateLongPaths(frontier,seen2, i,i,0);
		}
	}
	
	private void calculateConnectedPaths(Node n, Set<Node> seen){
		int indexN = nodes.indexOf(n);
		Set<Node> adjs = new HashSet<Node>();
		adjs.addAll(tree.getAdjacencies(n));
		adjs.removeAll(seen);
		seen.addAll(adjs);
		
		for(Node adj: adjs){	
			int indexAdj = nodes.indexOf(adj);
			double edgeLength=0;
			try{
				edgeLength= tree.getEdge(n, adj).getLength();
			}catch(Exception e){}
			dist[indexN][indexAdj] = edgeLength;
			dist[indexAdj][indexN] = edgeLength;
			calculateConnectedPaths(adj,seen);
		}
	}
	
	private void calculateLongPaths(Set<Node> frontier, Set<Node> seen, int origIndex,int lastIndex, double pathLength){
		seen.addAll(frontier);
		for(Node node: frontier){	
			int indexNode = nodes.indexOf(node);
			double edgeLength= dist[lastIndex][indexNode];
			double newPathLength = pathLength;
			newPathLength+=edgeLength;
			dist[origIndex][indexNode] = newPathLength;
			Set<Node> newFrontier = new HashSet<Node>();
			newFrontier.addAll(tree.getAdjacencies(node));
			newFrontier.removeAll(seen);
			if(!newFrontier.isEmpty()){
				calculateLongPaths(newFrontier, seen, origIndex, indexNode, newPathLength);
			}
		}
	}

	public double path(Node n1, Node n2){
		try{
		int n1Index = nodes.indexOf(n1);
		int n2Index = nodes.indexOf(n2);
		return dist[n1Index][n2Index];
		}catch(Exception e){
			return 0;
		}
	}
	
	public String toTable(boolean taxaConstrained){
//Timing execution - start
//		long startTime = System.currentTimeMillis();	
//
		StringBuffer buftable = new StringBuffer();
		buftable.append("<b>Tree Distance Matrix</b>\n<table BORDER=1>");
		if(taxaConstrained){
			ArrayList<Node> taxaNodes = new ArrayList<Node>();
			taxaNodes.addAll(tree.getNodes());
			for(Node n: tree.getNodes()){
				if(n.getDegree()>1){
					taxaNodes.remove(n);
				}
			}
			
			buftable.append("<tr>");
			buftable.append("<td><CENTER></td>");
			for(int k=0; k< taxaNodes.size(); k++){
				String groupName ="";
				if(taxaNodes.get(k).getAttributeNames().contains("GroupName")){
					groupName=(String)(taxaNodes.get(k).getAttribute("GroupName"));
				}
				
				if(groupName.equals("null") || groupName.length()==0){
					groupName="";
				}else{
					groupName=groupName+=": ";
				}
				buftable.append("<td><CENTER>"+groupName+tree.getTaxon(taxaNodes.get(k)).getName()+"</td>");
   			}
			buftable.append("</tr>");
			
			for(int i=0; i< taxaNodes.size(); i++){
				buftable.append("<tr>");
				String groupName ="";
				if(taxaNodes.get(i).getAttributeNames().contains("GroupName")){
					groupName=(String)(taxaNodes.get(i).getAttribute("GroupName"));
				}
				
				if(groupName.equals("null") || groupName.length()==0){
					groupName="";
				}else{
					groupName=groupName+=": ";
				}
				buftable.append("<td><CENTER>"+groupName+tree.getTaxon(taxaNodes.get(i)).getName()+"</td>");			
				for(int j=0; j< taxaNodes.size(); j++){
					double path = path(taxaNodes.get(i), taxaNodes.get(j));
					String pathView = ""+path;
					if(pathView.length()>5){
						pathView = pathView.substring(0, 5);
					}
					buftable.append("<td><CENTER>"+pathView+"</td>");
	   			}
				buftable.append("</tr>");
			}	
			
		}else{
			for(int i=0; i< n; i++){
				buftable.append("<tr>");
				for(int j=0; j< n; j++){
					buftable.append("<td><CENTER>"+dist[i][j]+"</td>");
	   			}
				buftable.append("</tr>");
			}	
		}
		buftable.append("</table>\n");
 
		String table = buftable.toString();

//Timing execution - finish
//				long elapsedTime = System.currentTimeMillis() - startTime;
//				System.out.println ("GeneticDistances.toTable Elapsed time = " + elapsedTime + " ms");		
//
		return table;
    }
	

}