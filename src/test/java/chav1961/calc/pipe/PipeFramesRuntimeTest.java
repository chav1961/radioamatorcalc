package chav1961.calc.pipe;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import javax.swing.JTabbedPane;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import chav1961.calc.interfaces.PipeItemRuntime.PipeConfigmation;
import chav1961.calc.interfaces.PipeItemRuntime.PipeStepReturnCode;
import chav1961.calc.windows.PipeManager;
import chav1961.calc.windows.PipeTab;
import chav1961.calc.windows.PipeManagerSerialForm.PluginSpecific;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.FieldFormat;
import chav1961.purelib.model.ModelUtils;
import chav1961.purelib.model.ModelUtilsTest;
import chav1961.purelib.model.MutableContentNodeMetadata;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;

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
		
		ps.initialCode = "test := \"123456\";";
		ps.fields = new MutableContentNodeMetadata[] {new MutableContentNodeMetadata("test",String.class,"./test",localizerURI,"testSet1",null,null,new FieldFormat(String.class), ModelUtils.buildUriByClassAndField(TestClass.class,"test"), null)};
		
		// Serialization test
		ipf.deserializeFrame(ps);
		ipf.serializeFrame(ps2);
		Assert.assertEquals(ps,ps2);
		
		// Pipe processing test
		final Object	temporary = ipf.preparePipeItem(PureLibSettings.INTERNAL_LOADER);
		
		Assert.assertTrue(temporary instanceof Map);
		
		try{ipf.storeIncomingValue(temporary,ps.fields[0],"123");
			Assert.fail("Mandatory exception was not detected (initial node doesn't support this method)");
		} catch (IllegalStateException exc) {
		}

		Assert.assertNull(ipf.getOutgoingValue(temporary,ps.fields[0]));
		
		Assert.assertEquals(PipeStepReturnCode.CONTINUE,ipf.processPipeStep(temporary,PureLibSettings.CURRENT_LOGGER,PipeConfigmation.ALWAYS_YES));
		
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

		try{ipf.processPipeStep(null,PureLibSettings.CURRENT_LOGGER,PipeConfigmation.ALWAYS_YES);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{ipf.processPipeStep("illegal",PureLibSettings.CURRENT_LOGGER,PipeConfigmation.ALWAYS_YES);
			Assert.fail("Mandatory exception was not detected (illegal 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{ipf.processPipeStep(temporary,null,PipeConfigmation.ALWAYS_YES);
			Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}
		try{ipf.processPipeStep(temporary,PureLibSettings.CURRENT_LOGGER,null);
			Assert.fail("Mandatory exception was not detected (null 3-rd argument)");
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
		final Object	temporary = tpf.preparePipeItem(PureLibSettings.INTERNAL_LOADER);
		
		Assert.assertTrue(temporary instanceof Map);
		
		tpf.storeIncomingValue(temporary,ps.fields[0],"123");

		Assert.assertEquals(PipeStepReturnCode.TERMINATE_FALSE,tpf.processPipeStep(temporary,PureLibSettings.CURRENT_LOGGER,PipeConfigmation.ALWAYS_YES));
		
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

		try{tpf.processPipeStep(null,PureLibSettings.CURRENT_LOGGER,PipeConfigmation.ALWAYS_YES);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{tpf.processPipeStep("illegal",PureLibSettings.CURRENT_LOGGER,PipeConfigmation.ALWAYS_YES);
			Assert.fail("Mandatory exception was not detected (illegal 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{tpf.processPipeStep(temporary,null,PipeConfigmation.ALWAYS_YES);
			Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}
		try{tpf.processPipeStep(temporary,PureLibSettings.CURRENT_LOGGER,null);
			Assert.fail("Mandatory exception was not detected (null 3-rd argument)");
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
		
		ps.program = "test := test+test;";
		ps.fields = new MutableContentNodeMetadata[] {new MutableContentNodeMetadata("test",String.class,"./test",localizerURI,"testSet1",null,null,new FieldFormat(String.class), ModelUtils.buildUriByClassAndField(TestClass.class,"test"), null)};
		
		// Serialization test
		cpf.deserializeFrame(ps);
		cpf.serializeFrame(ps2);
		Assert.assertEquals(ps,ps2);
		
		// Pipe processing test
		final Object	temporary = cpf.preparePipeItem(PureLibSettings.INTERNAL_LOADER);
		
		Assert.assertTrue(temporary instanceof Map);
		
		cpf.storeIncomingValue(temporary,ps.fields[0],"123");

		Assert.assertEquals(PipeStepReturnCode.CONTINUE,cpf.processPipeStep(temporary,PureLibSettings.CURRENT_LOGGER,PipeConfigmation.ALWAYS_YES));
		
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

		try{cpf.processPipeStep(null,PureLibSettings.CURRENT_LOGGER,PipeConfigmation.ALWAYS_YES);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{cpf.processPipeStep("illegal",PureLibSettings.CURRENT_LOGGER,PipeConfigmation.ALWAYS_YES);
			Assert.fail("Mandatory exception was not detected (illegal 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{cpf.processPipeStep(temporary,null,PipeConfigmation.ALWAYS_YES);
			Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}
		try{cpf.processPipeStep(temporary,PureLibSettings.CURRENT_LOGGER,null);
			Assert.fail("Mandatory exception was not detected (null 3-rd argument)");
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

	@Test
	public void conditionalPipeFrameTest() throws ContentException, IOException, FlowException {
		final ContentNodeMetadata	inner = new MutableContentNodeMetadata("inner",ConditionalPipeFrame.class,"./inner",localizerURI,"testSet1","testSet1",null,null,URI.create("app:action:/inner"),null); 
		final ContentNodeMetadata	onTrue = new MutableContentNodeMetadata("onTrue",ConditionalPipeFrame.class,"./ontrue",localizerURI,"testSet1","testSet1",null,null,URI.create("app:action:/ontrue"),null); 
		final ContentNodeMetadata	onFalse = new MutableContentNodeMetadata("onFalse",ConditionalPipeFrame.class,"./onfalse",localizerURI,"testSet1","testSet1",null,null,URI.create("app:action:/onfalse"),null); 
		final ConditionalPipeFrame	cpf = new ConditionalPipeFrame(1,mgr,localizer,inner,onTrue,onFalse,general);
		final PluginSpecific		ps = new PluginSpecific(), ps2 = new PluginSpecific();
		
		ps.expression = "test = \"123\"";
		ps.fields = new MutableContentNodeMetadata[] {new MutableContentNodeMetadata("test",String.class,"./test",localizerURI,"testSet1",null,null,new FieldFormat(String.class), ModelUtils.buildUriByClassAndField(TestClass.class,"test"), null)};
		
		// Serialization test
		cpf.deserializeFrame(ps);
		cpf.serializeFrame(ps2);
		Assert.assertEquals(ps,ps2);
		
		// Pipe processing test
		final Object	temporary = cpf.preparePipeItem(PureLibSettings.INTERNAL_LOADER);
		
		Assert.assertTrue(temporary instanceof Map);
		
		cpf.storeIncomingValue(temporary,ps.fields[0],"123");
		Assert.assertEquals(PipeStepReturnCode.CONTINUE_TRUE,cpf.processPipeStep(temporary,PureLibSettings.CURRENT_LOGGER,PipeConfigmation.ALWAYS_YES));
		
		cpf.storeIncomingValue(temporary,ps.fields[0],"456");
		Assert.assertEquals(PipeStepReturnCode.CONTINUE_FALSE,cpf.processPipeStep(temporary,PureLibSettings.CURRENT_LOGGER,PipeConfigmation.ALWAYS_YES));
		
		try{cpf.getOutgoingValue(temporary,ps.fields[0]);
			Assert.fail("Mandatory exception was not detected (conditional node doesn't support this method)");
		} catch (IllegalStateException exc) {
		}
		
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

		try{cpf.processPipeStep(null,PureLibSettings.CURRENT_LOGGER,PipeConfigmation.ALWAYS_YES);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{cpf.processPipeStep("illegal",PureLibSettings.CURRENT_LOGGER,PipeConfigmation.ALWAYS_YES);
			Assert.fail("Mandatory exception was not detected (illegal 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{cpf.processPipeStep(temporary,null,PipeConfigmation.ALWAYS_YES);
			Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}
		try{cpf.processPipeStep(temporary,PureLibSettings.CURRENT_LOGGER,null);
			Assert.fail("Mandatory exception was not detected (null 3-rd argument)");
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

		try{cpf.unpreparePipeItem(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{cpf.unpreparePipeItem("illegal");
			Assert.fail("Mandatory exception was not detected (illegal 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
	}

	@Test
	public void dialogPipeFrameTest() throws ContentException, IOException, FlowException {
		final ContentNodeMetadata	inner = new MutableContentNodeMetadata("inner",ConditionalPipeFrame.class,"./inner",localizerURI,"testSet1","testSet1",null,null,URI.create("app:action:/inner"),null); 
		final ContentNodeMetadata	onTrue = new MutableContentNodeMetadata("onTrue",ConditionalPipeFrame.class,"./ontrue",localizerURI,"testSet1","testSet1",null,null,URI.create("app:action:/ontrue"),null); 
		final ContentNodeMetadata	onFalse = new MutableContentNodeMetadata("onFalse",ConditionalPipeFrame.class,"./onfalse",localizerURI,"testSet1","testSet1",null,null,URI.create("app:action:/onfalse"),null); 
		final DialogPipeFrame		dpf = new DialogPipeFrame(1,mgr,localizer,inner,onTrue,onFalse,general);
		final PluginSpecific		ps = new PluginSpecific(), ps2 = new PluginSpecific();
		
		ps.initialCode = "test := \"123\";";
		ps.terminalCodeOK = "test := test + test;";
		ps.terminalCodeCancel = "test := \"\";";
		ps.fields = new MutableContentNodeMetadata[] {new MutableContentNodeMetadata("test",String.class,"./test",localizerURI,"testSet1",null,null,new FieldFormat(String.class), ModelUtils.buildUriByClassAndField(TestClass.class,"test"), null)};
		
		// Serialization test
		dpf.deserializeFrame(ps);
		dpf.serializeFrame(ps2);
		Assert.assertEquals(ps,ps2);
		
		// Pipe processing test
		final Object	temporary = dpf.preparePipeItem(PureLibSettings.INTERNAL_LOADER);
		
		Assert.assertTrue(temporary instanceof Map);
		
		dpf.storeIncomingValue(temporary,ps.fields[0],"0");
		Assert.assertEquals(PipeStepReturnCode.CONTINUE_TRUE,dpf.processPipeStep(temporary,PureLibSettings.CURRENT_LOGGER,PipeConfigmation.ALWAYS_YES));
		Assert.assertEquals("123123",dpf.getOutgoingValue(temporary,ps.fields[0]));
		
		dpf.storeIncomingValue(temporary,ps.fields[0],"0");
		Assert.assertEquals(PipeStepReturnCode.CONTINUE_FALSE,dpf.processPipeStep(temporary,PureLibSettings.CURRENT_LOGGER,PipeConfigmation.ALWAYS_NO));
		Assert.assertEquals("",dpf.getOutgoingValue(temporary,ps.fields[0]));
		
		dpf.unpreparePipeItem(temporary);

		// Exceptions test
		try{dpf.deserializeFrame(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try{dpf.serializeFrame(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}

		try{dpf.processPipeStep(null,PureLibSettings.CURRENT_LOGGER,PipeConfigmation.ALWAYS_YES);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{dpf.processPipeStep("illegal",PureLibSettings.CURRENT_LOGGER,PipeConfigmation.ALWAYS_YES);
			Assert.fail("Mandatory exception was not detected (illegal 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{dpf.processPipeStep(temporary,null,PipeConfigmation.ALWAYS_YES);
			Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}
		try{dpf.processPipeStep(temporary,PureLibSettings.CURRENT_LOGGER,null);
			Assert.fail("Mandatory exception was not detected (null 3-rd argument)");
		} catch (NullPointerException exc) {
		}
		
		try{dpf.storeIncomingValue(null,ps.fields[0],null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{dpf.storeIncomingValue("illegal",ps.fields[0],null);
			Assert.fail("Mandatory exception was not detected (illegal 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{dpf.storeIncomingValue(temporary,null,null);
			Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}

		try{dpf.unpreparePipeItem(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{dpf.unpreparePipeItem("illegal");
			Assert.fail("Mandatory exception was not detected (illegal 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
	}

	@Test
	public void containerPipeFrameTest() throws ContentException, IOException, FlowException {
		final PP						inst = new PP(PureLibSettings.CURRENT_LOGGER);
		final ContainerPipeFrame<PP>	dpf = new ContainerPipeFrame<>(1,mgr, localizer, (FormManager<?,PP>)inst, general);
		final PluginSpecific			ps = new PluginSpecific(), ps2 = new PluginSpecific();

		ps.pluginClass = PP.class.getCanonicalName();
		ps.initialCode = "test := \"123\";";
		ps.action = "app:action:/PP.calculate";
		
		// Serialization test
		dpf.deserializeFrame(ps);
		dpf.serializeFrame(ps2);
		Assert.assertEquals(ps,ps2);
		
		// Pipe processing test
		ps.fields = new MutableContentNodeMetadata[] {new MutableContentNodeMetadata("test",String.class,"./test",localizerURI,"testSet1",null,null,new FieldFormat(String.class), ModelUtils.buildUriByClassAndField(TestClass.class,"test"), null)};
		
		final Object	temporary = dpf.preparePipeItem(PureLibSettings.INTERNAL_LOADER);
		
		Assert.assertTrue(temporary instanceof Map);
		
		dpf.storeIncomingValue(temporary,ps.fields[0],"0");
		Assert.assertEquals(PipeStepReturnCode.CONTINUE,dpf.processPipeStep(temporary,PureLibSettings.CURRENT_LOGGER,PipeConfigmation.ALWAYS_YES));
		Assert.assertEquals("123123",dpf.getOutgoingValue(temporary,ps.fields[0]));
		
		dpf.unpreparePipeItem(temporary);

		// Exceptions test
		try{dpf.deserializeFrame(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}
		try{dpf.serializeFrame(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (NullPointerException exc) {
		}

		try{dpf.processPipeStep(null,PureLibSettings.CURRENT_LOGGER,PipeConfigmation.ALWAYS_YES);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{dpf.processPipeStep("illegal",PureLibSettings.CURRENT_LOGGER,PipeConfigmation.ALWAYS_YES);
			Assert.fail("Mandatory exception was not detected (illegal 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{dpf.processPipeStep(temporary,null,PipeConfigmation.ALWAYS_YES);
			Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}
		try{dpf.processPipeStep(temporary,PureLibSettings.CURRENT_LOGGER,null);
			Assert.fail("Mandatory exception was not detected (null 3-rd argument)");
		} catch (NullPointerException exc) {
		}
		
		try{dpf.storeIncomingValue(null,ps.fields[0],null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{dpf.storeIncomingValue("illegal",ps.fields[0],null);
			Assert.fail("Mandatory exception was not detected (illegal 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{dpf.storeIncomingValue(temporary,null,null);
			Assert.fail("Mandatory exception was not detected (null 2-nd argument)");
		} catch (NullPointerException exc) {
		}

		try{dpf.unpreparePipeItem(null);
			Assert.fail("Mandatory exception was not detected (null 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
		try{dpf.unpreparePipeItem("illegal");
			Assert.fail("Mandatory exception was not detected (illegal 1-st argument)");
		} catch (IllegalArgumentException exc) {
		}
	}
}