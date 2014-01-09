package speciesDelimitation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {
	
	private BufferedWriter bw = null;
	
	public Writer(){
		try {
			bw = new BufferedWriter(new FileWriter("/Users/hros001/Testing/SpDelim/testout.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void write(String x){
		try {
			bw = new BufferedWriter(new FileWriter("/Users/hros001/Testing/SpDelim/testout.txt", true));
			bw.write(x);
			bw.newLine();
			bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
