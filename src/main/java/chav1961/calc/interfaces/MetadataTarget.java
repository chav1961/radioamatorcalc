package chav1961.calc.interfaces;

import chav1961.calc.utils.PipeLink;

@FunctionalInterface
public interface MetadataTarget {
	void drop(final PipeLink link, final int xFrom, final int yFrom, final int xTo, final int yTo);
}
