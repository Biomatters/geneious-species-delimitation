/************************************************************/
//Species Delimitation
//Provides all interactions to be carried out with the TreeViewerPanel
//Produces the display for the MainPanel the the user may interact with.
//Subclasses include: ResultsTable,Coalescence,SelectionSet,TreeNodeSet and the ComboBoxRenderer
/************************************************************/
package speciesDelimitation;

import com.biomatters.geneious.publicapi.components.*;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentViewer;
import com.biomatters.geneious.publicapi.plugin.TreeViewerExtension;
import jebl.evolution.graphs.Node;
import jebl.evolution.trees.Tree;
import jebl.evolution.trees.TreeChangeEvent;
import jebl.evolution.trees.TreeSelectionChangeEvent;
import jebl.evolution.trees.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class SpeciesDelimitation extends TreeViewerExtension implements Runnable{
    private AnnotatedPluginDocument annotatedPluginDocument;
    private DocumentViewer treeViewer;
    private Tree tree;
    private int numTrees;
    private Set<Node> selectedNodes=new HashSet<Node>();
    private JPanel panel;
    private SelectionSet selectionSet;
    private ResultsTable resultsTable;
    private final Dimension DIMENSION;
    private GComboBox groupList;
    private boolean loaded = false;
    private GPanel functionText;
    private GPanel functionAdd;
    private GPanel functionRemove;
    private GPanel functionReset;
    private GPanel functionResults;
    private GPanel functionSave;
    private GPanel functionRename;
    private GPanel functionSendManual;
    private GTextArea text;
    private JTextField renameField;
    private boolean internalFire = true;
	private GPanel resetAndReload;
	private GPanel functionReload;
    
	public SpeciesDelimitation(DocumentViewer treeViewer,AnnotatedPluginDocument annotatedPluginDocument) {	
  	    	
    	selectionSet = new SelectionSet(this);	
    	groupList = new GComboBox(new String[0]);
    	this.annotatedPluginDocument = annotatedPluginDocument;
    	this.treeViewer = treeViewer;
    	
        panel = new GPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        functionText = new GPanel(new GridBagLayout());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        panel.add(functionText, c);
        
        functionAdd = new GPanel(new GridBagLayout());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        panel.add(functionAdd, c);
        functionAdd.setVisible(true);
        
        functionRename = new GPanel(new GridBagLayout());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        panel.add(functionRename, c);
        functionRename.setVisible(false);

        functionRemove = new GPanel(new GridBagLayout());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        panel.add(functionRemove, c);

        functionResults = new GPanel(new GridBagLayout());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 6;
        c.gridx = 0;
        c.gridy = 3;
        panel.add(functionResults, c);
        
       
        resetAndReload = new GPanel(new GridBagLayout());
        c.ipady = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 4;
        panel.add(resetAndReload, c);
        
        GridBagConstraints c2 = new GridBagConstraints();
        
        functionReset = new GPanel(new GridBagLayout());
        c2.ipady = 0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.gridx = 0;
        c2.gridy = 0;
        c2.weightx = 0.5;
        resetAndReload.add(functionReset, c2);
        
        functionReload = new GPanel(new GridBagLayout());
        c2.ipady = 0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.gridx = 1;
        c2.gridy = 0;
        c2.weightx = 0.5;
        resetAndReload.add(functionReload, c2);
        
        functionSave = new GPanel(new GridBagLayout());
        c.ipady = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 5;
        panel.add(functionSave, c);
        functionSave.setVisible(false);
        
        functionSendManual = new GPanel(new GridBagLayout());
        c.ipady = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 6;
        panel.add(functionSendManual, c);
 
        DIMENSION = panel.getSize();
        run();
    }
    
    public AnnotatedPluginDocument getAnnotatedPluginDoc(){
    	return annotatedPluginDocument;
    }
    
    public ResultsTable getResultsTable(){
    	return resultsTable;
    }
    
    public String getPanelTitle() {
        return "Species Delimitation";
    }

    public JPanel getPanel() {
        return panel;
    }
    
    public void fireTree(Tree tree){
    	Tree newTree= Utils.copyTree(Utils.rootTheTree(tree));	
    	fireTreeChanged(new TreeChangeEvent(newTree));
    	fireSelection(new TreeSelectionChangeEvent(selectedNodes));
    }
    
	private void refreshTree(){
		loaded = false;
		fireTree(tree);
	}

    public void treeChanged(TreeChangeEvent treeChangeEvent) {
    	//textDisplay(Math.random()+"");
    	tree=treeChangeEvent.getTree();
    	if(loaded!=true){
	    	if(tree!=null){
				for(Node n:tree.getNodes()){
					if(n.getAttributeNames().contains("nodeColor")){
						loaded=true;
						break;
					}
				}
			}
			if(loaded){
				selectionSet = new SelectionSet(this);
				if(selectionSet.size()>0){
					setUpFunctionRemove();
					setUpFunctionResults();
				}
			}
		}
    }
    
    public void fireSelection(TreeSelectionChangeEvent treeChangeEvent){
    	fireSelectionChanged(treeChangeEvent);
    	internalFire=false;
    }
    
    public Tree getTree(){
    	return tree;
    }
    
    public void selectionChanged(TreeSelectionChangeEvent treeChangeEvent) {
    	selectedNodes=treeChangeEvent.getSelectedNodes();
    	try{
	    	if(internalFire==true && selectionSet!=null){
		    	TreeNodeSet selectionTreeNodeSet = selectionSet.selectionAsExisitingTNS(selectedNodes);
		    	if(selectionTreeNodeSet!=null){
		    		if(resultsTable!=null && selectionSet.size()>1){
		    			resultsTable.displayGroup(selectionTreeNodeSet.getIndex()-1);
		    			groupList.setSelectedIndex(selectionTreeNodeSet.getIndex()-1);
		    			//Change the Add Nodes button to a rename button for the selected set.
		    			if(renameField!=null){
		    				String displayName = selectionTreeNodeSet.getDisplayName();
		    				if(displayName.equals(selectionTreeNodeSet.getIndex()+"")){
		    					renameField.setText("");
		    				}else{
		    					renameField.setText(selectionTreeNodeSet.getDisplayName());
		    				}
		    			}
		    			showAddSelection(false);
		    			updateSets();
		    		}
		    	}else{
		    		textDisplay("This selection is not a Species Group");
		    		showAddSelection(true);
		    	}
	    	}
	    	internalFire=true;
    	} catch(Exception e) {
            //this is really gross, but it is quite hard to fix properly
            e.printStackTrace();
    	}
    }

    private void showAddSelection(boolean showAdd){
    	if(showAdd){
    		//Show add selection
    		//functionResults.setVisible(false);
    		functionRemove.setVisible(false);
    		functionRename.setVisible(false);
    		functionAdd.setVisible(true);
    	}else{
    		//Show Rename
    		//functionResults.setVisible(true);
    		//Instead change such that results are present but not for a species
    		functionRemove.setVisible(true);
    		functionRename.setVisible(true);
    		functionAdd.setVisible(false);
    	}
    }
    
	public void run(){
		loaded = false;
		setUpFunctionText();
		setUpFunctionAdd();
		setUpFunctionRemove();
		setUpFunctionReset();
//		setUpFunctionResults();
		setUpFunctionSave();	
		setUpFunctionRename();
		setUpFunctionReload();
		setUpFunctionSendManual();
		if(selectionSet.size()>0){
			//Disable root and swap siblings
    		treeViewer.getActionProvider().getOtherActions().get(0).setEnabled(false);
    		treeViewer.getActionProvider().getOtherActions().get(1).setEnabled(false);
		}
		numTrees = numberOfTrees();
	}

	private void setUpFunctionResults() {
		functionResults.removeAll();
		JPanel results = new JPanel();
		if(selectionSet.size()>1){
   			resultsTable = new ResultsTable(selectionSet.getTreeNodeSets(),this);
   			results = resultsTable.resultsPanel(DIMENSION);
   	        functionSave.setVisible(true);
   		}else{
   			JTextField resultsText = new JTextField("Results: Require 2 or more Groups");
   			resultsText.setEditable(false);
   			resultsText.setOpaque(false);
   			resultsText.setFocusable(false);
   			resultsText.setBorder(null);
   			resultsText.setHorizontalAlignment(JTextField.CENTER);
   			resultsText.setPreferredSize(new Dimension(resultsText.getPreferredSize().width,resultsText.getPreferredSize().height));
   			results.add(resultsText);
   		}
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 0;
        c.gridy = 0;
		functionResults.add(new JLabel("Results"),c);
		c.gridx = 0;
        c.gridy = 1;
        functionResults.add(results,c);
		functionResults.setPreferredSize(new Dimension(results.getPreferredSize().width,results.getPreferredSize().height));
		
	}
	
	private void setUpFunctionReset() {
		final GButton reset = new GButton("Reset");
		reset.setToolTipText("Remove all grouping and colors from your tree.");
		reset.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		selectionSet.reset();
    			selectionSet.getColorChooser().reset();
    			setUpFunctionRemove();
    			setUpFunctionResults();
    			updateSets();
    			functionSave.setVisible(false);
        	}
		});
		GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
		functionReset.add(reset,c);
		setUpFunctionRemove();
		//Enable root and swap siblings
	}
	
	private void setUpFunctionReload(){
		final GButton reload = new GButton("Reload Tree");
		reload.setToolTipText("Reload tree such that any changes made via node colouring are represented in the results.");
		reload.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		refreshTree();
        		
    			updateSets();
        	}
		});
		GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
		functionReload.add(reload,c);		
	}
	
	private void setUpFunctionRemove() {
		functionRemove.removeAll();
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth= 1;
		String[] groups = new String[selectionSet.size()];
		for(int j=0; j< groups.length; j++){
			groups[j]=selectionSet.getTreeNodeSet(j).getName();
    	}
		groupList = new GComboBox(groups);
		groupList.setVisible(false);
    	groupList.setForeground(new Color(25,25,25));
        groupList.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                int curIndex=groupList.getSelectedIndex();
                if(curIndex>-1){
                    TreeNodeSet curSet = selectionSet.getTreeNodeSet(curIndex);
                    fireSelectionChanged(new TreeSelectionChangeEvent(curSet.getNodes()));
                    groupList.setBackground(curSet.getColor());
                    groupList.setFocusable(false);
                    groupList.setToolTipText(selectionSet.getTreeNodeSet(groupList.getSelectedIndex()).toTaxonNames(false));
                }else{
                    groupList.setBackground(Color.WHITE);
                    groupList.setFocusable(false);
                }
            }
        });
    	if(groups.length>0){
    		final ComboBoxRenderer renderer = new ComboBoxRenderer(selectionSet.getTreeNodeSets());
    		groupList.setRenderer(renderer);
    		groupList.setBackground(selectionSet.getTreeNodeSet(groupList.getSelectedIndex()).getColor());
    		groupList.setToolTipText(selectionSet.getTreeNodeSet(groupList.getSelectedIndex()).toTaxonNames(false));

    	}	
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        
		functionRemove.add(groupList,c);
		
		final GButton remove = new GButton("Remove");
		remove.setToolTipText("Remove the selected group.");
		remove.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
           		if(groupList.getSelectedIndex()!=-1){      			
	        		selectionSet.removeGroup(groupList.getSelectedIndex());
	        		updateSets();
          		}
        		setUpFunctionRemove();
        		setUpFunctionResults();
        	}
    	});

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.gridx = 1;
        c.gridy = 0;
        functionRemove.add(remove,c);
        functionRemove.setPreferredSize(new Dimension(functionRemove.getPreferredSize().width,functionRemove.getPreferredSize().height));
	}
		
	public void restart(){
		loaded=false; 				
		fireSelectionChanged(new TreeSelectionChangeEvent(new HashSet<Node>()));
		updateSets();
		setUpFunctionRemove();
		setUpFunctionResults();

	}
	
	private void setUpFunctionAdd() {
		GridBagConstraints c = new GridBagConstraints();
		final JTextField name = new JTextField(14);
		name.setDocument(new JTextFieldLimit(14));
		name.setToolTipText("<html>Set node attribute with this Groups name.<br> Default name is based on a numerical ording of nodes added.</html>");
		name.setRequestFocusEnabled(true);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        functionAdd.add(name,c);
        
        final GButton add = new GButton("Add Selection");
        add.setToolTipText("Adds the selected nodes as a group");
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
    	add.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if(selectionSet!=null){
                    if(selectedNodes.size()>0){
                        //Disable root and swap siblings
                        boolean alreadyInSet= selectionSet.contains(selectedNodes);
                        if(!alreadyInSet){
                            selectedNodes.removeAll(selectionSet.allNodesInSet());
                            selectionSet.addSet(selectedNodes,name.getText());
                            name.setText("");
                            setUpFunctionResults();
                            updateSets();
                        }else{
                            textDisplay("Use only ungrouped nodes for new Groups.");
                        }
                        setUpFunctionRemove();
                        setUpFunctionResults();
                        if(!alreadyInSet){
                            internalFire=true;
                            TreeNodeSet lastSet = selectionSet.getLastSet();
                            if (lastSet != null) {
                                selectionChanged(new TreeSelectionChangeEvent(lastSet.getNodes()));
                            }
                        }

                    }
                }
            }

			
		});
    	functionAdd.add(add,c);
    	functionAdd.setPreferredSize(new Dimension(functionAdd.getPreferredSize().width,functionAdd.getPreferredSize().height));
  
	}
		
	private void setUpFunctionRename() {
		GridBagConstraints c = new GridBagConstraints();
		renameField = new JTextField();
		renameField.setDocument(new JTextFieldLimit(14));
		renameField.setRequestFocusEnabled(true);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        functionRename.add(renameField,c);
        
        final GButton rename = new GButton("Rename Selection");
        rename.setToolTipText("Renames the selected Group");
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        rename.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
                if(selectionSet!=null){
                    TreeNodeSet selectionTreeNodeSet = selectionSet.selectionAsExisitingTNS(selectedNodes);
                    if(selectionTreeNodeSet!=null){
                        selectionTreeNodeSet.setName(renameField.getText());
                        renameField.setText("");
                        setUpFunctionRemove();
                        setUpFunctionResults();
                        fireTree(tree);
                        selectionSet.updateTreeNodeReferences();
                        internalFire=true;
                        selectionChanged(new TreeSelectionChangeEvent(selectionTreeNodeSet.getNodes()));
                        showAddSelection(false);
                    }
                }
            }
        });
    	functionRename.add(rename,c);
    	functionRename.setPreferredSize(new Dimension(functionRename.getPreferredSize().width,functionRename.getPreferredSize().height));
  
	}

	private void setUpFunctionText() {
		text = new GTextArea("Add Groups to Carry out Diagnostics");
		text.setFont(new Font("sansserif",Font.PLAIN,12));
		text.setOpaque(false);
		text.setFocusable(false);
		text.setEditable(false);
		/*GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
		functionText.add(debug,c);*/
	}
	
	private void textDisplay(String string) {
		text.setText(string);
	}
	
	private void updateSets() {
		text.setText("Sets: "+selectionSet.size());
	}
	
	private void setUpFunctionSave(){
		GridBagConstraints c = new GridBagConstraints();
		final GButton save = new GButton("Save SpDelim Results");
		save.setToolTipText("<html>Save the current Species Delimitation information<br></html>");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
        c.gridx = 1;
        c.gridy = 0;
		save.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		if(numTrees==1){
        			save(e);
        		}else{
        			Dialogs.DialogOptions opt = new Dialogs.DialogOptions(Dialogs.YES_NO, "Save");
        			Dialogs.DialogAction da = (Dialogs.DialogAction) Dialogs.showDialog(opt, "Saving this Species Delimitation information will result in the loss all but the primary tree. Do you wish to continue?");
        			if(da.toString().equals("Yes")){
        				save(e);
        			}
        		}
        	}
 		});
		functionSave.add(save,c);
	}

	public void setUpFunctionSendManual() {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0; //1
        c.gridx = 0; //1
        c.gridy = 0;
		GTextPane textPane = new GTextPane();
		textPane.setContentType("text/html");
		textPane.addHyperlinkListener(new DefaultHyperlinkListener());
		textPane.setOpaque(false);
		textPane.setEditable(false);
		textPane.setText("<html><a href=\"http://www.geneious.com/assets/plugins/3rdparty/Species_Delimitation_Plugin_Manual.pdf\">Download Manual</a></html>");
		functionSendManual.add(textPane,c);
		
	}
	
	public void save(ActionEvent e){
		for(Node n: tree.getNodes()){
			n.removeAttribute("DelimitationResult");
		}
		addResultsToTree(resultsTable.resultsSet());
		storeResult();
		loaded=false;
		fireSelectionChanged(new TreeSelectionChangeEvent(new HashSet<Node>()));
		treeViewer.getActionProvider().getSaveAction().actionPerformed(e); 
		annotatedPluginDocument.saveDocument();  
	}

	public void storeResult(){
		for(Node n:tree.getNodes()){
			n.setAttribute("Results", resultsTable.toHTML());
			n.setAttribute("TreeDistanceMatrix", resultsTable.getGeneticDistances().toTable(true));
			break;
		}
		Tree newTree=Utils.copyTree(Utils.rootTheTree(tree));
		fireTreeChanged(new TreeChangeEvent(newTree));
		
	}
	
	public void addResultsToTree(ArrayList<String> results){
		for(int i=0; i<selectionSet.size(); i++){
			selectionSet.getTreeNodeSet(i).setDelimitationResult(results.get(i));
		}
		Tree newTree=Utils.copyTree(Utils.rootTheTree(tree));
		fireTreeChanged(new TreeChangeEvent(newTree));
	}
	
	@SuppressWarnings("unchecked")
	public int numberOfTrees(){
		try{
        	Method[] meths = annotatedPluginDocument.getDocumentClass().getMethods();
			for(Method m: meths){
        		if(m.getName().equals("getTrees")){
        			try{
        				return ((Collection<? extends Tree>) m.invoke(annotatedPluginDocument.getDocument())).size();
        			}catch(Exception e){
        				return 1;
        			}
        		}
        	}
    	}catch(Exception e){
    		return 1;
    	}
		return 1;
	}
	
		
}
