package chav1961.calc.environment;

import org.junit.Assert;
import org.junit.Test;

public class PipeParameterWrapperTest {
	@Test
	public void test() {
		final PipeParameterWrapper	wr1 = new PipeParameterWrapper("id","instance","field",int.class)
									, wr2 = new PipeParameterWrapper("id","instance","field",int.class)
									, wr3 = new PipeParameterWrapper("id","instance","field",double.class);
		
		Assert.assertEquals(wr1.getPluginId(),"id");
		Assert.assertEquals(wr1.getPluginInstanceName(),"instance");
		Assert.assertEquals(wr1.getPluginFieldName(),"field");
		Assert.assertEquals(wr1.getPluginFieldType(),int.class);
		Assert.assertEquals(wr1,wr2);
		Assert.assertEquals(wr1.toString(),wr2.toString());
		
		Assert.assertFalse(wr1.equals(wr3));
		wr3.setPluginFieldType(int.class);
		Assert.assertEquals(wr1,wr3);

		wr3.setPluginId("idnew");
		Assert.assertEquals(wr3.getPluginId(),"idnew");
		wr3.setPluginInstanceName("instancenew");
		Assert.assertEquals(wr3.getPluginInstanceName(),"instancenew");
		wr3.setPluginFieldName("fieldnew");
		Assert.assertEquals(wr3.getPluginFieldName(),"fieldnew");
		
		
		try{new PipeParameterWrapper(null,"instance","field",int.class);
			Assert.fail("Mandatiry exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{new PipeParameterWrapper("","instance","field",int.class);
			Assert.fail("Mandatiry exception was not detected (empty 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{new PipeParameterWrapper("id",null,"field",int.class);
			Assert.fail("Mandatiry exception was not detected (null 2-nd argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{new PipeParameterWrapper("id","","field",int.class);
			Assert.fail("Mandatiry exception was not detected (empty 2-nd argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{new PipeParameterWrapper("id","instance",null,int.class);
			Assert.fail("Mandatiry exception was not detected (null 3-rd argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{new PipeParameterWrapper("id","instance","",int.class);
			Assert.fail("Mandatiry exception was not detected (empty 3-rd argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{new PipeParameterWrapper("id","instance","field",null);
			Assert.fail("Mandatiry exception was not detected (null 4-th argument)");
		} catch (NullPointerException exc) {
		}
		
		try{wr1.setPluginId(null);
			Assert.fail("Mandatiry exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{wr1.setPluginId("");
			Assert.fail("Mandatiry exception was not detected (empty 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
	
		try{wr1.setPluginInstanceName(null);
			Assert.fail("Mandatiry exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{wr1.setPluginInstanceName("");
			Assert.fail("Mandatiry exception was not detected (empty 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}

		try{wr1.setPluginFieldName(null);
			Assert.fail("Mandatiry exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{wr1.setPluginFieldName("");
			Assert.fail("Mandatiry exception was not detected (empty 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}

		try{wr1.setPluginFieldType(null);
			Assert.fail("Mandatiry exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
	}
}
