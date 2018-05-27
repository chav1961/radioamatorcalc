package chav1961.calc.elements.coils;

import java.io.Closeable;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import chav1961.calc.interfaces.PluginInterface;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.swing.AutoBuiltForm;

public class SingleCoils extends AutoBuiltForm<SingleCoilsCalculator> implements PluginInterface, Closeable {
	private static final long 	serialVersionUID = 2615737307529282959L;
	private static final Icon	ICON = new ImageIcon(SingleCoils.class.getResource("SingleCoils.png"));
	
	public SingleCoils(final Localizer localizer, final LoggerFacade logger) throws NullPointerException, IllegalArgumentException, LocalizationException, SyntaxException, ContentException, IOException {
		this(localizer,new SingleCoilsCalculator(logger));
	}

	protected SingleCoils(final Localizer localizer, final SingleCoilsCalculator instance) throws NullPointerException, IllegalArgumentException, LocalizationException, SyntaxException, ContentException, IOException {
		super(localizer,instance,instance);
	}
	
	@Override
	public String getPluginId() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getCaptionId() {
		return getPluginId()+".caption";
	}

	@Override
	public String getToolTipId() {
		return getPluginId()+".tooltip";
	}

	@Override
	public String getHelpId() {
		return getPluginId()+".help";
	}

	@Override
	public Icon getIcon() {
		return ICON;
	}
}
