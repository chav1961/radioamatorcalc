package chav1961.calc.references.tubes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Locale;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;

import chav1961.calc.references.interfaces.TubeConnector;
import chav1961.calc.references.interfaces.TubePanelGroup;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.FieldFormat;
import chav1961.purelib.ui.swing.SwingUtils;

public class ConnScreen extends JPanel implements LocaleChangeListener {
	private static final long 	serialVersionUID = 2335837174087681281L;
	private static final String	LABEL_TEXT = "chav1961.calc.reference.tubesReference.connScreen.label"; 
	private static final int	PREFERRED_WIDTH = 400;
	private static final int	PREFERRED_HEIGHT = 200;
	
	private final Localizer		localizer;
	private final JLabel		pinoutLabel = new JLabel();
	private final JComboBox<TubePanelGroup>	pinout = new JComboBox<>(TubePanelGroup.values());
	private final ConnPicture	picture;
	
	public ConnScreen(final Localizer localizer) throws ContentException {
		super(new BorderLayout(5, 5));
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else {
			this.localizer = localizer;
			this.picture = new ConnPicture();
		
			final SpringLayout	sl = new SpringLayout();
			final JPanel		top = new JPanel(sl);
			
			top.add(pinoutLabel);
			top.add(pinout);
			
			sl.putConstraint(SpringLayout.NORTH, pinoutLabel, 5, SpringLayout.NORTH, top);
			sl.putConstraint(SpringLayout.NORTH, pinout, 5, SpringLayout.NORTH, top);
			sl.putConstraint(SpringLayout.WEST, pinoutLabel, 5, SpringLayout.WEST, top);
			sl.putConstraint(SpringLayout.EAST, pinout, 0, SpringLayout.EAST, top);
			sl.putConstraint(SpringLayout.WEST, pinout, 0, SpringLayout.EAST, pinoutLabel);
			sl.putConstraint(SpringLayout.SOUTH, pinoutLabel, 5, SpringLayout.SOUTH, top);
			sl.putConstraint(SpringLayout.SOUTH, pinout, 5, SpringLayout.SOUTH, top);
			top.setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_WIDTH/10));
			
			picture.setBorder(new LineBorder(Color.black));
			picture.setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
			
			add(top, BorderLayout.NORTH);
			add(picture, BorderLayout.CENTER);
			
			pinout.setRenderer(SwingUtils.getCellRenderer(TubePanelGroup.class, new FieldFormat(TubePanelGroup.class), ListCellRenderer.class));
			pinout.addActionListener((e)->picture.setPinout(TubePanelGroup.class.getResourceAsStream(((TubePanelGroup)pinout.getSelectedItem()).getSVGName())));
			pinout.setSelectedIndex(0);
			fillLocalizedStrings();
		}
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings();
	}

	public TubeConnector[] getConnectors() {
		return picture.getConnectors();
	}
	
	private void fillLocalizedStrings() {
		pinoutLabel.setText(localizer.getValue(LABEL_TEXT));
	}
}
