package chav1961.calc.interfaces;

import chav1961.calc.pipe.JControlLabel;
import chav1961.calc.pipe.JControlTargetLabel;
import chav1961.calc.utils.PipeLink;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public interface PipeContainerInterface {
	public enum PipeItemType {
		INITIAL_ITEM, TERMINAL_ITEM, CONDITIONAL_ITEM, DIALOG_ITEM, CALC_ITEM, PLUGIN_ITEM  
	}

	String getPipeItemName();
	PipeItemType getType();
	int getItemCount();
	Iterable<PipeContainerItemInterface> getItems();
	boolean hasComponentAt(int x, int y);
	PipeContainerItemInterface at(int x, int y);
	ContentMetadataInterface getModel();
	JControlLabel[] getControlSources();
	JControlTargetLabel getControlTarget();
	PipeLink[] getLinks();
}
