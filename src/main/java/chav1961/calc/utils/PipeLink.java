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
	private final ContentNodeMetadata		associatedMeta;
	
	public PipeLink(final PipeLink.PipeLinkType type, final PipeContainerInterface source, final Component sourcePoint, final PipeContainerInterface target,final Component targetPoint, final ContentNodeMetadata metadata, final ContentNodeMetadata associatedMeta) {
		this.type = type;
		this.source = source;
		this.target = target;
		this.sourcePoint = sourcePoint;
		this.targetPoint = targetPoint;
		this.metadata = metadata;
		this.associatedMeta = associatedMeta;
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

	public ContentNodeMetadata getAssociatedMeta() {
		return associatedMeta;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((associatedMeta == null) ? 0 : associatedMeta.hashCode());
		result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((sourcePoint == null) ? 0 : sourcePoint.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + ((targetPoint == null) ? 0 : targetPoint.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		PipeLink other = (PipeLink) obj;
		if (associatedMeta == null) {
			if (other.associatedMeta != null) return false;
		} else if (!associatedMeta.equals(other.associatedMeta)) return false;
		if (metadata == null) {
			if (other.metadata != null) return false;
		} else if (!metadata.equals(other.metadata)) return false;
		if (source == null) {
			if (other.source != null) return false;
		} else if (!source.equals(other.source)) return false;
		if (sourcePoint == null) {
			if (other.sourcePoint != null) return false;
		} else if (!sourcePoint.equals(other.sourcePoint)) return false;
		if (target == null) {
			if (other.target != null) return false;
		} else if (!target.equals(other.target)) return false;
		if (targetPoint == null) {
			if (other.targetPoint != null) return false;
		} else if (!targetPoint.equals(other.targetPoint)) return false;
		if (type != other.type) return false;
		return true;
	}

	@Override
	public String toString() {
		return "PipeLink [type=" + type + ", source=" + source + ", target=" + target + ", sourcePoint=" + sourcePoint + ", targetPoint=" + targetPoint + ", metadata=" + metadata + ", associatedMeta=" + associatedMeta + "]";
	}
}