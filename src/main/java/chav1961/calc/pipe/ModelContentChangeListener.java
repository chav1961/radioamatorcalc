package chav1961.calc.pipe;

@FunctionalInterface
public interface ModelContentChangeListener {
	public enum ChangeType {
		INSERTED, REMOVED
	}
	
	void contentChangePerormed(final ModelContentChangeListener.ChangeType changeType, final Object source, final Object current);
}