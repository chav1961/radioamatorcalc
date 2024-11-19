package chav1961.calc;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;

import javax.swing.SwingUtilities;

// https://github.com/LibreTranslate/LibreTranslate
// https://github.com/dynomake/libretranslate-java
public class Clipbrd {
	final Clipboard 		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); 
	final FlavorListener	l = (e)->{
								process();
							};
	String					oldString = "";

	public Clipbrd() {
		clipboard.addFlavorListener(l);
	}
	
	private void process() {
		clipboard.removeFlavorListener(l);
		try {
			if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
				final String	data = (String)clipboard.getData(DataFlavor.stringFlavor);
				
				if (!oldString.equals(data)) {
					final Process	p = new ProcessBuilder("python.exe","x.py").directory(new File("d:/mnist")).start();
					final byte[]	buffer = new byte[256];
				
					System.err.println("Insert: "+data);
					p.getOutputStream().write((data+"\r\n").getBytes("UTF-8"));
					p.getOutputStream().flush();
					final int 		len = p.getInputStream().read(buffer);
					final String	trans = new String(buffer, 0, len, "UTF-8");
					System.err.println("Translate: "+trans);
					p.waitFor();
					final StringSelection selection = new StringSelection(trans);
					clipboard.setContents(selection, selection);
					oldString = trans;
				}
			}
		} catch (UnsupportedFlavorException | IOException | InterruptedException e) {
		} finally {
			clipboard.addFlavorListener(l);
		}
	}
							
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		final Clipbrd	c = new Clipbrd();
		
		System.in.read();
	}

}
