package chav1961.calc.interfaces;

import java.net.URI;

public interface PipeContainerValueInterface {
	URI getItemAssociated();
	boolean isInputValue();
	boolean isOutputValue();
	boolean isReadOnly();
	String getInitialValue();
	<T> T getCurrentValue();
	<T> void setCurrentValue(T newValue);
	void fireChange();
}
