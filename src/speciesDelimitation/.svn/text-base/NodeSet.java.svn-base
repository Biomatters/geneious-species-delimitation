package speciesDelimitation;

import java.util.HashSet;
import java.util.Set;

import jebl.evolution.graphs.Node;

public class NodeSet {
	private String name;
	private int num;
	private HashSet<Node> set;
	private String color;
	
	public NodeSet(int num, String name, String color,Set<Node> set){
		this.num = num;
		this.name = name;
		this.color = color;
		this.set = new HashSet<Node>();
		this.set.addAll(set);
		for(Node n: set){
			n.setAttribute("nodeColor", color);
			n.setAttribute("GroupName", getDisplayName());
		}
	}
	
	public String getDisplayName(){
		return num+": "+name;
	}
	public String getName(){
		return name;
	}
	
	public int getNum(){
		return num;
	}
	
	public String getColor(){
		return color;
	}
	
	public HashSet<Node> getSet(){
		return set;
	}
	
	public void clear(){
		for(Node n: set){
			n.removeAttribute("nodeColor");
			n.removeAttribute("GroupName");
		}
	}
}
