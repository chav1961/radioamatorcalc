package chav1961.calc.references.tubes;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.URI;

import javax.swing.JFrame;

import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.Localizer;

public class App extends JFrame {
	private static final long serialVersionUID = 1L;

	private final Localizer	localizer;
	
	public App() throws IOException {
		this.localizer = LocalizerFactory.getLocalizer(URI.create("i18n:xml:root://chav1961.calc.Application/chav1961/calculator/i18n/i18n.xml"));
		
		final TubesScreen	ts = new TubesScreen(localizer);
		
		getContentPane().add(ts, BorderLayout.CENTER);
		setSize(1024, 768);
		setLocationRelativeTo(null);
	}

	public static void main(String[] args) throws IOException {
		new App().setVisible(true);
	}
}
