package chav1961.calc.interfaces;

import java.awt.Component;

import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

@FunctionalInterface
public interface MetadataTarget {
	void drop(final ContentNodeMetadata meta, final Component from, final int xFrom, final int yFrom, final int xTo, final int yTo);
}
