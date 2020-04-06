package chav1961.calc.pipe;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.model.interfaces.NodeMetadataOwner;

public class JControlLabel extends JLabel implements NodeMetadataOwner, LocaleChangeListener {
	private static final long 	serialVersionUID = 1L;

	private final ContentNodeMetadata	metadata;
	
	
	public JControlLabel(final Icon icon, final ContentNodeMetadata metadata) throws ContentException {
		super(icon);
		if (metadata == null) {
			throw new NullPointerException("Metadata associated can't be null");
		}
		else {
			try{
				final Localizer 	l = LocalizerFactory.getLocalizer(metadata.getLocalizerAssociated());
				this.metadata = metadata;
				setBorder(new LineBorder(Color.BLACK));
				fillLocalizedStrings(l.currentLocale().getLocale(),l.currentLocale().getLocale());
				enableEvents(AWTEvent.MOUSE_EVENT_MASK);
			} catch (LocalizationException e) {
				throw new ContentException(e);
			}
		}
	}

	@Override
	public ContentNodeMetadata getNodeMetadata() {
		return metadata;
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings(oldLocale,newLocale);
	}

	private void fillLocalizedStrings(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		if (getNodeMetadata().getTooltipId() != null) {
			try{setToolTipText(LocalizerFactory.getLocalizer(getNodeMetadata().getLocalizerAssociated()).getValue(getNodeMetadata().getTooltipId()));
			} catch (LocalizationException e) {
				setToolTipText(getNodeMetadata().getTooltipId());
			}
		}
	}
}
