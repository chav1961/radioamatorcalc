package chav1961.calc.references.interfaces;

public enum TubeParameter {
	MYU(100);
	
	private final int	orderIndex;
	
	private TubeParameter(final int orderIndex) {
		this.orderIndex = orderIndex;
	}
	
	public int getOrderIndex() {
		return orderIndex;
	}
}