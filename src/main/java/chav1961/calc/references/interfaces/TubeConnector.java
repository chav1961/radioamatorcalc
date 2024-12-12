package chav1961.calc.references.interfaces;

public interface TubeConnector {
	public static enum TubeConnectorType {
		AN
	}
	
	public static enum PinType {
		ORDINAL,
		TOP
	}
	
	int getLampNo();
	int getPin();
	PinType getPinType();
	TubeConnectorType getType();
}
