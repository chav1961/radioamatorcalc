package chav1961.calc.pipe;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import javax.swing.JTabbedPane;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import chav1961.calc.interfaces.PipeItemRuntime.PipeStepReturnCode;
import chav1961.calc.windows.PipeManager;
import chav1961.calc.windows.PipeTab;
import chav1961.calc.windows.PipeManagerSerialForm.PluginSpecific;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.FieldFormat;
import chav1961.purelib.model.ModelUtils;
import chav1961.purelib.model.ModelUtilsTest;
import chav1961.purelib.model.MutableContentNodeMetadata;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public class PipeFramesRuntimeTest {
	private final JTabbedPane			pane = new JTabbedPane();
	private final URI					localizerURI = URI.create("i18n:xml:root://chav1961.calc.Application/chav1961/calculator/i18n/i18n.xml");
	private Localizer					localizer;
	private PipeTab						tab;
	private ContentMetadataInterface	general; 
	private PipeManager					mgr;
	
	@Before
	public void prepare() throws Exception {
		localizer = LocalizerFactory.getLocalizer(localizerURI);
		PureLibSettings.PURELIB_LOCALIZER.push(localizer);
		tab = new PipeTab(pane,localizer,PureLibSettings.CURRENT_LOGGER);
		try(final InputStream	is = PipeManager.class.getResourceAsStream("pipe.xml")) {
			general = ContentModelFactory.forXmlDescription(is);
			mgr = new PipeManager(tab,localizer,PureLibSettings.CURRENT_LOGGER,general);
		}
	}
	
	@Test
	public void initialPipeFrameTest() throws ContentException, IOException, FlowException {
		final ContentNodeMetadata	initial = new MutableContentNodeMetadata("initial",InitialPipeFrame.class,"./initial",localizerURI,"testSet1","testSet1", null, null, URI.create("app:action:/start"),null);
		final InitialPipeFrame		ipf = new InitialPipeFrame(1,mgr,localizer, initial, general);
		final PluginSpecific		ps = new PluginSpecific(), ps2 = new PluginSpecific();
		
		ps.initialCode = "test := \"123456\"; ";
		ps.fields = new MutableContentNodeMetadata[] {new MutableContentNodeMetadata("test",String.class,"./test",localizerURI,"testSet1",null,null,new FieldFormat(String.class), ModelUtils.buildUriByClassAndField(TestClass.class,"test"), null)};
		
		// Serialization test
		ipf.deserializeFrame(ps);
		ipf.serializeFrame(ps2);
		Assert.assertEquals(ps,ps2);
		
		// Pipe processing test
		final Object	temporary = ipf.preparePipeItem();
		
		Assert.assertTrue(temporary instanceof Map);
		
		try{ipf.storeIncomingValue(temporary,ps.fields[0],"123");
			Assert.fail("Mandatory exception was not detected (initial node doesn't support this method)");
		} catch (IllegalStateException exc) {
		}

		Assert.assertNull(ipf.getOutgoingValue(temporary,ps.fields[0]));
		
		Assert.assertEquals(PipeStepReturnCode.CONTINUE,ipf.processPipeStep(temporary,PureLibSettings.CURRENT_LOGGER,false));
		
		Assert.assertEquals("123456",ipf.getOutgoingValue(temporary,ps.fields[0]));
		
		ipf.unpreparePipeItem(temporary);
		
		// Exceptions test
		try{ipf.deserializeFrame(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try{ipf.serializeFrame(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}

		try{ipf.processPipeStep(null,PureLibSettings.CURRENT_LOGGER,false);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{ipf.processPipeStep("illegal",PureLibSettings.CURRENT_LOGGER,false);
			Assert.fail("Mandatory exception was not detected (illegal 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{ipf.processPipeStep(temporary,null,false);
			Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}
		
		try{ipf.getOutgoingValue(null,ps.fields[0]);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{ipf.getOutgoingValue("illegal",ps.fields[0]);
			Assert.fail("Mandatory exception was not detected (illegal 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{ipf.getOutgoingValue(temporary,null);
			Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}

		try{ipf.unpreparePipeItem(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{ipf.unpreparePipeItem("illegal");
			Assert.fail("Mandatory exception was not detected (illegal 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
	}

	@Test
	public void terminalPipeFrameTest() throws ContentException, IOException, FlowException {
		final ContentNodeMetadata	terminal = new MutableContentNodeMetadata("terminal",TerminalPipeFrame.class,"./terminal",localizerURI,"testSet1","testSet1", null, null, URI.create("app:action:/stop"),null); 
		final TerminalPipeFrame		tpf = new TerminalPipeFrame(1,mgr,localizer,terminal,general);
		final PluginSpecific		ps = new PluginSpecific(), ps2 = new PluginSpecific();
		
		ps.message = "the end";
		ps.isError = true;
		ps.fields = new MutableContentNodeMetadata[] {new MutableContentNodeMetadata("test",String.class,"./test",localizerURI,"testSet1",null,null,new FieldFormat(String.class), ModelUtils.buildUriByClassAndField(TestClass.class,"test"), null)};
		
		// Serialization test
		tpf.deserializeFrame(ps);
		tpf.serializeFrame(ps2);
		Assert.assertEquals(ps,ps2);

		// Pipe processing test
		final Object	temporary = tpf.preparePipeItem();
		
		Assert.assertTrue(temporary instanceof Map);
		
		tpf.storeIncomingValue(temporary,ps.fields[0],"123");

		Assert.assertEquals(PipeStepReturnCode.TERMINATE_FALSE,tpf.processPipeStep(temporary,PureLibSettings.CURRENT_LOGGER,true));
		
		try{tpf.getOutgoingValue(temporary,ps.fields[0]);
			Assert.fail("Mandatory exception was not detected (initial node doesn't support this method)");
		} catch (IllegalStateException exc) {
		}
		
		Assert.assertEquals("123",((Map<String,?>)temporary).get("#1.test")); 
		
		tpf.unpreparePipeItem(temporary);
		
		// Exceptions test
		try{tpf.deserializeFrame(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try{tpf.serializeFrame(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}

		try{tpf.processPipeStep(null,PureLibSettings.CURRENT_LOGGER,false);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{tpf.processPipeStep("illegal",PureLibSettings.CURRENT_LOGGER,false);
			Assert.fail("Mandatory exception was not detected (illegal 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{tpf.processPipeStep(temporary,null,false);
			Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}
		
		try{tpf.storeIncomingValue(null,ps.fields[0],null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{tpf.storeIncomingValue("illegal",ps.fields[0],null);
			Assert.fail("Mandatory exception was not detected (illegal 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{tpf.storeIncomingValue(temporary,null,null);
			Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}

		try{tpf.unpreparePipeItem(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{tpf.unpreparePipeItem("illegal");
			Assert.fail("Mandatory exception was not detected (illegal 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
	}

	@Test
	public void calcPipeFrameTest() throws ContentException, IOException, FlowException {
		final ContentNodeMetadata	inner = new MutableContentNodeMetadata("inner",CalcPipeFrame.class,"./inner",localizerURI,"testSet1","testSet1", null, null, URI.create("app:action:/inner"),null); 
		final ContentNodeMetadata	outer = new MutableContentNodeMetadata("outer",CalcPipeFrame.class,"./outer",localizerURI,"testSet1","testSet1", null, null, URI.create("app:action:/outer"),null); 
		final CalcPipeFrame			cpf = new CalcPipeFrame(1,mgr,localizer,inner,outer,general);
		final PluginSpecific		ps = new PluginSpecific(), ps2 = new PluginSpecific();
		
		ps.program = "test := test+test; ";
		ps.fields = new MutableContentNodeMetadata[] {new MutableContentNodeMetadata("test",String.class,"./test",localizerURI,"testSet1",null,null,new FieldFormat(String.class), ModelUtils.buildUriByClassAndField(TestClass.class,"test"), null)};
		
		// Serialization test
		cpf.deserializeFrame(ps);
		cpf.serializeFrame(ps2);
		Assert.assertEquals(ps,ps2);
		
		// Pipe processing test
		final Object	temporary = cpf.preparePipeItem();
		
		Assert.assertTrue(temporary instanceof Map);
		
		cpf.storeIncomingValue(temporary,ps.fields[0],"123");

		Assert.assertEquals(PipeStepReturnCode.CONTINUE,cpf.processPipeStep(temporary,PureLibSettings.CURRENT_LOGGER,false));
		
		Assert.assertEquals("123123",cpf.getOutgoingValue(temporary,ps.fields[0]));
		
		cpf.unpreparePipeItem(temporary);

		// Exceptions test
		try{cpf.deserializeFrame(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try{cpf.serializeFrame(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}

		try{cpf.processPipeStep(null,PureLibSettings.CURRENT_LOGGER,false);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{cpf.processPipeStep("illegal",PureLibSettings.CURRENT_LOGGER,false);
			Assert.fail("Mandatory exception was not detected (illegal 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{cpf.processPipeStep(temporary,null,false);
			Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}
		
		try{cpf.storeIncomingValue(null,ps.fields[0],null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{cpf.storeIncomingValue("illegal",ps.fields[0],null);
			Assert.fail("Mandatory exception was not detected (illegal 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{cpf.storeIncomingValue(temporary,null,null);
			Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}

		try{cpf.getOutgoingValue(null,ps.fields[0]);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{cpf.getOutgoingValue("illegal",ps.fields[0]);
			Assert.fail("Mandatory exception was not detected (illegal 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{cpf.getOutgoingValue(temporary,null);
			Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}
		
		try{cpf.unpreparePipeItem(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{cpf.unpreparePipeItem("illegal");
			Assert.fail("Mandatory exception was not detected (illegal 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		
	}
}
