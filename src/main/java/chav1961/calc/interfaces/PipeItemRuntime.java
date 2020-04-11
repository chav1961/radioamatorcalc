package chav1961.calc.interfaces;

import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public interface PipeItemRuntime {
	public enum PipeStepReturnCode {
		CONTINUE, CONTINUE_TRUE, CONTINUE_FALSE, TERMINATE_TRUE, TERMINATE_FALSE
	}
	
	<T> void storeIncomingValue(final ContentNodeMetadata meta, final T value) throws ContentException;
	<T> T getOutgoingValue(final ContentNodeMetadata meta) throws ContentException;
	PipeStepReturnCode processPipeStep() throws FlowException;
}
