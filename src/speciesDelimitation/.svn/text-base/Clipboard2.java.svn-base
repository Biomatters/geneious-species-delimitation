package speciesDelimitation;
import java.awt.datatransfer.*;
import java.awt.*;

public class Clipboard2 implements ClipboardOwner {

    public void toClipboard(String copy) {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
        try {
           sm.checkSystemClipboardAccess();
           }
        catch (Exception e) {e.printStackTrace();}
        }
      Toolkit tk = Toolkit.getDefaultToolkit();
      StringSelection st = 
           new StringSelection(copy);
      Clipboard cp = tk.getSystemClipboard();
      cp.setContents(st, this);
    }

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		System.out.println("Lost Clipboard Ownership?!?");
		
	}
}