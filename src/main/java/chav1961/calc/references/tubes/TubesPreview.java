package chav1961.calc.references.tubes;

import java.util.Locale;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import chav1961.calc.references.interfaces.TubeDescriptor;
import chav1961.calc.references.interfaces.TubePanelType;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;

class TubesPreview extends JPanel implements LocaleChangeListener {
	private final Localizer	localizer;
	
	private JLabel			scheme = new JLabel();
	private JLabel			panel = new JLabel();
	private JLabel			abbr = new JLabel();
	private JLabel			caption = new JLabel();
	private TubeDescriptor	desc = null;
	
	TubesPreview(final Localizer localizer) {
		this.localizer = localizer;
		fillLocalizedStrings();
	}
	
	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings();
	}

	public void refreshDesc(final TubeDescriptor desc) {
		this.desc = desc;
		if (desc != null) {
			fillContent();
		}
	}
	
	private void fillContent() {
		// TODO Auto-generated method stub
		scheme.setIcon(desc.getScheme());
		panel.setIcon(desc.getPanelType().getIcon());
		abbr.setText(desc.getAbbr());
		fillLocalizedStrings();
	}

	private void fillLocalizedStrings() {
		// TODO Auto-generated method stub
	}

}
