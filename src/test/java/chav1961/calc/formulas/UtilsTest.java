package chav1961.calc.formulas;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {
	@Test
	public void inductanceOneLayerCoilTest() {
		Assert.assertEquals(Utils.inductanceOneLayerCoil(10,10f,10f,0.1f),6.8f,0.1f);
		Assert.assertEquals(Utils.inductanceOneLayerCoil(10,10f,3f,0.1f),13.7f,0.1f);

		try{Utils.inductanceOneLayerCoil(0,10f,10f,0.1f);
			Assert.fail("Mandatory exception was not detected (1-st argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.inductanceOneLayerCoil(10,0f,10f,0.1f);
			Assert.fail("Mandatory exception was not detected (2-nd argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.inductanceOneLayerCoil(10,10f,0f,0.1f);
			Assert.fail("Mandatory exception was not detected (3-rd argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.inductanceOneLayerCoil(10,10f,10f,0f);
			Assert.fail("Mandatory exception was not detected (4-th argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
	}
	
	@Test
	public void numberOfCoilsOneLayerCoilTest() {
		Assert.assertEquals(Utils.numberOfCoilsOneLayerCoil(10f,10f,10f,0.1f),12);
		Assert.assertEquals(Utils.numberOfCoilsOneLayerCoil(10f,10f,3f,0.1f),8);

		try{Utils.numberOfCoilsOneLayerCoil(0f,10f,3f,0.1f);
			Assert.fail("Mandatory exception was not detected (1-st argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.numberOfCoilsOneLayerCoil(10f,0f,3f,0.1f);
			Assert.fail("Mandatory exception was not detected (2-nd argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.numberOfCoilsOneLayerCoil(10f,10f,0f,0.1f);
			Assert.fail("Mandatory exception was not detected (3-rd argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.numberOfCoilsOneLayerCoil(10f,10f,3f,0.0f);
			Assert.fail("Mandatory exception was not detected (4-th argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
	}
	
	@Test
	public void inductanceRingCoilTest() {
		Assert.assertEquals(Utils.inductanceRingCoil(11,20f,10f,5f,140),11.75f,0.1f);
		Assert.assertEquals(Utils.inductanceRingCoil(11,20f,15f,5f,140),4.85f,0.1f);

		try{Utils.inductanceRingCoil(0,20f,10f,5f,140);
			Assert.fail("Mandatory exception was not detected (1-st argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.inductanceRingCoil(11,0f,10f,5f,140);
			Assert.fail("Mandatory exception was not detected (2-nd argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.inductanceRingCoil(11,20f,0f,5f,140);
			Assert.fail("Mandatory exception was not detected (3-rd argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.inductanceRingCoil(11,10f,20f,5f,140);
			Assert.fail("Mandatory exception was not detected (2-st argument is less than 3-rd one)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.inductanceRingCoil(11,20f,10f,0f,140);
			Assert.fail("Mandatory exception was not detected (4-th argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.inductanceRingCoil(11,20f,10f,5f,0);
			Assert.fail("Mandatory exception was not detected (5-th argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
	}
	
	@Test
	public void coilsRingCoilTest() {
		Assert.assertEquals(Utils.coilsRingCoil(100f,20f,10f,5f,140),32);
		Assert.assertEquals(Utils.coilsRingCoil(100f,20f,15f,5f,140),50);
		
		try{Utils.coilsRingCoil(0f,20f,10f,5f,140);
			Assert.fail("Mandatory exception was not detected (1-st argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.coilsRingCoil(100f,0f,10f,5f,140);
			Assert.fail("Mandatory exception was not detected (2-nd argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.coilsRingCoil(100f,20f,0f,5f,140);
			Assert.fail("Mandatory exception was not detected (3-rd argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.coilsRingCoil(100f,10f,20f,5f,140);
			Assert.fail("Mandatory exception was not detected (2-nd argument is less than 3-rd one)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.coilsRingCoil(100f,20f,10f,0f,140);
			Assert.fail("Mandatory exception was not detected (4-th argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.coilsRingCoil(100f,20f,10f,5f,0);
			Assert.fail("Mandatory exception was not detected (5-th argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
	}
	
	@Test
	public void inductionRingCoilTest() {
		Assert.assertEquals(Utils.inductionRingCoil(1,100,20f,10f,140),0.37f,0.01f);

		try{Utils.inductionRingCoil(0,100,20f,10f,140);
			Assert.fail("Mandatory exception was not detected (1-st argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.inductionRingCoil(1,0,20f,10f,140);
			Assert.fail("Mandatory exception was not detected (2-nd argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.inductionRingCoil(1,100,0f,10f,140);
			Assert.fail("Mandatory exception was not detected (3-rd argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.inductionRingCoil(1,100,20f,0f,140);
			Assert.fail("Mandatory exception was not detected (4-th argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.inductionRingCoil(1,100,10f,20f,140);
			Assert.fail("Mandatory exception was not detected (3-rd argument is less than 4-th one)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.inductionRingCoil(1,100,20f,10f,0);
			Assert.fail("Mandatory exception was not detected (1-st argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
	}
	
	@Test
	public void wireLength4RingTest() {
		Assert.assertArrayEquals(Utils.wireLength4Ring(10,0.1f,20f,10f,5f),new float[]{204f,20.2f,9.8f,5.4f},0.1f);
		Assert.assertNull(Utils.wireLength4Ring(1000,1.0f,20f,10f,5f));
		
		try{Utils.wireLength4Ring(0,0.1f,20f,10f,5f);
			Assert.fail("Mandatory exception was not detected (1-st argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.wireLength4Ring(10,0f,20f,10f,5f);
			Assert.fail("Mandatory exception was not detected (2-nd argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.wireLength4Ring(10,0.1f,0f,10f,5f);
			Assert.fail("Mandatory exception was not detected (3-rd argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.wireLength4Ring(10,0.1f,20f,0f,5f);
			Assert.fail("Mandatory exception was not detected (4-th argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.wireLength4Ring(10,0.1f,10f,20f,5f);
			Assert.fail("Mandatory exception was not detected (3-rd argument is less then 4-th one)");
		} catch (IllegalArgumentException exc) {
		}
		try{Utils.wireLength4Ring(10,0.1f,20f,10f,0f);
			Assert.fail("Mandatory exception was not detected (5-th argument is non-positive)");
		} catch (IllegalArgumentException exc) {
		}
	}
}
