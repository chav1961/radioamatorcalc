package chav1961.calc.script;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import chav1961.calc.script.LocalVarStack.ValueType;

public class LocalVarStackTest {
	@Test
	public void basicTest() {
		try(final LocalVarStack	lvs = new LocalVarStack()) {
			
			Assert.assertFalse(lvs.isDefined("name"));
			Assert.assertFalse(lvs.exists("name"));
			
			try{lvs.isDefined(null);
				Assert.fail("Mandatory exception was not detected (null 1-st argument)");
			} catch (IllegalArgumentException exc) {
			}
			try{lvs.isDefined("");
				Assert.fail("Mandatory exception was not detected (empty 1-st argument)");
			} catch (IllegalArgumentException exc) {
			}

			try{lvs.exists(null);
				Assert.fail("Mandatory exception was not detected (null 1-st argument)");
			} catch (IllegalArgumentException exc) {
			}
			try{lvs.exists("");
				Assert.fail("Mandatory exception was not detected (empty 1-st argument)");
			} catch (IllegalArgumentException exc) {
			}
			
			Assert.assertTrue(lvs.add("name",ValueType.INTEGER));
			Assert.assertTrue(lvs.exists("name"));
			Assert.assertTrue(lvs.isDefined("name"));
			Assert.assertFalse(lvs.add("name",ValueType.INTEGER));
			
			try{lvs.add(null,ValueType.INTEGER);
				Assert.fail("Mandatory exception was not detected (null 1-st argument)");
			} catch (IllegalArgumentException exc) {
			}
			try{lvs.add("",ValueType.INTEGER);
				Assert.fail("Mandatory exception was not detected (empty 1-st argument)");
			} catch (IllegalArgumentException exc) {
			}
			try{lvs.add("name",null);
				Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
			} catch (NullPointerException exc) {
			}

			Assert.assertTrue(lvs.add("name2",ValueType.INTEGER,10));
			Assert.assertTrue(lvs.exists("name2"));
			Assert.assertTrue(lvs.isDefined("name2"));
			Assert.assertFalse(lvs.add("name2",ValueType.INTEGER,10));
			
			try{lvs.add(null,ValueType.INTEGER,10);
				Assert.fail("Mandatory exception was not detected (null 1-st argument)");
			} catch (IllegalArgumentException exc) {
			}
			try{lvs.add("",ValueType.INTEGER,10);
				Assert.fail("Mandatory exception was not detected (empty 1-st argument)");
			} catch (IllegalArgumentException exc) {
			}
			try{lvs.add("name2",null,10);
				Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
			} catch (NullPointerException exc) {
			}
			
			Assert.assertEquals(ValueType.INTEGER,lvs.getType("name"));

			try{lvs.getType(null);
				Assert.fail("Mandatory exception was not detected (null 1-st argument)");
			} catch (IllegalArgumentException exc) {
			}
			try{lvs.getType("");
				Assert.fail("Mandatory exception was not detected (empty 1-st argument)");
			} catch (IllegalArgumentException exc) {
			}
			try{lvs.getType("unknown");
				Assert.fail("Mandatory exception was not detected (name doesn't exist)");
			} catch (IllegalArgumentException exc) {
			}
			
			try(final LocalVarStack	nested = lvs.push()) {
				Assert.assertTrue(nested.exists("name2"));
				Assert.assertFalse(nested.isDefined("name2"));
				Assert.assertFalse(nested.exists("unknown"));
				Assert.assertFalse(nested.isDefined("unknown"));
				
				Assert.assertTrue(nested.add("name3",ValueType.STRING,"test"));
				Assert.assertTrue(nested.exists("name3"));
				Assert.assertTrue(nested.isDefined("name3"));

				Assert.assertEquals(ValueType.INTEGER,nested.getType("name"));
				Assert.assertEquals(ValueType.STRING,nested.getType("name3"));
				
				Assert.assertNull(nested.getValue("name"));
				Assert.assertEquals(Integer.valueOf(10),nested.getValue("name2"));
				Assert.assertEquals("test",nested.getValue("name3"));
				
				try{nested.getValue(null);
					Assert.fail("Mandatory exception was not detected (null 1-st argument)");
				} catch (IllegalArgumentException exc) {
				}
				try{nested.getValue("");
					Assert.fail("Mandatory exception was not detected (empty 1-st argument)");
				} catch (IllegalArgumentException exc) {
				}
				try{nested.getValue("unknown");
					Assert.fail("Mandatory exception was not detected (name doesn't exist)");
				} catch (IllegalArgumentException exc) {
				}
				
				nested.setValue("name",20);
				nested.setValue("name2",20);
				nested.setValue("name3","test test");

				Assert.assertEquals(Integer.valueOf(20),nested.getValue("name"));
				Assert.assertEquals(Integer.valueOf(20),nested.getValue("name2"));
				Assert.assertEquals("test test",nested.getValue("name3"));

				try{nested.setValue(null,null);
					Assert.fail("Mandatory exception was not detected (null 1-st argument)");
				} catch (IllegalArgumentException exc) {
				}
				try{nested.setValue("",null);
					Assert.fail("Mandatory exception was not detected (empty 1-st argument)");
				} catch (IllegalArgumentException exc) {
				}
				try{nested.setValue("unknown",null);
					Assert.fail("Mandatory exception was not detected (name doesn't exist)");
				} catch (IllegalArgumentException exc) {
				}
				try{nested.setValue("name","test");
					Assert.fail("Mandatory exception was not detected (illegal type of 2-nd argument)");
				} catch (IllegalArgumentException exc) {
				}
			}
			Assert.assertFalse(lvs.exists("name3"));
			Assert.assertFalse(lvs.isDefined("name3"));
		}
	}
	
	@Test
	public void valueTypeTest() {
		Assert.assertEquals(ValueType.BOOLEAN,ValueType.classify(true));
		Assert.assertEquals(ValueType.INTEGER,ValueType.classify(100));
		Assert.assertEquals(ValueType.REAL,ValueType.classify(100.0));
		Assert.assertEquals(ValueType.STRING,ValueType.classify("test"));
		Assert.assertNull(ValueType.classify(new Object()));
		Assert.assertNull(ValueType.classify(null));
	}
}
