package chav1961.calc.elements;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import chav1961.calc.elements.coils.CoilsCalculationType;
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
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.RefreshMode;
import chav1961.purelib.ui.swing.AutoBuiltForm;
import util.PluginTestingUtil;

public class CoilsTest {
	@Test
	public void SingleCoilsTest() throws LocalizationException, SyntaxException, ContentException, IOException {
		final PluginInterface		pi = new SingleCoilsService();
			
		PluginTestingUtil.verifyPluginDescriptor(pi);
		
		try(final Localizer			parent = new PureLibLocalizer();
			final LoggerFacade		logger = new NullLoggerFacade()) {
			final PluginInstance	inst = pi.newInstance(parent, logger);
			
			Assert.assertTrue(PluginTestingUtil.playScenario((FormManager<?,?>)((AutoBuiltForm<?>)inst).getFormManagerAssociated()
					,new PluginTestingUtil.AssignValue("length",10.0f,RefreshMode.RECORD_ONLY)
					,new PluginTestingUtil.AssignValue("diameter",10.0f,RefreshMode.RECORD_ONLY)
					,new PluginTestingUtil.AssignValue("wireDiameter",0.1f,RefreshMode.RECORD_ONLY)
					,new PluginTestingUtil.AssignValue("calcType",CoilsCalculationType.INDUCTANCE,RefreshMode.RECORD_ONLY)
					,new PluginTestingUtil.AssignValue("coils",10,RefreshMode.RECORD_ONLY)
					,new PluginTestingUtil.ProcessAction("calculate",null,RefreshMode.RECORD_ONLY)
					,new PluginTestingUtil.CompareValue("inductance",10,0.01)
					));
			Assert.assertTrue(PluginTestingUtil.playScenario((FormManager<?,?>)inst
					,new PluginTestingUtil.AssignValue("length",10.0f,RefreshMode.RECORD_ONLY)
					,new PluginTestingUtil.AssignValue("diameter",10.0f,RefreshMode.RECORD_ONLY)
					,new PluginTestingUtil.AssignValue("wireDiameter",0.1f,RefreshMode.RECORD_ONLY)
					,new PluginTestingUtil.AssignValue("calcType",CoilsCalculationType.NUMBER_OF_COILS,RefreshMode.RECORD_ONLY)
					,new PluginTestingUtil.AssignValue("inductance",10,RefreshMode.RECORD_ONLY)
					,new PluginTestingUtil.ProcessAction("calculate",null,RefreshMode.RECORD_ONLY)
					,new PluginTestingUtil.CompareValue("coils",10,0.01)
					));
			
			Assert.assertFalse(PluginTestingUtil.playScenario((FormManager<?,?>)inst
					,new PluginTestingUtil.AssignValue("length",0.0f,RefreshMode.RECORD_ONLY)
					));
			Assert.assertFalse(PluginTestingUtil.playScenario((FormManager<?,?>)inst
					,new PluginTestingUtil.AssignValue("diameter",0.0f,RefreshMode.RECORD_ONLY)
					));
			Assert.assertFalse(PluginTestingUtil.playScenario((FormManager<?,?>)inst
					,new PluginTestingUtil.AssignValue("wireDiameter",0.0f,RefreshMode.RECORD_ONLY)
					));
			Assert.assertFalse(PluginTestingUtil.playScenario((FormManager<?,?>)inst
					,new PluginTestingUtil.AssignValue("calcType",CoilsCalculationType.NUMBER_OF_COILS,RefreshMode.RECORD_ONLY)
					,new PluginTestingUtil.AssignValue("coils",0,RefreshMode.RECORD_ONLY)
					));
			Assert.assertFalse(PluginTestingUtil.playScenario((FormManager<?,?>)inst
					,new PluginTestingUtil.AssignValue("calcType",CoilsCalculationType.INDUCTANCE,RefreshMode.RECORD_ONLY)
					,new PluginTestingUtil.AssignValue("inductance",0.0f,RefreshMode.RECORD_ONLY)
					));
		}
	}
}
