package ca.nengo.ui.actions;

import javax.swing.JFileChooser;

import ca.nengo.ui.NengoGraphics;
import ca.nengo.ui.lib.actions.StandardAction;
import ca.nengo.ui.lib.world.piccolo.primitives.Text;
import ca.nengo.ui.lib.world.piccolo.primitives.Universe;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfContentByte;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * TODO
 * 
 * @author Chris Eliasmith
 */
public class GeneratePDFAction extends StandardAction {
    private static final long serialVersionUID = 1L;

    /**
     * @param description TODO
     */
    public GeneratePDFAction(String description) {
        super(description);
    }

    protected void action() {
    	String name = new String("Nengo");
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save layout as PDF");
        fileChooser.setSelectedFile(new File(name + ".pdf"));
        
        Component ng = NengoGraphics.getInstance();
        
        if (fileChooser.showSaveDialog(ng)==JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            Universe universe = ((NengoGraphics) ng).getUniverse();
            double w = universe.getSize().getWidth();
            double h = universe.getSize().getHeight();
            

//            if(false) {
//                // basic method: make a PDF page the same size as the Nengo window.
//                //   This method preserves all details visible in the GUI
//                float pw = (float) w;
//                float ph = (float) h;
//
//                // create PDF document and writer
//                Document doc = new Document(new Rectangle(pw,ph), 0, 0, 0, 0 );
//                PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file));
//                doc.open();
//                PdfContentByte cb = writer.getDirectContent();
//
//                // create a template, print the image to it, and add it to the page
//                PdfTemplate tp = cb.createTemplate(pw, ph);
//                Graphics2D g2 = tp.createGraphicsShapes(pw, ph);
//                universe.paint(g2);
//                g2.dispose();
//                cb.addTemplate(tp,0,0);
//
//                // clean up everything
//                doc.close();
//            }

            // Top of page method: prints to the top of the page
            float pw = 550;
            float ph = 800;
    
            // create PDF document and writer
           Document doc = new Document();
           try{
        	   PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file));
        	   doc.open();

            PdfContentByte cb = writer.getDirectContent();

            // create a template
            PdfTemplate tp = cb.createTemplate(pw,ph);
            Graphics2D g2 = tp.createGraphicsShapes(pw,ph);

            // scale the template to fit the page
            AffineTransform at = new AffineTransform(); 
            float s = (float) Math.min(pw/w,ph/h);
            at.scale(s,s);
            g2.setTransform(at);

            // print the image to the template
            // turning off setUseGreekThreshold allows small text to print
            Text.setUseGreekThreshold(false);
            universe.paint(g2);
            Text.setUseGreekThreshold(true);
            g2.dispose();

            // add the template
            cb.addTemplate(tp,20,0);

            // clean up everything
            doc.close();
           } catch (DocumentException e) {
        	   e.printStackTrace();
           } catch (IOException e) {
        	   e.printStackTrace();
           }

        }
    }
}
