package chav1961.calc.interfaces;

import chav1961.purelib.model.interfaces.ContentMetadataInterface;

public interface PipeContainerInterface {
	public enum PipeItemType {
		INITIAL_ITEM, TERMINAL_ITEM, CONDITIONAL_ITEM, DIALOG_ITEM, CALC_ITEM, PLUGIN_ITEM  
	}

	PipeItemType getType();
	int getItemCount();
	Iterable<PipeContainerItemInterface> getItems();
	boolean hasComponentAt(int x, int y);
	PipeContainerItemInterface at(int x, int y);
	ContentMetadataInterface getModel();
}
