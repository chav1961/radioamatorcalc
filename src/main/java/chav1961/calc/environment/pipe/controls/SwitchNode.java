package chav1961.calc.environment.pipe.controls;


import java.io.IOException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import chav1961.calc.environment.Constants;
import chav1961.calc.interfaces.PipeControlInterface;
import chav1961.calc.interfaces.PluginInterface;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.Localizer;

public class SwitchNode implements PipeControlInterface, PluginInterface {
	private static final long serialVersionUID = -8768571970349291284L;
	private static final URL				LEFT_ICON_RESOURCE = TerminalNode.class.getResource("SwitchNode.png");
	private static final URL				MINI_ICON_RESOURCE = TerminalNode.class.getResource("SwitchNodeIcon.png");
	private static final Icon				ICON = new ImageIcon(MINI_ICON_RESOURCE);
	private static final String[]			RECOMMENDED_PATH = {"menu.pipe","switch"};
	private static final String[] 			EMPTY_ACTION_LIST = new String[0];

	private final Localizer	localizer;
	
	public SwitchNode(final Localizer localizer) {
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else {
			this.localizer = localizer;
		}
	}

	@Override
	public PluginInstance newInstance(Localizer localizer, LoggerFacade logger) throws LocalizationException, SyntaxException, ContentException, IOException {
		return new SwitchContent(this,localizer);
	}

	@Override
	public String getPluginId() {
		return Constants.PIPE_SWITCH_NODE;
	}

	@Override
	public String getCaptionId() {
		return getClass().getSimpleName()+".caption";
	}

	@Override
	public String getToolTipId() {
		return getClass().getSimpleName()+".tooltip";
	}

	@Override
	public String getHelpId() {
		return getClass().getSimpleName()+".help";
	}

	@Override
	public Icon getIcon() {
		return ICON;
	}

	@Override
	public URL getMiniIconURL() {
		return MINI_ICON_RESOURCE;
	}

	@Override
	public URL getLeftIconURL() {
		return LEFT_ICON_RESOURCE;
	}

	@Override
	public String[] getRecommendedNavigationPath() {
		return RECOMMENDED_PATH;
	}

	@Override
	public Localizer getLocalizerAssociated(final Localizer parent) throws LocalizationException {
		return localizer;
	}

	@Override
	public String[] getUsesIds(final Localizer parent) throws LocalizationException {
		return EMPTY_ACTION_LIST;
	}

	@Override
	public String[] getTagsIds(final Localizer parent) throws LocalizationException {
		return EMPTY_ACTION_LIST;
	}

	@Override
	public String[] getSeeAlsoIds(final Localizer parent) throws LocalizationException {
		return EMPTY_ACTION_LIST;
	}

	@Override
	public boolean hasField(final String fieldName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] getActions() {
		// TODO Auto-generated method stub
		return null;
	}
}
