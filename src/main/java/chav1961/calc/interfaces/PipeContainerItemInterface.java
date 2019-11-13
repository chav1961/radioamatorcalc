package chav1961.calc.interfaces;

import java.net.URI;

public interface PipeContainerItemInterface {
	public enum ContainerItemType {
		FIELD, CONTROL
	}
	ContainerItemType getType();
	boolean canUseAsSource();
	boolean canUseAsTarget();
	int getIncomingLinkCount();
	int getOutgoingLinkCount();
	Iterable<PipeContainerItemInterface> getIncomingLinks();
	Iterable<PipeContainerItemInterface> getOutgoingLinks();
	PipeContainerItemInterface addIncomingLink(PipeContainerItemInterface another);
	PipeContainerItemInterface addOutgoingLink(PipeContainerItemInterface another);
	PipeContainerItemInterface removeIncomingLink(PipeContainerItemInterface another);
	PipeContainerItemInterface removeOutgoingLink(PipeContainerItemInterface another);
}
