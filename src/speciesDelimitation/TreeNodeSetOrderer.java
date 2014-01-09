package speciesDelimitation;

import java.util.LinkedList;

public class TreeNodeSetOrderer {
	
	LinkedList<TreeNodeSet> set;
	LinkedList<TreeNodeSet> newSet = new LinkedList<TreeNodeSet>();
	
	public TreeNodeSetOrderer(LinkedList<TreeNodeSet> set){
		this.set = set;
		order();
		renumberSetIndex();
	}
	
	public LinkedList<TreeNodeSet> getOrderedSet(){
		return newSet;
	}
	
	private void order(){
		
		int size = set.size();
		
		for(int i=0; i<size; i++){
			TreeNodeSet lowest =null;
			for(TreeNodeSet tns: set){
				if(lowest==null){
					lowest=tns;
				}else{
					if(tns.indexIsLowerThan(lowest)){
						lowest=tns;
					}
				}
			}
			newSet.add(lowest);
			set.remove(lowest);
		}	
	}
	
	private void renumberSetIndex(){
		int i= 1;
		for(TreeNodeSet tns: newSet){
			tns.setIndex(i);
			i++;
		}
	}
	
	
}
