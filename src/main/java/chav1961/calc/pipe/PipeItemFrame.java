package chav1961.calc.pipe;

import java.util.Locale;

import javax.swing.JInternalFrame;

import chav1961.calc.interfaces.PipeContainerControlInterface;
import chav1961.calc.interfaces.PipeContainerInterface;
import chav1961.calc.interfaces.PipeContainerItemInterface;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.ui.swing.useful.JStateString;

class PipeItemFrame<T> extends JInternalFrame implements LocaleChangeListener, PipeContainerInterface {
	private final Localizer		localizer;
	private final PipeItemType	itemType;
	private final JStateString	state;
	
    PipeItemFrame(final Localizer localizer, final PipeItemType itemType) throws ContentException {
    	if (localizer == null) {
    		throw new NullPointerException("Localizer can't be null");
    	}
    	else if (itemType == null) {
    		throw new NullPointerException("Item type can't be null");
    	}
    	else {
    		this.localizer = localizer;
    		this.itemType = itemType;
	    	this.state = new JStateString(localizer);
	    	
	    	fillLocalizedStrings(localizer.currentLocale().getLocale(), localizer.currentLocale().getLocale());
    	}
    }
    
	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		// TODO Auto-generated method stub
		fillLocalizedStrings(oldLocale, newLocale);
	}
	
	@Override
	public PipeItemType getType() {
		return itemType;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Iterable<PipeContainerItemInterface> getItems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasComponentAt(int x, int y) {
		return false;
	}
	
	@Override
	public PipeContainerItemInterface at(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ContentMetadataInterface getModel() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private void fillLocalizedStrings(final Locale oldLocale, final Locale newLocale) {
		
	}
}