package chav1961.calc.pipe;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import chav1961.calc.interfaces.PipeContainerInterface;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.model.interfaces.NodeMetadataOwner;

public class JControlLabel extends JLabel implements NodeMetadataOwner, LocaleChangeListener {
	private static final long 	serialVersionUID = 1L;

	private final ContentNodeMetadata		metadata;
	private final PipeContainerInterface	owner;
	
	public JControlLabel(final Icon icon, final ContentNodeMetadata metadata, final PipeContainerInterface owner) throws ContentException {
		super(icon);
		if (metadata == null) {
			throw new NullPointerException("Metadata associated can't be null");
		}
		else if (owner == null) {
			throw new NullPointerException("Control owner can't be null");
		}
		else {
			try{final Localizer 	l = LocalizerFactory.getLocalizer(metadata.getLocalizerAssociated());
				
				this.owner = owner;
				this.metadata = metadata;
				setBorder(new LineBorder(Color.BLACK));
				fillLocalizedStrings(l.currentLocale().getLocale(),l.currentLocale().getLocale());
				enableEvents(AWTEvent.MOUSE_EVENT_MASK);
				setCursor(new Cursor(Cursor.HAND_CURSOR));
				setName(this.getClass().getSimpleName());
			} catch (LocalizationException e) {
				throw new ContentException(e);
			}
		}
	}

	@Override
	public ContentNodeMetadata getNodeMetadata() {
		return metadata;
	}

	public PipeContainerInterface getOwner() {
		return owner;
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
