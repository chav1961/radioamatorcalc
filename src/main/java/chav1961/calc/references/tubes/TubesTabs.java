package chav1961.calc.references.tubes;

import java.util.Locale;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

import chav1961.calc.references.interfaces.TubeDescriptor;
import chav1961.calc.references.interfaces.TubesType;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.ui.swing.SwingUtils;

class TubesTabs extends JTabbedPane implements LocaleChangeListener {
	private static final long 	serialVersionUID = -9007080601952548368L;
	private static final Icon	GENERAL_ICON = new ImageIcon(TubesTabs.class.getResource(""));
	private static final String	GENERAL_TITLE = "chav1961.calc.reference.tubesReference.tab.general";
	
	
	private final Localizer		localizer;

	TubesTabs(final Localizer localizer, final Consumer<TubeDescriptor> selection, final TubeDescriptor... content) {
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (content == null || Utils.checkArrayContent4Nulls(content) >= 0) {
			throw new IllegalArgumentException("Content is null or contains nulls inside");
		}
		else {
			this.localizer = localizer;
			
			addTab(localizer, content, selection);
			for(TubesType item : TubesType.values()) {
				addTab(item, localizer, content, selection);
			}
			fillLocalizedStrings();
			setSelectedIndex(0);
		}
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		for(int index = 0, maxIndex = getTabCount(); index < maxIndex; index++) {
			SwingUtils.refreshLocale(getTabComponentAt(index), oldLocale, newLocale);
		}
		fillLocalizedStrings();
	}

	public TubeDescriptor getSelection() {
		return ((TubesTab)getTabComponentAt(getSelectedIndex())).getSelection();
	}
	
	private void addTab(final Localizer localizer, final TubeDescriptor[] content, final Consumer<TubeDescriptor> selection) {
		final TubesTab	tab = new TubesTab(localizer, null, selection, content);
		
		addTab("", GENERAL_ICON, tab);
	}
	
	private void addTab(final TubesType type, final Localizer localizer, final TubeDescriptor[] content, final Consumer<TubeDescriptor> selection) {
		final TubesTab	tab = new TubesTab(localizer, type, selection, content);
		
		addTab("", tab.getType().getIcon(), tab);
	}

	private void fillLocalizedStrings() {
		for(int index = 0, maxIndex = getTabCount(); index < maxIndex; index++) {
			if (index == 0) {
				setTitleAt(index, localizer.getValue(GENERAL_TITLE));
			}
			else {
				try {
					setTitleAt(index, localizer.getValue(TubesType.class.getField(TubesType.values()[index - 1].name()).getAnnotation(LocaleResource.class).value()));
				} catch (LocalizationException | NoSuchFieldException | SecurityException e) {
					setTitleAt(index, TubesType.values()[index - 1].name());
				}
			}
		}
	}
}
