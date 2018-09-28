package chav1961.calc.environment.pipe.controls;

import java.io.IOException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import chav1961.calc.environment.Constants;
import chav1961.calc.interfaces.PipeControlInterface;
import chav1961.calc.interfaces.PluginInterface;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.AbstractLowLevelFormFactory.FieldDescriptor;

public class StartNode implements PipeControlInterface, PluginInterface {
	private static final long serialVersionUID = -8768571970349291284L;
	private static final URL				LEFT_ICON_RESOURCE = TerminalNode.class.getResource("StartNode.png");
	private static final URL				MINI_ICON_RESOURCE = TerminalNode.class.getResource("StartNodeIcon.png");
	private static final Icon				ICON = new ImageIcon(MINI_ICON_RESOURCE);
	private static final String[]			RECOMMENDED_PATH = {"menu.pipe","start"};
	private static final FieldDescriptor[] 	EMPTY_FIELD_LIST = new FieldDescriptor[0]; 
	private static final String[] 			EMPTY_ACTION_LIST = new String[0];

	private final Localizer					localizer;
	
	public StartNode(final Localizer localizer) {
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else {
			this.localizer = localizer;
		}
	}

	@Override
	public PluginInstance newInstance(final Localizer localizer, final LoggerFacade logger) throws LocalizationException, SyntaxException, ContentException, IOException {
		return new StartContent(this,localizer,logger);
	}

	@Override
	public String getPluginId() {
		return Constants.PIPE_START_NODE;
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
	public String[] getUsesIds(Localizer parent) throws LocalizationException {
		return EMPTY_ACTION_LIST;
	}

	@Override
	public String[] getTagsIds(Localizer parent) throws LocalizationException {
		return EMPTY_ACTION_LIST;
	}

	@Override
	public String[] getSeeAlsoIds(Localizer parent) throws LocalizationException {
		return EMPTY_ACTION_LIST;
	}

	@Override
	public boolean hasField(final String fieldName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public FieldDescriptor getFieldDescriptor(final String fieldName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FieldDescriptor[] getInnerControls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FieldDescriptor[] getOuterControls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getActions() {
		// TODO Auto-generated method stub
		return null;
	}
}
