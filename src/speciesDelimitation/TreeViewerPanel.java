/************************************************************/
//Tree Viewer Panel
//A panel that contains specific panels for messages at the top, and any componenets in the mid
//and bottom sections. These panels can be updtated independantly as neccesary.
/************************************************************/
package speciesDelimitation;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.text.StyleConstants;

class TreeViewerPanel extends JPanel{

	private static final long serialVersionUID = 1;
	private JPanel messagePanel;
	private JPanel midPanel;
	private JPanel botPanel;
	
	public TreeViewerPanel(Dimension d){
		this.setSize(d);
		this.setMaximumSize(d);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		messagePanel = new JPanel();
		messagePanel.setAutoscrolls(true);
		messagePanel.add(defaultJEditorPane());
		midPanel = new JPanel();
		botPanel = new JPanel();
		this.add(messagePanel);
		this.add(midPanel);
		this.add(botPanel);
	}
	
	public JPanel createButtonsPanel(ArrayList<JComponent> buttons){
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(buttons.size(),1));
		for(JComponent button: buttons){
			buttonsPanel.add(button);
		}
		return buttonsPanel;
	}
		
	public void shortMessage(String dialogText){
		dialogText= "<html><font size=\"3\", face=\"sanserif\"><B><Center>"+dialogText+"</html>";
		((JEditorPane)messagePanel.getComponent(0)).setText(dialogText);
	}
	public void longMessage(ArrayList<String> text){
		String dialogText = "<html><font size=\"3\", face=\"sanserif\"><B><Center>";
		for(String line:text){
			dialogText+=(line+"\n");
		}
		dialogText += "</html>";
		((JEditorPane)messagePanel.getComponent(0)).setText(dialogText);
	}
	

	private JEditorPane defaultJEditorPane(){
		JEditorPane dTextArea = new JEditorPane("text/html","");
		dTextArea.setFont(new Font("sansserif",Font.BOLD,12));
		dTextArea.setOpaque(false);
		dTextArea.setEditable(false);
		dTextArea.setAlignmentX(StyleConstants.ALIGN_LEFT);
		return dTextArea;
	}	
	
	public void updateMidPanel(JPanel newPanel){
		this.remove(1);
		this.add(newPanel,1);
	}
	
	public void updateBotPanel(JPanel newPanel){
		this.remove(2);
		this.add(newPanel,2);
	}
	
}
