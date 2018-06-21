package chav1961.calc.formulas;

public class Coils {
	/**
	 * <p>Calculate inductance of the one layer coil.</p>
	 * @param diameter diameter (sm)
	 * @param length length (sm)
	 * @param wireDiameter diameter (mm)
	 * @param numberOfCoils
	 * @return inductance (microH)
	 */
	public static double inductanceOneLayerCoil(final double diameter, final double length, final double wireDiameter, final double numberOfCoils) {
		return length > diameter / 2 
				? diameter * diameter * numberOfCoils * numberOfCoils / (45 * diameter + 100 * length) 
				: diameter * diameter * numberOfCoils * numberOfCoils / (10 * (4 * diameter + 11 * length));
	}

	/**
	 * <p>Calculate number of coils of the one layer coil</p>
	 * @param diameter diameter (sm)
	 * @param length length (sm)
	 * @param wireDiameter diameter (mm)
	 * @param inductance (microH)
	 * @return number of coils
	 */
	public static double numberOfCoilsOneLayerCoil(final double diameter, final double length, final double wireDiameter, final double inductance) {
		return length > diameter / 2
				? Math.sqrt(5 * inductance * (9 * diameter + 20 * length))/diameter 
				: Math.sqrt(10 * inductance * (4 * diameter + 11 * length))/diameter;
	}

	public static double inductanceMultiLayerCoil(final double minDiameter, final double maxDiameter, final double length, final double wireDiameter, final double numberOfCoils) {
		return 0;
	}

	public static double numberOfCoilsMultiLayerCoil(final double minDiameter, final double maxDiameter, final double length, final double wireDiameter, final double inductance) {
		return 0;
	}

	public static double inductanceRoundedFlatCoil(final double minDiameter, final double maxDiameter, final double wireWidth, final double numberOfCoils) {
		return 0;
	}

	public static double numberOfCoilsRoundedFlatCoil(final double minDiameter, final double maxDiameter, final double wireWidth, final double inductance) {
		return 0;
	}

	public static double inductanceSquaredFlatCoil(final double minWidth, final double maxWidth, final double wireWidth, final double numberOfCoils) {
		return 0;
	}

	public static double numberOfCoilsSquaredFlatCoil(final double minWidth, final double maxWidth, final double wireWidth, final double inductance) {
		return 0;
	}
}
