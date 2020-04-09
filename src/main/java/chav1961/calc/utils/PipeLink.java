package chav1961.calc.utils;

import java.awt.Component;

import chav1961.calc.interfaces.PipeContainerInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public class PipeLink {
	public enum PipeLinkType {
		DATA_LINK, CONTROL_LINK
	}
	
	private final PipeLinkType				type;
	private final PipeContainerInterface 	source, target;
	private final Component					sourcePoint, targetPoint;
	private final ContentNodeMetadata		metadata;
	
	public PipeLink(final PipeLink.PipeLinkType type, final PipeContainerInterface source, final Component sourcePoint, final PipeContainerInterface target,final Component targetPoint, final ContentNodeMetadata metadata) {
		this.type = type;
		this.source = source;
		this.target = target;
		this.sourcePoint = sourcePoint;
		this.targetPoint = targetPoint;
		this.metadata = metadata;
	}

	public PipeLinkType getType() {
		return type;
	}

	public PipeContainerInterface getSource() {
		return source;
	}

	public PipeContainerInterface getTarget() {
		return target;
	}

	public Component getSourcePoint() {
		return sourcePoint;
	}

	public Component getTargetPoint() {
		return targetPoint;
	}

	public ContentNodeMetadata getMetadata() {
		return metadata;
	}

	@Override
	public String toString() {
		return "PipeLink [type=" + type + ", source=" + source.getClass().getCanonicalName() + ", target=" + target.getClass().getCanonicalName() + ", sourcePoint=" + sourcePoint.getName() + ", targetPoint=" + targetPoint.getName() + ", metadata=" + metadata + "]";
	}
}