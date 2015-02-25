/************************************************************/
//Species Delimitation Plugin
//Passes all the information about the plugin to Geneious.
//This information is displayed in the plugins section when the plugin is installed.
//
/************************************************************/
package speciesDelimitation;

import jebl.evolution.trees.Tree;

import com.biomatters.geneious.publicapi.plugin.GeneiousPlugin;
import com.biomatters.geneious.publicapi.plugin.TreeViewerExtension;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.types.PhylogenyDocument;
import com.biomatters.geneious.publicapi.documents.types.RootedTreeDocument;
import com.biomatters.geneious.publicapi.documents.types.SameTaxaTreesDocument;
import com.biomatters.geneious.publicapi.documents.types.TreeDocument;
import com.biomatters.geneious.publicapi.implementations.DefaultSameTaxaTreesDocument;
import com.biomatters.geneious.publicapi.implementations.DefaultTreeDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentViewerFactory;
import com.biomatters.geneious.publicapi.plugin.DocumentViewer;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;


public class SpeciesDelimitationPlugin extends GeneiousPlugin {
	
	private SpeciesDelimitation sd;
	
    public TreeViewerExtension.Factory[] getTreeViewerExtensionFactories() {
        TreeViewerExtension.Factory factory = new TreeViewerExtension.Factory() {
            public TreeViewerExtension createTreeViewerExtension(DocumentViewer treeViewer, AnnotatedPluginDocument annotatedPluginDocument) {
            	sd = new SpeciesDelimitation(treeViewer,annotatedPluginDocument);
            	return sd;
            }
        };
        return new TreeViewerExtension.Factory[]{factory};
    }

    public String getName() {
        return "Species Delimitation";
    }

    public String getHelp() {
        return "Species Delimitation";
    }

    public String getDescription() {
        return "Species Delimitation allows users to define clades in their trees and test for distinctiveness based on multiple methods.";
    }

    public String getAuthors() {
        return "Brad Masters (major developer) & Howard Ross (modifications)";
    }

    public String getVersion() {
        return "1.4.1";
    }

    public String getMinimumApiVersion() {
        return "4.8";
    }

    public int getMaximumApiVersion() {
        return 4;
    }
       
    
    /************************************************************/
    //Allows the processing of the results information if it is available within the document.
    /************************************************************/
    public DocumentViewerFactory[] getDocumentViewerFactories() {
        return new DocumentViewerFactory[]{
                new DocumentViewerFactory() {
                    public DocumentSelectionSignature[] getSelectionSignatures(){
                        return new DocumentSelectionSignature[]{
                        		new DocumentSelectionSignature(TreeDocument.class,1,1),
                        		new DocumentSelectionSignature(RootedTreeDocument.class,1,1),
                        		new DocumentSelectionSignature(PhylogenyDocument.class,1,1),
                        		new DocumentSelectionSignature(DefaultTreeDocument.class,1,1),
                        		new DocumentSelectionSignature(DefaultSameTaxaTreesDocument.class,1,1),
                        		new DocumentSelectionSignature(SameTaxaTreesDocument.class,1,1)
                        };
                    }

                    public String getHelp(){
                    	String help ="";
                    	help+= "<p>The Species Delimitation plugin allows users to explore the boundaries of putative species in phylogenetic trees. The tool summarizes measures of phylogenetic support and diagnosability of species defined as user-selected collections of taxa.</p>";
                    	help+= "<h2>Use</h2>";
                    	help+= "<p><b>Adding Species</b>: Select a group of nodes, enter a name and click <i>Add Selection</i> to define a species. Alternatively, select a set of nodes, use the Color Nodes tool to give them a unique color, and click <i>Reload Tree.</i></p>";
                    	help+= "<p><b>Redefining Species</b>: Select an existing species and click <i>Remove</i>. Reassign the taxa using the methods for adding species.</p>";
                    	help+= "<p><b>Reload Tree</b>: Clears all of the species sets and redefines them based on the identically colored groups in the tree.</p>";
                    	help+= "<p><b>Reset</b>: Clears all of the coloring and species sets from the tree.</p>";
                    	help+= "<p><b>Save</b>: Creates a new tab containing a table of summary statistics or updates the existing tab.</p>";
                    	help+= "<h2>Statistics</h2>";
                    	help+= "<p><b>Monophyletic?</b>: Whether the species is Monophyletic or not. By definition, singletons are monophyletic.</p>";
                    	help+= "<p><b>Intra Dist</b>: The average pairwise tree distance among members of the species.</p>";
                    	help+= "<p><b>Inter Dist</b>: The average pairwise tree distance between the members of the focal species and members of the next closest species.</p>";
                    	help+= "<p><b>Intra/Inter</b>: The ratio of the two distances. It provides a measure of genetic differentiation between the focal species and its nearest neighboring species.</p>";
                    	help+= "<p><b>P ID(Strict)</b>: The mean probability (95% CI) of making a correct identification of an unknown specimen using placement on a tree and the criterion that it must fall within, not sister to, the species clade.</p>";
                    	help+= "<p><b>P ID(Liberal)</b>: The mean probability (95% CI) of making a correct identification of an unknown specimen using any of BLAST (best sequence alignment), DNA Barcoding (closest genetic distance) or placement on a tree, with the criterion that it falls sister to or within a monospecies clade.</p>";
                    	help+= "<p><b>Av(MRCA)</b>: The mean distance between the MRCA of a species and its members.</p>";
                    	help+= "<p><b>P(Randomly Distinct)</b>: The probability that a clade is as distinct (has such a long subtending branch) as this due to random coalescent processes.</p>";
                    	help+= "<p><b>Clade Support</b>: The clade support (bootstrap support or Bayesian posterior probability) for the species. This must be estimated before the tree is analysed using the Species Delimitation tool.</p>";                		
                    	help+= "<p><b>Rosenberg's P<sub>AB</sub></b>: The probability that <i>a</i> sequences from species A are reciprocally monophyletic with <i>b</i> sequences from other species by chance alone.</p>";
                    	return help;
                    }

                    public String getDescription(){
                        return "Displays the results from Species Delimitation";
                    }

                    public String getName(){
                        return "Species Delimitation";
                    }

                    public DocumentViewer createViewer(AnnotatedPluginDocument[] annotatedDocuments) {
                        if (sd != null) {
                            Tree tree = sd.getTree();
                            sd = null; //to prevent the next viewer from wrongly using the same "sd" if createTreeViewerExtension is not called.
                            return new SpeciesDelimitationResultsViewer(tree);
                        }
                        return null;
                    }
                }
        };
    }   
   
    
}
