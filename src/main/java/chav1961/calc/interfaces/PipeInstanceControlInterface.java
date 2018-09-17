package chav1961.calc.interfaces;

import chav1961.purelib.ui.AbstractLowLevelFormFactory.FieldDescriptor;

public interface PipeInstanceControlInterface {
	Object getValue(FieldDescriptor desc);
	void setValue(FieldDescriptor desc, Object value);
	boolean execute(String action);
}
