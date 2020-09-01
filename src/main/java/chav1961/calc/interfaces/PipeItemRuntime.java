package chav1961.calc.interfaces;

import chav1961.purelib.basic.SimpleURLClassLoader;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public interface PipeItemRuntime {
	public enum PipeStepReturnCode {
		CONTINUE, CONTINUE_TRUE, CONTINUE_FALSE, TERMINATE_TRUE, TERMINATE_FALSE
	}

	public enum PipeConfigmation {
		ALWAYS_YES, ALWAYS_NO, ASK
	}
	
	Object preparePipeItem(final SimpleURLClassLoader loader) throws FlowException;
	<T> void storeIncomingValue(Object temp, ContentNodeMetadata meta, T value) throws ContentException;
	PipeStepReturnCode processPipeStep(Object temp, LoggerFacade logger, PipeConfigmation confirmation) throws FlowException;
	<T> T getOutgoingValue(Object temp, ContentNodeMetadata meta) throws ContentException;
	void unpreparePipeItem(Object temp) throws FlowException;
}
