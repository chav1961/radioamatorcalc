package chav1961.calc.interfaces;

@FunctionalInterface
public interface ContentClassificator {
	public enum ContentType {
		FREE, CONTAINER, CONTAINER_ICON, VALUE, CONTROL, SOURCE_OR_TARGET, LINK
	}
	ContentType classify(int x, int y);
}