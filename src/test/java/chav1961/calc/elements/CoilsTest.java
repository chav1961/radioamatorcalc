package chav1961.calc.elements;

import java.io.IOException;

import org.junit.Test;

import chav1961.calc.elements.coils.singlecoilsplugin.SingleCoilsService;
import chav1961.calc.interfaces.PluginInterface;
import chav1961.calc.interfaces.PluginInterface.PluginInstance;
import chav1961.purelib.basic.NullLoggerFacade;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.PureLibLocalizer;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.interfacers.FormManager.RefreshMode;
import util.PluginTestingUtil;

public class CoilsTest {
	@Test
	public void SingleCoilsTest() throws LocalizationException, SyntaxException, ContentException, IOException {
		final PluginInterface		pi = new SingleCoilsService();
			
		PluginTestingUtil.verifyPluginDescriptor(pi);
		
		try(final Localizer			parent = new PureLibLocalizer();
			final LoggerFacade		logger = new NullLoggerFacade()) {
			
			final PluginInstance	inst = pi.newInstance(parent, logger);
			
			PluginTestingUtil.playScenario(inst,new PluginTestingUtil.AssignValue("",,RefreshMode. awaited));
		}
	}
}
