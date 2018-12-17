package util;


import java.lang.reflect.Field;

import org.junit.Assert;

import chav1961.calc.interfaces.PluginInterface;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.PureLibLocalizer;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.interfacers.FormManager;
import chav1961.purelib.ui.interfacers.RefreshMode;

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

	public static boolean playScenario(final FormManager<?,?> instance, final ScenarioStep... steps) {
		if (instance == null) {
			throw new IllegalArgumentException("Instance can't be null");
		}
		else if (steps == null || steps.length == 0) {
			throw new IllegalArgumentException("Scenario step list can't be null or empty array");
		}
		else {
			for (ScenarioStep item : steps) {
				if (!item.execute(instance)) {
					return false;
				}
			}
			return true;
		}
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
		public abstract boolean execute(final FormManager instance);
	}
	
	public static class CompareValue extends ScenarioStep {
		private final String	fieldName;
		private final Object	awaitedValue;
		private final double	precision;
		
		public CompareValue(final String fieldName, final Object awaitedValue) {
			this(fieldName,awaitedValue,0.0);
		}

		public CompareValue(final String fieldName, final Object awaitedValue, final double precision) {
			if (fieldName == null || fieldName.isEmpty()) {
				throw new IllegalArgumentException("Field name ca't be null or empty");
			}
			else if (precision < 0) {
				throw new IllegalArgumentException("Precision to test can't be negative");
			}
			else {
				this.fieldName = fieldName;
				this.awaitedValue = awaitedValue;
				this.precision = precision;
			}
		}
		
		@Override
		public boolean execute(final FormManager instance) {
			if (instance == null) {
				throw new NullPointerException("Instance can't be null"); 
			}
			else {
				try{final Class<FormManager<?, ?>>	clazz = (Class<FormManager<?, ?>>) instance.getClass();
					final Field						f = clazz.getDeclaredField(fieldName);
					
					f.setAccessible(true);
					final Object					value = f.get(instance);
					final double					value1, value2;
					boolean							usePrecision1 = false, usePrecision2 = false;

					if (value instanceof Float) {
						value1 = ((Float)value).doubleValue();
						usePrecision1 = true;
					}
					else if (value instanceof Double) {
						value1 = ((Double)value).doubleValue();
						usePrecision1 = true;
					}
					else {
						value1 = 0;
					}
					if (awaitedValue instanceof Float) {
						value2 = ((Float)awaitedValue).doubleValue();
						usePrecision2 = true;
					}
					else if (awaitedValue instanceof Double) {
						value2 = ((Double)awaitedValue).doubleValue();
						usePrecision2 = true;
					}
					else {
						value2 = 0;
					}
					if (usePrecision1 && usePrecision2) {
						return Math.abs(value1 - value2) < precision;
					}
					else {
						return value == awaitedValue || value != null && value.equals(awaitedValue);
					}
				} catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
					throw new IllegalArgumentException(e);
				}
			}
		}
	}
	
	public static class AssignValue extends ScenarioStep {
		private final String 		fieldName;
		private final Object 		fieldValue;
		private final RefreshMode[]	awaited;
		
		public AssignValue(final String fieldName, final Object fieldValue, final RefreshMode... awaited) {
			if (fieldName == null || fieldName.isEmpty()) {
				throw new IllegalArgumentException("Field name ca't be null or empty");
			}
			else if (awaited == null || awaited.length == 0) {
				throw new IllegalArgumentException("Awaited refresh mode can't be null or empty");
			}
			else {
				this.fieldName = fieldName;
				this.fieldValue = fieldValue;
				this.awaited = awaited;
			}
		}

		@Override
		public boolean execute(final FormManager instance) {
			if (instance == null) {
				throw new NullPointerException("Instance can't be null"); 
			}
			else {
				try{final Class<FormManager<?, ?>>	clazz = (Class<FormManager<?, ?>>) instance.getClass();
					final Field						f = clazz.getDeclaredField(fieldName);
					
					f.setAccessible(true);
					final Object					oldValue = f.get(instance);
					
					f.set(instance,fieldValue);
					final RefreshMode				result = instance.onField(instance,null,fieldName,oldValue);
					
					for (RefreshMode item : awaited) {
						if (result == item) {
							return true;
						}
					}
					return false;
				} catch (NoSuchFieldException | SecurityException | IllegalAccessException | LocalizationException | FlowException e) {
					throw new IllegalArgumentException(e);
				}
			}
		}
	}
	
	public static class ProcessAction extends ScenarioStep {
		private final String 		action;
		private final Object 		parameter;
		private final RefreshMode[]	awaited;
		
		public ProcessAction(final String action, final Object parameter, final RefreshMode... awaited) {
			if (action == null || action.isEmpty()) {
				throw new IllegalArgumentException("Field name ca't be null or empty");
			}
			else if (awaited == null || awaited.length == 0) {
				throw new IllegalArgumentException("Awaited refresh mode can't be null or empty");
			}
			else {
				this.action = action;
				this.parameter = parameter;
				this.awaited = awaited;
			}
		}
		
		@Override
		public boolean execute(FormManager instance) {
			try{final RefreshMode				result = instance.onAction(instance,null,action,parameter);
			
				for (RefreshMode item : awaited) {
					if (result == item) {
						return true;
					}
				}
				return false;
			} catch (SecurityException | LocalizationException | FlowException e) {
				throw new IllegalArgumentException(e);
			}
		}		
	}

}
