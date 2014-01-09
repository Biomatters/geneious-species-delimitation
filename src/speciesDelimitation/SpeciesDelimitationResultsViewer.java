/************************************************************/
//Species Delimitation Results Viewer
//Checks the selected document to determine if species delimitation results
//are available to be displayed as a table in a separate 'SpeciesDelimitation' tab.
//The tab is only displayed if such information is saved to the tree.
//Contains a button to save results to file.
/************************************************************/
package speciesDelimitation;
import com.biomatters.geneious.publicapi.plugin.*;
import com.biomatters.geneious.publicapi.components.GButton;

import javax.swing.*;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import jebl.evolution.graphs.Node;
import jebl.evolution.trees.*;

public class SpeciesDelimitationResultsViewer extends DocumentViewer {
	private String html2;
	private String text;
	private Tree tree;
	private boolean processed;
	private boolean matrixShowing = false;
	private boolean savingText = false;
	 private JEditorPane dTextArea; 
	 private JPanel canvas;
	 JScrollPane scroller;

    public SpeciesDelimitationResultsViewer(Tree tree) {  	
    	this.tree = tree;  	
    	if(tree!=null){
	     	processed=false;
	        for(Node n: tree.getNodes()){
	     		if(n.getAttributeNames().contains("DelimitationResult")){
	     			processed=true;
	     		}
	     	}
    	}
        updateText();
    }
    
    public String treeDistanceMatrixHTML(Tree t){
    	GeneticDistances gd = new GeneticDistances(t);  	
    	return gd.toTable(true);

    }
    
    @SuppressWarnings("serial")
	@Override
	public JPanel getComponent(){
    	if(processed==false){
    		return null;
    	}
    	canvas = new JPanel(new GridBagLayout()){
            @Override
			public void paintComponent(Graphics g){
                //we need to clear the background
                g.setColor(Color.white);
                g.fillRect(0,0,getWidth(),getHeight());
            }
        };
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        dTextArea = new JEditorPane("text/html","");
		dTextArea.setFont(new Font("Verdana",Font.BOLD,12));
		dTextArea.setOpaque(false);
		dTextArea.setEditable(false);
		dTextArea.setAlignmentX(StyleConstants.ALIGN_LEFT);
		dTextArea.setText(html2);
        canvas.add(dTextArea,c);
        
        //Copy table to clipboard
       /* GButton copy = new GButton("Copy Table to Clipboard");
        copy.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				 Clipboard2 clipb = new Clipboard2();
				 clipb.toClipboard(dTextArea.getText());
			}}
        );   
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        canvas.add(copy,c);*/
        final GButton saveText = new GButton("Save to Text File");
        saveText.setSize(50, 15);
        saveText.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(savingText==false){
					savingText=true;
				}else{
					savingText=false;
				}
				printableText();

				// create a file chooser
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

				//In response to a button click, do the following
				int returnVal = fc.showSaveDialog(saveText);
				if(returnVal == JFileChooser.APPROVE_OPTION){
					//Write the table to file
					File file = fc.getSelectedFile();
					try {
						export(file, text);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
        	
        });
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        canvas.add(saveText,c);
 
        GButton showMatrix = new GButton("Show Tree Distance Matrix");
        showMatrix.setSize(50, 15);
        showMatrix.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(matrixShowing==false){
					matrixShowing=true;
				}else{
					matrixShowing=false;
				}
				updateText();
				dTextArea.setText(html2);
				scroller.getHorizontalScrollBar().setValue(0);
				scroller.getVerticalScrollBar().setValue(0);
				
			}
        	
        });
        c.gridy = 2; // 1
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        canvas.add(showMatrix,c);
        
  
               
        
        //we put the main canvas into a JScrollPane in case the sequence is bigger than the viewer window.
       scroller = new JScrollPane(canvas);

        //since we return a JPanel, we need to put the JScrollPane into a new JPanel
        scroller.setAutoscrolls(true);
        JPanel holder = new JPanel(new BorderLayout());
        holder.add(scroller,BorderLayout.CENTER);
        return holder;
    }
 
 // writes text to file   
    public void export(File file, String outtext)
    throws IOException
    {
    	PrintWriter writer = new PrintWriter(new FileWriter(file));
    	writer.write(outtext);
    	writer.flush();
    	writer.close();
    }

    public void printableText(){
    	
        this.text= toText();
     }

    // substitutes all internal table boundaries with TAB
    public String toText(){
    	return html2.replaceAll("</td><td>", "\t").replaceAll("</tr><tr>", "\n").replaceAll("<.+?>", "");
    }
    
    public void updateText(){
    	
        this.html2="<html>";
    	if(processed){
        	
	    	if(tree!=null){
	    		html2+="<b>Species Delimitation Results</b>\n";
		    	for(Node n: tree.getNodes()){
		    		if(n.getAttributeNames().contains("Results")){
    					html2+=(String)(n.getAttribute("Results"));
    					break;
    				}
		    	}
		       if(matrixShowing)
		        for(Node n: tree.getNodes()){
    				if(n.getAttributeNames().contains("TreeDistanceMatrix")){
    					html2+=(String)(n.getAttribute("TreeDistanceMatrix"));
    					break;
    				}
		        }
		        
		        
	    	}
        }
        html2+="</html>";
    }
    public JComponent getPrintableComponent(){
    	return canvas;
    }

    //DocumentViewer contains two methods for printing, getExtendedPrintable and getPrintable
    //it is suggested that you use getExtendedPrintable for more full-featured printing
    @Override
	public ExtendedPrintable getExtendedPrintable(){
    	return null;
    }

    @Override
	public NoLongerViewedListener getNoLongerViewedListener(){
        return new NoLongerViewedListener(){
            @Override
			public void noLongerViewed(boolean isTemporary){
                //return JOptionPane.showConfirmDialog(null,"It is ludicrous to leave!\nAre you sure?") == JOptionPane.YES_OPTION;
            }
        };
    }

    @Override
	public ActionProvider getActionProvider() {
        return new ActionProvider(){
            @Override
			public GeneiousAction getCopyAction() {
                return new GeneiousAction("Copy"){
                    /**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent e){
                        StringSelection ss = new StringSelection("");
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
                    }
                };
             }
        };
    }



    
}

