package speciesDelimitation;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import com.biomatters.geneious.publicapi.components.GLabel;

public class ComboBoxRenderer extends GLabel implements ListCellRenderer{
	/************************************************************/
	//Combo Box Renderer
	//Changes the display of a combobox such that it is uniquely coloured
	//based on the putative species that have been selected.
	/************************************************************/
	final static long serialVersionUID = 0;
	ArrayList<TreeNodeSet> s;
	SpeciesDelimitation sd;
	Border border;
	public ComboBoxRenderer(ArrayList<TreeNodeSet> set){
		s = set;
		this.setOpaque(true);
		this.setHorizontalAlignment(CENTER);
		border = LineBorder.createGrayLineBorder();
		this.setBorder(border);
	}
	
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus){
		TreeNodeSet tns=null;
		
			try{
				tns = s.get(index);
			}catch(Exception e){
				setBackground(Color.white);
			}
			
		setText(value.toString());
		if(tns!=null){
			setBackground(tns.getColor());
			setBorder(border);
		}else{
			setBackground(Color.WHITE);
			setBorder(border);
		}
		setForeground(new Color(40,40,40));		
		
		return this;
		
	}
}