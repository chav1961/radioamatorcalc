package chav1961.calc.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import chav1961.calc.interfaces.PipeContainerInterface;
import chav1961.calc.interfaces.PipeContainerInterface.PipeItemType;
import chav1961.calc.interfaces.PipeItemRuntime;
import chav1961.calc.interfaces.PipeItemRuntime.PipeStepReturnCode;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.interfaces.LoggerFacade;

public class PipeExecutor {
	private final LoggerFacade			logger;
	private final PipePluginFrame<?>[]	content;
	private final boolean				debugMode; 
	
	public PipeExecutor(final LoggerFacade logger, final PipePluginFrame<?>[] content, final boolean debugMode) {
		if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else if (content == null || content.length == 0) {
			throw new IllegalArgumentException("Plugin frame content can't be null or empty array");
		}
		else {
			this.logger = logger;
			this.content = content;
			this.debugMode = debugMode;
		}
	}
	
	public void run() throws Exception {
		final Set<PipePluginFrame<?>>	completedAsTrue = new HashSet<>();
		final Set<PipePluginFrame<?>>	completedAsFalse = new HashSet<>();
		PipeStepReturnCode				rc;
		
		while(!Thread.interrupted()) {
			for (PipePluginFrame<?> item : getReadyToProcess(completedAsTrue,completedAsFalse)) {
				for (PipeLink value : item.getIncomingControls()) {
					if (value.getSource() != null) {
						item.storeIncomingValue(value.getMetadata(),((PipeItemRuntime)value.getSource()).getOutgoingValue(value.getAssociatedMeta() != null ? value.getAssociatedMeta() : value.getMetadata()));
					}
				}
				switch (rc = item.processPipeStep()) {
					case CONTINUE			:
						completedAsTrue.add(item);
						completedAsFalse.add(item);
						break;
					case CONTINUE_FALSE		:
						completedAsFalse.add(item);
						break;
					case CONTINUE_TRUE		:
						completedAsTrue.add(item);
						break;
					case TERMINATE_FALSE	:
						throw new FlowException("Pipe was terminated abnormally");
					case TERMINATE_TRUE		:
						return;
					default :
						throw new UnsupportedOperationException("Pipe step ret.code ["+rc+"] is not supported yet");
				}
			}
		}
	}

	Iterable<PipePluginFrame<?>> getReadyToProcess(final Set<PipePluginFrame<?>> completedAsTrue, final Set<PipePluginFrame<?>> completedAsFalse) throws FlowException {
		final List<PipePluginFrame<?>>	result = new ArrayList<>();
		
		if (completedAsTrue.isEmpty() && completedAsFalse.isEmpty()) {
			for (PipePluginFrame<?> item : content) {
				if (item.getType() == PipeItemType.INITIAL_ITEM) {
					result.add(item);
					break;
				}
			}
		}
		else {
			for (PipePluginFrame<?> item : content) {
				final PipeLink[]	links = item.getLinks();
				int 				count = 0;
				
				for (PipeLink link : links) {
					if (completedAsTrue.contains(link.getSource()) || completedAsFalse.contains(link.getSource())) {
						count++;
					}
				}
				if (count == links.length) {
					result.add(item);
				}
			}
		}
		if (result.isEmpty()) {
			throw new FlowException("Pipe error: unresolved completion state in the pipe");
		}
		else {
			return result;
		}
	}
}
