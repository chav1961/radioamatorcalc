package chav1961.calc.references;

import java.net.URI;

import java.net.URISyntaxException;

import org.junit.Test;
import org.junit.Assert;

import chav1961.calc.references.tubes.DiodeRecord;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.sql.content.ResultSetFactory;

public class ReferenceUtilTest {

	@Test
	public void loadTest() throws ContentException, URISyntaxException, LocalizationException {
		final DiodeRecord[]	result = ReferenceUtil.loadCSV(DiodeRecord.class,URI.create(ResultSetFactory.RESULTSET_PARSERS_SCHEMA+":csv:jar:"
										+getClass().getResource("test.zip").toURI()+"!/diodes/content.csv?allowemptycolumn=true"));
		
		Assert.assertEquals(3,result.length);
		Assert.assertEquals(6.3f,result[0].Uf,0.001f);
		
		try {ReferenceUtil.loadCSV(null,URI.create("./"));
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try {ReferenceUtil.loadCSV(DiodeRecord.class,null);
			Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}
	}

}
