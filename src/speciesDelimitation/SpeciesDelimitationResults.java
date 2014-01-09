package speciesDelimitation;

import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.documents.URN;
import org.jdom.Element;
import org.jdom.CDATA;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SpeciesDelimitationResults implements PluginDocument{

    private Date creationDate; // creation date of file
    private String name;
    private String html;
    private String desc;

	public SpeciesDelimitationResults(SpeciesDelimitation sd, Date creationDate) {
        this.creationDate = creationDate;
        this.name = "Species Delimitation Results for "+ sd.getAnnotatedPluginDoc().getName();
        this.html = sd.getResultsTable().toHTML();
        this.desc = "Species Delimitation Results: "+ sd.getAnnotatedPluginDoc().getName();
    }

    // Empty constructor: PluginDocument requirement
    public SpeciesDelimitationResults() {}

    public Element toXML() {
        Element root = new Element("Species DelimitationResults");
        root.addContent(new Element("name").setText(name));
        root.addContent(new Element("desc").setText(desc));
        root.addContent(new Element("date").setText("" + creationDate.getTime()));
        root.addContent(new Element("html").setContent(new CDATA(html)));
        return root;
    }

    public void fromXML(Element doc) {
        name = doc.getChildText("name");
        desc = doc.getChildText("desc");

        final String dateText = doc.getChildText("date");
        try {
            creationDate = new Date(Long.parseLong(dateText));
        } catch (NumberFormatException e) {
            // should not happen
        }
        html = doc.getChild("html").getText();
    }
   
    public List<DocumentField> getDisplayableFields() {
        return Arrays.asList(new DocumentField[]{});
    }

    public String getName() {
        return name;
    }

    public URN getURN() {
        return null;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getDescription() {
        return desc;
    }

    public String toHTML() {
        return html;
    }

	public Object getFieldValue(String arg0) {
		return null;
	}	
	
}
