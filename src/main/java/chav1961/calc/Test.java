package chav1961.calc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.fit.pdfdom.PDFDomTree;

import chav1961.purelib.basic.Utils;

public class Test {

	public static void main(String[] args) throws IOException, ParserConfigurationException {
		// TODO Auto-generated method stub
		    PDDocument pdf = PDDocument.load(new File("c:/tmp/luconin/tiny13.pdf"));
		    Utils.copyStream(pdf.getDocumentCatalog().getMetadata().exportXMPMetadata(),System.err);
//		    new PDFDomTree().writeText(pdf,new FileWriter(new File("c:/tmp/luconin/tiny13.html")));
	}

}
