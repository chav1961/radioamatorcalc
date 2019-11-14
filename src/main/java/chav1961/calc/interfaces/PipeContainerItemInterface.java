package chav1961.calc.interfaces;

import java.awt.Rectangle;
import java.net.URI;

public interface PipeContainerItemInterface {
	public enum ContainerItemType {
		FIELD, CONTROL
	}
	ContainerItemType getType();
	Rectangle getLocation();
	URI getItemURI();
	boolean canUseAsSource(DragMode mode);
	boolean canUseAsTarget(DragMode mode);
	int getIncomingLinkCount();
	int getOutgoingLinkCount();
	Iterable<PipeContainerItemInterface> getIncomingLinks();
	Iterable<PipeContainerItemInterface> getOutgoingLinks();
	PipeContainerItemInterface addIncomingLink(PipeContainerItemInterface another);
	PipeContainerItemInterface addOutgoingLink(PipeContainerItemInterface another);
	PipeContainerItemInterface removeIncomingLink(PipeContainerItemInterface another);
	PipeContainerItemInterface removeOutgoingLink(PipeContainerItemInterface another);
}
