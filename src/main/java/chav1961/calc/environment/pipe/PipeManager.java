package chav1961.calc.environment.pipe;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import chav1961.calc.environment.Constants;
import chav1961.calc.environment.PipeParameterWrapper;
import chav1961.calc.interfaces.PipeInterface;
import chav1961.calc.interfaces.PluginInterface;
import chav1961.calc.interfaces.PluginInterface.PluginInstance;
import chav1961.purelib.basic.NullLoggerFacade;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.PrintingException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.json.JsonSerializer;
import chav1961.purelib.streams.JsonStaxParser;
import chav1961.purelib.streams.JsonStaxPrinter;

class PipeManager implements PipeInterface, LocaleChangeListener {
	private final List<PluginInstance>				registeredInstances = new ArrayList<>();
	
	private final Localizer							localizer;	
	private final LoggerFacade						logger;	
	private final JsonSerializer<SerializedPipe>	serializer;	
	private volatile boolean						wasModified = false;
	
	PipeManager(final Localizer localizer, final LoggerFacade logger, final PipeFactory factory) throws EnvironmentException {
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else {
			this.localizer = localizer;
			this.logger = logger;
			this.serializer = JsonSerializer.buildSerializer(SerializedPipe.class);
		}
	}

	@Override
	public boolean isModified() {
		return wasModified;
	}

	@Override
	public void setModified(final boolean modificationFlag) {
		this.wasModified = modificationFlag;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ContentDescription getContentDescription(final PluginInstance inst) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<PluginInterface> getPluginsRequired() {
		final Set<PluginInterface>	result = new HashSet<>();
		
		for (PluginInstance item : getPluginInstanceUsed()) {
			result.add(item.getPluginDescriptor());
		}
		return result;
	}

	@Override
	public Iterable<PluginInstance> getPluginInstanceUsed() {
		return Collections.unmodifiableCollection(registeredInstances);
	}
	
	@Override
	public void serialize(final Writer writer) throws IOException {
		if (writer == null) {
			throw new NullPointerException("Writer to serialize to can't be null");
		}
		else if (!validatePipe(logger)) {
			throw new IllegalStateException("Attempt to serialize invalid pipe");
		}
		else {
			try(final JsonStaxPrinter	printer = new JsonStaxPrinter(writer)) {

				serializer.serialize(buildSerialized(),printer);
				printer.flush();
				setModified(false);
			} catch (PrintingException e) {
				throw new IOException(e);
			}
		}
	}

	@Override
	public void deserialize(final Reader reader) throws IOException {
		if (reader == null) {
			throw new NullPointerException("Reader to deserialize from can't be null");
		}
		else {
			try(final JsonStaxParser	parser = new JsonStaxParser(reader)) {
				
				fillFromSerialized(serializer.deserialize(parser));
			} catch (ContentException | SyntaxException | LocalizationException e) {
				throw new IOException(e);
			}
		}
	}

	@Override
	public boolean isReadyToPlay() {
		return validatePipe(new NullLoggerFacade());
	}

	@Override
	public boolean validatePipe(final LoggerFacade validationLog) {
		if (validationLog == null) {
			throw new NullPointerException("Validation log can't be null");
		}
		else {
			boolean	hasStartNode = false, hasTerminalNode = false, success = true;
			
			for (PluginInterface item : getPluginsRequired()) {
				if (Constants.PIPE_START_NODE.equals(item.getPluginId())) {
					hasStartNode = true;
				}
				else if (Constants.PIPE_TERMINAL_NODE.equals(item.getPluginId())) {
					hasTerminalNode = true;
				}
			}
			if (!hasStartNode) {
				printMessage(validationLog,"");
				success = false;
			}
			if (!hasTerminalNode) {
				printMessage(validationLog,"");
				success = false;
			}
			return success;
		}
	}
	
	@Override
	public void play() throws IOException, FlowException {
		if (!isReadyToPlay()) {
			throw new IllegalStateException("Pipe is not ready yet to play (isReadyToPlay() == false)");
		}
		else {
			for (PluginInstance item : getPluginInstanceUsed()) {
				if (Constants.PIPE_START_NODE.equals(item.getPluginDescriptor().getPluginId())) {
					play(item);
				}
			}
		}
	}

	@Override
	public void registerPluginInstance(final PluginInstance instance) {
		if (instance == null) {
			throw new NullPointerException("Instance to register can't be null");
		}
		else {
			synchronized (registeredInstances) {
				registeredInstances.add(instance);
				setModified(true);
			}
		}
	}

	@Override
	public void unregisterPluginInstance(final PluginInstance instance) {
		if (instance == null) {
			throw new NullPointerException("Instance to unregister can't be null");
		}
		else {
			synchronized (registeredInstances) {
				registeredInstances.remove(instance);
				setModified(true);
			}
		}
	}

	private void printMessage(final LoggerFacade logger, final String formatId, final Object... parameters) {
		try{logger.message(Severity.warning,localizer.getValue(formatId),parameters);
		} catch (LocalizationException | IllegalArgumentException e) {
			logger.message(Severity.error,"No error message found [%1$s]: parameters are %2$s",formatId,Arrays.toString(parameters));
		}
	}

	private SerializedPipe buildSerialized() {
		final Set<String>					pluginIds = new HashSet<>();
		final Set<String>					terminalNames = new HashSet<>();
		final Set<String>					inputs = new HashSet<>();
		final Set<String>					controlSources = new HashSet<>();
		final List<PluginDataSource>		dataSources = new ArrayList<>();
		final List<PluginDefault>			defaults = new ArrayList<>();
		final List<SerializedPipePlugin>	pluginList = new ArrayList<>();
		String								startName = null;
		
		for (PluginInterface item : getPluginsRequired()) {
			pluginIds.add(item.getPluginId());
		}
		
		for (PluginInstance item : getPluginInstanceUsed()) {
			final ContentDescription	desc = getContentDescription(item);
					
			dataSources.clear();	defaults.clear();
			inputs.clear();			controlSources.clear();
			
			if (Constants.PIPE_START_NODE.equals(item.getPluginDescriptor().getPluginId())) {
				startName = desc.getNameInPipe(); 
			}
			if (Constants.PIPE_TERMINAL_NODE.equals(item.getPluginDescriptor().getPluginId())) {
				terminalNames.add(desc.getNameInPipe());
			}
			for (DataLinkDescription link : desc.getIncomingDataLinks()) {
				final PluginDataSource	entity = new PluginDataSource();
				
				entity.sourcePluginInstanceName = link.getSource().getPluginInstanceName();
				entity.sourcePluginFieldName = link.getSource().getPluginFieldName();
				entity.currentPluginFieldName = link.getTarget().getPluginFieldName();
				dataSources.add(entity);
			}
			for (ControlLinkDescription link : desc.getIncomingControlLinks()) {
				controlSources.add(getContentDescription(link.getSource()).getNameInPipe());
			}
			
			final SerializedPipePlugin	entity = new SerializedPipePlugin();
			
			entity.pluginId = item.getPluginDescriptor().getPluginId();
			entity.pluginInstanceName = desc.getNameInPipe();
			entity.dataSources = dataSources.toArray(new PluginDataSource[dataSources.size()]);
			entity.defaults = defaults.toArray(new PluginDefault[defaults.size()]);
			entity.inputs = inputs.toArray(new String[inputs.size()]);
			entity.controlSourceNames = controlSources.toArray(new String[controlSources.size()]);
			pluginList.add(entity);
		}

		final SerializedPipe	result = new SerializedPipe();
		
		result.startPluginInstanceName = startName;
		result.terminalPluginInstanceNames = terminalNames.toArray(new String[terminalNames.size()]);
		result.pluginIds = pluginIds.toArray(new String[pluginIds.size()]);
		result.pluginDescriptors = pluginList.toArray(new SerializedPipePlugin[pluginList.size()]);		
		
		return result;
	}

	private void fillFromSerialized(final SerializedPipe deserialize) throws ContentException, LocalizationException, SyntaxException, IOException {
		final PluginInstance[]				components = new PluginInstance[deserialize.pluginDescriptors.length];
		final Map<String,PluginInterface>	plugins = new HashMap<>();
		
next:	for (String pluginId : deserialize.pluginIds) {	// Check plugin list
			for (PluginInterface item : ServiceLoader.load(PluginInterface.class)) {
				if (pluginId.equals(item.getPluginId())) {
					plugins.put(pluginId,item);
					continue next;
				}
			}
			throw new ContentException("Plugin id ["+pluginId+"] not found in the current plugin list. Deserialization failed");
		}
	
		synchronized (registeredInstances) {	// Clear plugin list
			registeredInstances.clear();
		}
		
		for (int index = 0; index < components.length; index++) {	// Create plugin instances and associate names with them
			final PluginInstance	inst = plugins.get(deserialize.pluginDescriptors[index].pluginId).newInstance(localizer,logger); 
			
			registerPluginInstance(inst);
			components[index] = inst;
			
			final MutableContentDescription	desc = (MutableContentDescription)getContentDescription(inst);

			desc.setNameInPipe(deserialize.pluginDescriptors[index].pluginInstanceName);
		}

		for (int index = 0; index < components.length; index++) {	// Build control and data links
			final MutableContentDescription	desc = (MutableContentDescription)getContentDescription(components[index]);

next:		for (String link : deserialize.pluginDescriptors[index].controlSourceNames) {
				for (PluginInstance item : getPluginInstanceUsed()) {
					final MutableContentDescription	candidate = (MutableContentDescription)getContentDescription(item);
					
					if (candidate.getNameInPipe().equals(link)) {
						desc.addControlLink(candidate);
						continue next;
					}
				}
				throw new ContentException("Plugin instance ["+desc.getNameInPipe()+"] has invalid non-existent link ["+link+"]");
			}
		}
	}

	private void play(final PluginInstance inst) {
		// TODO Auto-generated method stub
		final ContentDescription		desc = getContentDescription(inst);
		
		for (DataLinkDescription link : desc.getIncomingDataLinks()) {
			final ContentDescription	srcDesc = getDescByName(link.getSource().getPluginInstanceName());
			
			assignValue(srcDesc.getPluginInstance(),link.getSource().getPluginFieldName(),link.getSource().getPluginFieldType(),inst,link.getTarget().getPluginFieldName(),link.getTarget().getPluginFieldType());
		}
		
	}

	private ContentDescription getDescByName(final String nameInPipe) {
		for (PluginInstance item : getPluginInstanceUsed()) {
			final ContentDescription	desc = getContentDescription(item);
			
			if (nameInPipe.equals(desc.getNameInPipe())) {
				return desc;
			}
		}
		throw new IllegalArgumentException("Plugin instance name ["+nameInPipe+"] not found");
	}
	
	private void assignValue(final PluginInstance source, final String sourceFieldName, final Class<?> sourceFieldType, final PluginInstance target, final String targetFieldName, final Class<?> targetFieldType) {
		// TODO Auto-generated method stub
		
	}

	
	public static class SerializedPipe {
		public String[]					pluginIds;
		public String					startPluginInstanceName;
		public String[]					terminalPluginInstanceNames;
		public SerializedPipePlugin[]	pluginDescriptors;
	}
	
	public static class SerializedPipePlugin {
		public String					pluginId;
		public String					pluginInstanceName;
		public PluginDataSource[]		dataSources;
		public PluginDefault[]			defaults;
		public String[]					inputs;
		public String[]					controlSourceNames;
	}
	
	public static class PluginDataSource {
		public String					sourcePluginInstanceName;
		public String					sourcePluginFieldName;
		public String					currentPluginFieldName;
	}
	
	public static class PluginDefault {
		public String					currentPluginFieldName;
		public String					currentPluginFieldValue;
	}

	private static class MutableContentDescription implements ContentDescription {
		private final PluginInstance	inst;
		private final List<MutableContentDescription>	controlLinks = new ArrayList<>();
		private final List<DataLinkDescriptor>			dataLinks = new ArrayList<>();
		private String					name = "";
		
		MutableContentDescription(final PluginInstance inst) {
			this.inst = inst;
		}

		@Override
		public String getNameInPipe() {
			return name;
		}

		@Override
		public Icon getSourceIcon() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Icon getTargetIcon() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public Iterable<ControlLinkDescription> getIncomingControlLinks() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Iterable<DataLinkDescription> getIncomingDataLinks() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public PluginInstance getPluginInstance() {
			return inst;
		}
		
		void setNameInPipe(final String nameInPipe) {
			this.name = nameInPipe;
		}
		
		void addControlLink(final MutableContentDescription source) {
			
		}

		void removeControlLink(final MutableContentDescription source) {
			
		}

		void addDataLink(final MutableContentDescription source, final String sourceField, final String currentField) {
			
		}

		void removeDataLink(final MutableContentDescription source, final String sourceField, final String currentField) {
			
		}
		
		@Override
		public String toString() {
			return "MutableContentDescription [inst=" + inst + ", controlLinks=" + controlLinks + ", dataLinks=" + dataLinks + ", name=" + name + "]";
		}

		private static class DataLinkDescriptor {
			final MutableContentDescription 	source;
			final String 						sourceField;
			final String 						currentField;
			
			public DataLinkDescriptor(MutableContentDescription source, String sourceField, String currentField) {
				this.source = source;
				this.sourceField = sourceField;
				this.currentField = currentField;
			}

			@Override
			public String toString() {
				return "DataLinkDescriptor [source=" + source + ", sourceField=" + sourceField + ", currentField=" + currentField + "]";
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((currentField == null) ? 0 : currentField.hashCode());
				result = prime * result + ((source == null) ? 0 : source.hashCode());
				result = prime * result + ((sourceField == null) ? 0 : sourceField.hashCode());
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj) return true;
				if (obj == null) return false;
				if (getClass() != obj.getClass()) return false;
				DataLinkDescriptor other = (DataLinkDescriptor) obj;
				if (currentField == null) {
					if (other.currentField != null) return false;
				} else if (!currentField.equals(other.currentField)) return false;
				if (source == null) {
					if (other.source != null) return false;
				} else if (!source.equals(other.source)) return false;
				if (sourceField == null) {
					if (other.sourceField != null) return false;
				} else if (!sourceField.equals(other.sourceField)) return false;
				return true;
			}
		}
	}
}
