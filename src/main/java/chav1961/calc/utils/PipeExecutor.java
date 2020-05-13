package chav1961.calc.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import chav1961.calc.interfaces.PipeContainerInterface;
import chav1961.calc.interfaces.PipeContainerInterface.PipeItemType;
import chav1961.calc.interfaces.PipeItemRuntime;
import chav1961.calc.interfaces.PipeItemRuntime.PipeConfigmation;
import chav1961.calc.interfaces.PipeItemRuntime.PipeStepReturnCode;
import chav1961.calc.pipe.JControlFalse;
import chav1961.calc.pipe.JControlSource;
import chav1961.calc.pipe.JControlTarget;
import chav1961.calc.pipe.JControlTrue;
import chav1961.purelib.basic.PureLibSettings;
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
		final Object[]					temporaries = new Object[content.length];
		final Set<PipePluginFrame<?>>	completedAsTrue = new HashSet<>();
		final Set<PipePluginFrame<?>>	completedAsFalse = new HashSet<>();
		final Set<Integer>				processed = new HashSet<>();
		PipeStepReturnCode				rc;
		
		for (int index = 0, maxIndex = temporaries.length; index < maxIndex; index++) {
			temporaries[index] = content[index].preparePipeItem();
		}
		try{
			while(!Thread.interrupted()) {
				for (int itemIndex : getReadyToProcess(completedAsTrue,completedAsFalse,processed)) {
					final PipePluginFrame<?>	item = content[itemIndex];
					
					for (PipeLink value : item.getIncomingControls()) {
						if (value.getSource() != null) {
							for (int index = 0, maxIndex = content.length; index < maxIndex; index++) {
								if (content[index] == value.getSource()) {
									final Object	toStore = ((PipeItemRuntime)value.getSource()).getOutgoingValue(temporaries[index],value.getAssociatedMeta() != null ? value.getAssociatedMeta() : value.getMetadata()); 
									
									item.storeIncomingValue(temporaries[itemIndex],value.getMetadata(),toStore);
									break;
								}
							}
						}
					}
					switch (rc = item.processPipeStep(temporaries[itemIndex],logger,PipeConfigmation.ASK)) {
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
					processed.add(itemIndex);
				}
			}
		} finally {
			for (int index = 0, maxIndex = temporaries.length; index < maxIndex; index++) {
				 content[index].unpreparePipeItem(temporaries[index]);
			}
		}
	}

	Integer[] getReadyToProcess(final Set<PipePluginFrame<?>> completedAsTrue, final Set<PipePluginFrame<?>> completedAsFalse, final Set<Integer> processed) throws FlowException {
		final List<Integer>	result = new ArrayList<>();
		
		if (completedAsTrue.isEmpty() && completedAsFalse.isEmpty()) {
			for (int index = 0, maxIndex = content.length; index < maxIndex; index++) {
				if (content[index].getType() == PipeItemType.INITIAL_ITEM) {
					result.add(index);
					break;
				}
			}
		}
		else {
loop:		for (int index = 0, maxIndex = content.length; index < maxIndex; index++) {
				if (content[index].getType() != PipeItemType.INITIAL_ITEM) {
					final PipeLink[]	links = content[index].getLinks();
					
					for (PipeLink link : links) {
						if (!completedAsTrue.contains(link.getSource()) && !completedAsFalse.contains(link.getSource())) {
							continue loop;
						}
					}
					
					for (PipeLink link : links) {
						if (completedAsTrue.contains(link.getSource()) && ((link.getSourcePoint() instanceof JControlSource) || (link.getSourcePoint() instanceof JControlTrue))   
							|| completedAsFalse.contains(link.getSource()) && ((link.getSourcePoint() instanceof JControlSource) || (link.getSourcePoint() instanceof JControlFalse))) {
							result.add(index);
						}
					}
				}
			}
		}
		if (result.isEmpty()) {
			throw new FlowException("Pipe error: unresolved completion state in the pipe");
		}
		else {
			result.removeAll(processed);
			return result.toArray(new Integer[result.size()]);
		}
	}
}
