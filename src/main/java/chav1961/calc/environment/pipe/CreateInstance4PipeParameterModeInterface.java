package chav1961.calc.environment.pipe;

import chav1961.calc.environment.PipeParameterWrapper;
import chav1961.purelib.basic.exceptions.ContentException;

@FunctionalInterface
public interface CreateInstance4PipeParameterModeInterface {
	PipeParameterWrapper newInstance(int uniqueId) throws ContentException;
}