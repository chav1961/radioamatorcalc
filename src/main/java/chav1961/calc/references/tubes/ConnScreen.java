package chav1961.calc.references.tubes;

import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import chav1961.calc.references.interfaces.TubePanelGroup;
import chav1961.purelib.model.FieldFormat;
import chav1961.purelib.ui.swing.SwingUtils;

public class ConnScreen extends JPanel {
	private static final long serialVersionUID = 2335837174087681281L;
	
	private final JComboBox<TubePanelGroup>	pinout = new JComboBox<>(TubePanelGroup.values());
	
	public ConnScreen() {
		super(new BorderLayout(5, 5));
		pinout.setRenderer(SwingUtils.getCellRenderer(TubePanelGroup.class, new FieldFormat(TubePanelGroup.class), ListCellRenderer.class));
		
	}
}
