package chav1961.calc.environment.pipe.controls;

import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import chav1961.calc.LocalizationKeys;
import chav1961.calc.environment.Constants;
import chav1961.calc.interfaces.PipeControlInterface;
import chav1961.calc.interfaces.PluginInterface;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.swing.SwingUtils;

public class MapNode implements PipeControlInterface, PluginInterface {
	private static final URL				LEFT_ICON_RESOURCE = MapNode.class.getResource("MapNode.png");
	private static final URL				MINI_ICON_RESOURCE = MapNode.class.getResource("MapNodeIcon.png");
	private static final Icon				ICON = new ImageIcon(MINI_ICON_RESOURCE);
	private static final String[]			RECOMMENDED_PATH = {"menu.pipe","Map"};
	private static final String[] 			EMPTY_ACTION_LIST = new String[0];
	
	private final Localizer					localizer;
	
	public MapNode(final Localizer localizer) {
		this.localizer = localizer;
	}

	@Override
	public boolean hasField(final String fieldName) {
		return false;
	}

	@Override
	public String[] getActions() {
		return EMPTY_ACTION_LIST;
	}

	@Override
	public PluginInstance newInstance(final Localizer localizer, final LoggerFacade logger) throws LocalizationException, SyntaxException, ContentException, IOException {
		return new MapContent(this,localizer);
	}

	@Override
	public String getPluginId() {
		return Constants.PIPE_MAP_NODE;
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
}
