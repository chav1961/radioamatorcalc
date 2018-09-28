package chav1961.calc.interfaces;

import chav1961.calc.interfaces.PluginInterface.PluginInstance;
import chav1961.purelib.ui.AbstractLowLevelFormFactory.FieldDescriptor;

public interface PipeControlInterface {
	boolean hasField(String fieldName);
	FieldDescriptor getFieldDescriptor(String fieldName);
	FieldDescriptor[] getInnerControls();
	FieldDescriptor[] getOuterControls();
	String[] getActions();
}
