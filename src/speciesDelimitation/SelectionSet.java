package speciesDelimitation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import jebl.evolution.graphs.Node;
import jebl.evolution.trees.Tree;
import speciesDelimitation.ColorChooser;
import speciesDelimitation.TreeNodeSet;


public class SelectionSet{
		/************************************************************/
		//Selection Set
		//Is able to hold all the node group information and names that have
		//been assigned to these groups until the results need to be processes.
		/************************************************************/
		private SpeciesDelimitation sd;
		private String lastColor;
		private LinkedList<TreeNodeSet> set;
		private ColorChooser colorChooser;
		private TreeNodeSet latestTNS;
				
		public SelectionSet(SpeciesDelimitation sd){
			this.sd = sd;
			set = new LinkedList<TreeNodeSet>();
			colorChooser = new ColorChooser();
			if(sd.getTree()!=null){
				initialiseWithTree(sd.getTree());
			}
		}
		
		public TreeNodeSet getLastSet(){
			return latestTNS;
		}
		
		public void initialiseWithTree(Tree tree){
			//Checks tree for groups already created previously to use of the SpeciesDelimitation Program.
			//Groups that have same colour and the same names. All others are discarded.
			LinkedList<String> setColorAttributes = new LinkedList<String>();
			Set<Node> nodes = new HashSet<Node>();
			nodes.addAll(sd.getTree().getExternalNodes());
			
			for(Node n: nodes){
				if(n.getAttributeNames().contains("nodeColor")){
					String newColor = n.getAttribute("nodeColor").toString();
					if(!newColor.equals("0,0,0")){
						if(!setColorAttributes.contains(newColor) && n.getAttributeNames().contains("Grouping")){
							setColorAttributes.add(newColor);
						}
					}
				}
			}
			
			for(Node n: nodes){
				if(n.getAttributeNames().contains("nodeColor")){
					String newColor = n.getAttribute("nodeColor").toString();
					if(!newColor.equals("0,0,0")){
						if(!setColorAttributes.contains(newColor)){
							setColorAttributes.add(newColor);
						}
					}
				}
			}
			
			int numberOfColourGroups = setColorAttributes.size();
			
			set = new LinkedList<TreeNodeSet>();
			
			for(int i=0; i<numberOfColourGroups; i++){
				String color = setColorAttributes.get(i);
							
				TreeNodeSet tns = new TreeNodeSet(tree, i+1, color, ""+i+1);
				
				Set<String> names = new HashSet<String>();
				Set<Node> nodeGroup = new HashSet<Node>();
				for(Node n: nodes){
					if(n.getAttributeNames().contains("nodeColor")){
						String thisColor=(String) n.getAttribute("nodeColor");
						if(thisColor.equals(color)){
							nodeGroup.add(n);
							if(n.getAttributeNames().contains("Grouping")){
								String thisGrouping=(String) n.getAttribute("Grouping");
								names.add(thisGrouping);
							}
						}
					}
					
				}
				
				if(!names.isEmpty()){
		    		for(String thisName: names){
		    			tns.setNumberedName(thisName);
		    			break;
					}
				}else{
					tns.setName("");
				}
	    		
				tns.add(extendSet(nodeGroup));
				colorChooser.removeColors(setColorAttributes);
				set.add(tns);
			}
			
			TreeNodeSetOrderer tnso = new TreeNodeSetOrderer(set);
			set.clear();
			set.addAll(tnso.getOrderedSet());
		}
							
		public TreeNodeSet getTreeNodeSet(int i){
			return (TreeNodeSet)set.get(i);
		}
			
		public void addSet(Set<Node> selection, String name){		
			LinkedList<String> curColorAttributes = new LinkedList<String>();
			for(Node n: sd.getTree().getNodes()){
				if(n!=null){
					if(n.getAttributeNames().contains("nodeColor")){
						String color = n.getAttribute("nodeColor").toString();
						curColorAttributes.add(color);
					}
				}
			}
			colorChooser.removeColors(curColorAttributes);
			lastColor = colorChooser.getColor();
			if(name.length()>0){
				latestTNS = new TreeNodeSet(sd.getTree(),extendSet(selection),size()+1,lastColor,size()+1+": "+name);
			}else{
				latestTNS = new TreeNodeSet(sd.getTree(),extendSet(selection),size()+1,lastColor,""+(size()+1));
			}
			set.add(latestTNS);
			fireTreeChange(sd.getTree());
			updateTreeNodeReferences();
		}
		
		public void updateTreeNodeReferences(){
			for(TreeNodeSet tns: set){
				tns.updateNodeRef(sd.getTree());
			}
		}
		
		public void removeGroup(int i){
			set.get(i).removeAttributes();
			set.remove(i);
			for(TreeNodeSet tns: set){
				if(tns.getIndex()>i){
					tns.setIndex(tns.getIndex()-1);
					String name = tns.getName();
					
					int ind = name.indexOf(":");
					if(ind>0){
						tns.setNumberedName(tns.getIndex() +": "+ name.substring(ind+1));
					}else{
						tns.setNumberedName(tns.getIndex()+"");
					}
				}
			}
			fireTreeChange(sd.getTree());
			updateTreeNodeReferences();
		}
		
		
		private Set<Node> extendSet(Set<Node> set){
			boolean found = false;
			Set<Node> setAdjacencies = new HashSet<Node>();
			for(Node n: set){
				setAdjacencies.addAll(sd.getTree().getAdjacencies(n));
			}
			setAdjacencies.removeAll(set);
			for(Node n: setAdjacencies){
				Set<Node> nodeAdjacencies = new HashSet<Node>();
				boolean add =true;
				nodeAdjacencies.addAll(sd.getTree().getAdjacencies(n));
				for(Node adj: nodeAdjacencies){
					if(adj.getDegree()==1){
						if(!set.contains(adj)){
							add=false;
						}
					}
				}
				nodeAdjacencies.retainAll(set);
				
				if(nodeAdjacencies.size()>1){
					
					if(add){
						set.add(n);
						found=true;
					}
				}
			}
			if(found){
				set = extendSet(set);
			}			
			return set;	
		}
			
		public int size(){
			return set.size();
		}
		
		public void reset(){
			for(Node node: sd.getTree().getNodes()){
				node.removeAttribute("Grouping");
				node.removeAttribute("GroupName");
				node.removeAttribute("nodeColor");
			}
			set.clear();
			colorChooser.reset();
			fireTreeChange(sd.getTree());
		}
						
		public Set<Node> allNodesInSet(){
			Set<Node> totalSelection = new HashSet<Node>();
			for(TreeNodeSet tns: set){
				totalSelection.addAll(tns.getNodes());
			}
			return totalSelection;
		}

		public void fireTreeChange(Tree t){
		    sd.fireTree(t);
		}
			
		public ColorChooser getColorChooser(){
			return colorChooser;
		}
		
		public ArrayList<TreeNodeSet> getTreeNodeSets(){
			ArrayList<TreeNodeSet> newList = new ArrayList<TreeNodeSet>();
			newList.addAll(set);
			return newList;
		}
		
		public boolean contains(Node n){
			if(allNodesInSet().contains(n)){
				return true;
			}
			return false;
		}
		
		public boolean contains(Set<Node> set){
			Set<Node> nodes = new HashSet<Node>();
			nodes.addAll(allNodesInSet());
			nodes.retainAll(set);
			if(nodes.size()>0){
				return true;
			}else{
				return false;
			}
		}

		public TreeNodeSet selectionAsExisitingTNS(Set<Node> selectedNodes) {
			for(TreeNodeSet tns: set){
				
				if(tns.getNodes().equals(extendSet(selectedNodes))){
					return tns;
				}
			}
			return null;
		}
		
	}