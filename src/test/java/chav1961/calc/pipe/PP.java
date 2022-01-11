package chav1961.calc.pipe;

import org.junit.Assert;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.ModuleAccessor;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.ui.interfaces.Action;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.RefreshMode;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.calc.contour.ContourPlugin/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.file",tooltip="menu.file",help="testSet3")
@Action(resource=@LocaleResource(value="menu.file",tooltip="menu.file"),actionString="calculate")
@PluginProperties(width=500,height=150,leftWidth=250,svgURI="schema.SVG",pluginIconURI="frameIcon.png",desktopIconURI="desktopIcon.png",resizable=false)
public class PP implements FormManager<Object,PP>, ModuleAccessor {
	private final LoggerFacade	logger;
	
	@LocaleResource(value="menu.file",tooltip="menu.file")
	public String	test = "test";

	public PP(final LoggerFacade logger) {
		this.logger = logger;
	}
	
	@Override
	public RefreshMode onField(PP inst, Object id, String fieldName, Object oldValue, boolean beforeCommit) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onAction(final PP inst, final Object id, final String actionName, final Object... parameter) throws FlowException, LocalizationException {
		if ("app:action:/PP.calculate".equals(actionName)) {
			test = test + test;
			return RefreshMode.DEFAULT;
		}
		else {
			Assert.fail("Illegal application URI ");
			return RefreshMode.REJECT;
		}
	}
	
	@Override
	public LoggerFacade getLogger() {
		return logger;
	}

	@Override
	public void allowUnnamedModuleAccess(final Module... unnamedModules) {
		for (Module item : unnamedModules) {
			this.getClass().getModule().addExports(this.getClass().getPackageName(),item);
		}
	}
}