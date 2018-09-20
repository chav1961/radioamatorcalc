package util;

import org.junit.Assert;

import chav1961.calc.interfaces.PluginInterface;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.PureLibLocalizer;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.interfacers.FormManager;
import chav1961.purelib.ui.interfacers.FormManager.RefreshMode;

public class PluginTestingUtil {
	public static void verifyPluginDescriptor(final PluginInterface pi) {
		try(final Localizer	localizer = new PureLibLocalizer()) {
			Assert.assertNotNull(pi.getPluginId());		Assert.assertTrue(pi.getPluginId().length() > 0);
			Assert.assertNotNull(pi.getCaptionId());	Assert.assertTrue(pi.getCaptionId().length() > 0);
			Assert.assertNotNull(pi.getToolTipId());	Assert.assertTrue(pi.getToolTipId().length() > 0);
			Assert.assertNotNull(pi.getHelpId());		Assert.assertTrue(pi.getHelpId().length() > 0);
	
			Assert.assertNotNull(pi.getIcon());
			Assert.assertNotNull(pi.getMiniIconURL());
			Assert.assertNotNull(pi.getLeftIconURL());
			checkStringArray(pi.getRecommendedNavigationPath());
			
			checkStringArray(pi.getUsesIds(localizer));
			checkStringArray(pi.getTagsIds(localizer));
			checkStringArray(pi.getSeeAlsoIds(localizer));
			
			Assert.assertNotNull(pi.getLocalizerAssociated(localizer));

			try(final Localizer		temp = pi.getLocalizerAssociated(localizer)) {
				Assert.assertTrue(temp.containsKey(pi.getCaptionId()));
				Assert.assertTrue(temp.containsKey(pi.getToolTipId()));
				Assert.assertTrue(temp.containsKey(pi.getHelpId()));
			}
		} catch (LocalizationException e) {
			Assert.fail("Error verifying plugin descriptor: "+e.getMessage());
		} 
	}

	public static void playScenario(final FormManager<?,?> instance, final ScenarioStep... steps) {
		
	}

	private static void checkStringArray(final String[] array) {
		Assert.assertNotNull(array);
		Assert.assertTrue(array.length > 0);
		for (String item : array) {
			Assert.assertNotNull(item);
			Assert.assertTrue(item.length() > 0);
		}
	}
	
	
	public static abstract class ScenarioStep {
		public abstract boolean execute(final FormManager<?,?> instance);
	}
	
	public static class ReadValue extends ScenarioStep {
		public ReadValue(final int fieldName, final Object fieldValue) {
			
		}

		@Override
		public boolean execute(final FormManager<?, ?> instance) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	public static class AssignValue extends ScenarioStep {
		public AssignValue(final int fieldName, final Object fieldValue, final RefreshMode... awaited) {
			
		}

		@Override
		public boolean execute(final FormManager<?, ?> instance) {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	public static class ProcessAction extends ScenarioStep {
		public ProcessAction(final String action, final RefreshMode awaited) {
			
		}
		
		@Override
		public boolean execute(FormManager<?, ?> instance) {
			// TODO Auto-generated method stub
			return false;
		}		
	}

}
