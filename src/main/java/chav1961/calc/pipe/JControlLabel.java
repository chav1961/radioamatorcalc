package chav1961.calc.pipe;

import java.io.IOException;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.interfaces.NodeMetadataOwner;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public class JControlLabel extends JLabel implements NodeMetadataOwner, LocaleChangeListener {
	private static final long 	serialVersionUID = 1L;

	private final ContentNodeMetadata	metadata;
	
	public JControlLabel(final Icon icon, final ContentNodeMetadata metadata) {
		super(icon);
		if (metadata == null) {
			throw new NullPointerException("Metadata associated can't be null");
		}
		else {
			this.metadata = metadata;
		}
	}

	@Override
	public ContentNodeMetadata getNodeMetadata() {
		return metadata;
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		if (getNodeMetadata().getTooltipId() != null) {
			try {
				setToolTipText(LocalizerFactory.getLocalizer(getNodeMetadata().getLocalizerAssociated()).getValue(getNodeMetadata().getTooltipId()));
			} catch (IOException e) {
				setToolTipText(getNodeMetadata().getTooltipId());
			}
		}
	}
}
