/************************************************************/
//Color Chooser
//Uses a predefined 'good' colour set to return a random RGB value for any colour requiring program.
//If all the predefined colours are used then a random variation of the colours in the colour set
//to produce a new colour.
/************************************************************/
package speciesDelimitation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
public class ColorChooser {
	
	private boolean[][][] colorGrid;
	private Color[] preset;
	private int presetsUsed=0;
	private ArrayList<Color> colorList;
	public ColorChooser(){
		colorGrid = new boolean[256][256][256];
		Color[] colors = colorGrid();
		colorList = new ArrayList<Color>();
		for(int i=0; i< colors.length; i++){
			colorList.add(colors[i]);
		}
	}
	
	public String getColor(){
		if(colorList.size()>0){
			int index = (int)(Math.random()*colorList.size()+1);
			Color newColor = colorList.get(index);
			colorList.remove(index);
			int red = newColor.getRed();
			int green = newColor.getGreen();
			int blue = newColor.getBlue();
			if(colorGrid[red][green][blue]==true){
				return getColor();
			}
			colorGrid[red][green][blue]=true;
			return red+","+green+","+blue;
		}else{
			int preset1 = (int)(Math.random()*preset.length);
			int preset2 = (int)(Math.random()*preset.length);
			int red = (int)((preset[preset1].getRed()+preset[preset2].getRed())/2);
			int green = (int)((preset[preset1].getGreen()+preset[preset2].getGreen())/2);
			int blue = (int)((preset[preset1].getBlue()+preset[preset2].getBlue())/2);
			if(colorGrid[red][green][blue]==false){
				colorGrid[red][green][blue]=true;
				presetsUsed++;
				return red+","+green+","+blue;
			}else{
				return getColor();
			}
		}
	}
	
	public void removeColors(LinkedList<String> al){
		for(int i=0; i<al.size(); i++){
			String[] rgb = (al.get(i)).split(",");
			colorGrid[Integer.parseInt(rgb[0])][Integer.parseInt(rgb[1])][Integer.parseInt(rgb[2])]=true;
		}
	}
	
	public void reset(){
		colorGrid = new boolean[256][256][256];
		Color[] colors = colorGrid();
		colorList = new ArrayList<Color>();
		for(int i=0; i< colors.length; i++){
			colorList.add(colors[i]);
		}
	}
	
	private Color[] colorGrid(){
		Color[] colors = {
		new Color(20,128,255),
		new Color(87,193,69),
		new Color(0,0,255),
		new Color(255,102,255),
		new Color(255,200,102),
		new Color(255,111,173),
		new Color(50,168,0),
		new Color(64,0,196),
		new Color(201,0,255),
		new Color(227,255,102),
		new Color(102,255,255),
		new Color(145,206,243),
		new Color(207,98,164),
		//new Color(255,255,0),
		new Color(255,0,255),
		new Color(255,20,30),
		new Color(214,196,0),
		new Color(142,202,138),
		new Color(102,102,255),
		new Color(191,0,127),
		new Color(255,102,150),
		new Color(84,230,199),
		new Color(102,202,255),
		new Color(102,255,204),
		new Color(0,255,0),
		new Color(227,0,130),
		new Color(255,126,0),
		new Color(0,181,181),
		new Color(217,153,209),
		new Color(156,224,209),
		new Color(0,204,255),
		new Color(0,153,255),
		new Color(0,102,255),
		new Color(0,204,204),
		new Color(0,153,204),
		new Color(0,153,51),
		new Color(0,204,0),
		new Color(51,204,255),
		new Color(51,102,204),
		new Color(102,0,51),
		new Color(102,0,153),
		new Color(102,0,102),
		new Color(102,51,153),
		new Color(102,102,153),
		new Color(102,102,204),
		new Color(102,102,255),
		new Color(102,204,204),
		new Color(102,204,153),
		new Color(102,255,204),
		new Color(153,0,102),
		new Color(153,0,51),
		new Color(153,0,0),
		new Color(153,51,0),
		new Color(153,51,204),
		new Color(153,153,255),
		new Color(153,204,255),
		new Color(153,204,204),
		new Color(153,204,153),
		new Color(153,204,51),
		new Color(153,204,0),
		new Color(153,255,255),
		new Color(153,255,204),
		new Color(153,255,153),
		new Color(153,255,51),
		new Color(204,204,102),
		new Color(204,153,51),
		new Color(204,153,255),
		new Color(204,51,102),
		new Color(204,0,102),
		new Color(204,51,0),
		new Color(255,51,102),
		new Color(255,51,51),
		new Color(255,102,0),
		new Color(255,204,51),
		};
		return colors;
	}

}