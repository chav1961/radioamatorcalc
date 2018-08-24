package chav1961.calc.formulas;

import chav1961.calc.LocalizationKeys;
import chav1961.calc.interfaces.UseAsFormulaTag;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.ui.interfacers.FormManager.RefreshMode;

public class Utils {
	/**
	 * <p>Calculate inductance of the one layer coil.</p>
	 * @param diameter diameter (sm)
	 * @param length length (sm)
	 * @param wireDiameter diameter (mm)
	 * @param numberOfCoils
	 * @return inductance (microH)
	 */
	@UseAsFormulaTag(LocalizationKeys.FORMULA_INDUCTANCE_ONE_LAYER_COIL)
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
	@UseAsFormulaTag(LocalizationKeys.FORMULA_NUMBER_OF_COILS_ONE_LAYER_COIL)
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

	/**
	 * <p>Calculate inductance of the ring coil by it's coils</p>
	 * @param coils coils of the ring coil
	 * @param outerDiameter outer diameter of the ring
	 * @param innerDiameter innr diameter of the ring
	 * @param height height of the ring
	 * @param permability magnetic permability of the ring
	 * @return inductance of the ring
	 */
	@UseAsFormulaTag(LocalizationKeys.FORMULA_INDUCTANCE_RING_COIL)
	public static double inductanceRingCoil(final int coils, final float outerDiameter, final float innerDiameter, final float height, final int permability) {
		if (outerDiameter/innerDiameter > 1.75) {
			return 0.0002f * permability * height * coils * coils * Math.log(outerDiameter/innerDiameter);
		}
		else {
			return 0.0004f * permability * height * coils * coils * (outerDiameter - innerDiameter) / (outerDiameter + innerDiameter);
		}
	}

	/**
	 * <p>Calculate coils of the ring coil by it's inductance</p>
	 * @param inductance inductance of the ring coil 
	 * @param outerDiameter outer diameter of the ring
	 * @param innerDiameter inner diameter of the ring
	 * @param height height of the ring
	 * @param permability magnetic permability of the ring
	 * @return number of coils for the ring
	 */
	@UseAsFormulaTag(LocalizationKeys.FORMULA_COILS_RING_COIL)
	public static int coilsRingCoil(final float inductance, final float outerDiameter, final float innerDiameter, final float height, final int permability) {
		if (outerDiameter/innerDiameter > 1.75) {
			return (int) Math.sqrt(inductance / (0.0002f * permability * height * Math.log(outerDiameter/innerDiameter)));  
		}
		else {
			return (int) Math.sqrt(inductance / (0.0004f * permability * height * (outerDiameter - innerDiameter) / (outerDiameter + innerDiameter)));
		}
	}

	/**
	 * <p>Calculate magnetic field density inside the ring coil</p>
	 * @param current current of the ring coil
	 * @param coils number if coils of the ring coil
	 * @param outerDiameter outer diameter of the ring
	 * @param innerDiameter inner diameter of the ring
	 * @param permability magnetic permability of the ring
	 * @return magnetic field induction value inside the ring
	 */
	@UseAsFormulaTag(LocalizationKeys.FORMULA_INDUCTION_RING_COIL)
	public static float inductionRingCoil(final float current, final int coils, final float outerDiameter, final float innerDiameter, final int permability) {
		return (float) (1.257e-3*permability*current*coils/(Math.PI*(outerDiameter+innerDiameter)/2));
	}
	
	/**
	 * <p>Calculate wire length, new inner and new outer diameter for the ring coil.</p>
	 * @param coils number of coils on the ring 
	 * @param wireDiameter wire diameter
	 * @param outerDiameter initial outer diameter of the ring
	 * @param innerDiameter initial inner diameter of the ring
	 * @param height initial height of the ring
	 * @return array of wire length, new outer diameter of the ring, new inner diameter of the ring and new height of the ring. If the given number of coils can't
	 * be placed on the given ring, returns null instead of array 
	 */
	public static float[] wireLength4Ring(final int coils, final float wireDiameter, final float outerDiameter, final float innerDiameter, final float height) {
		int 	restOfCoils = coils;
		float 	currentInnerDiameter = innerDiameter - wireDiameter, currentOuterDiameter, currentHeight;
		
		while (restOfCoils > 0 && currentInnerDiameter > 0) {
			restOfCoils -= Math.PI * currentInnerDiameter / wireDiameter;
			currentInnerDiameter -= wireDiameter;
		}
		
		if (currentInnerDiameter < 0) {
			return null;
		}
		else {
			float currentWireLength = 0.0f, currentCoilLength = outerDiameter - innerDiameter + 2 * height + 4 * wireDiameter;
			
			restOfCoils = coils;
			currentInnerDiameter = innerDiameter - wireDiameter;
			currentOuterDiameter = outerDiameter + wireDiameter;
			currentHeight = height + 2 * wireDiameter; 

			while (restOfCoils > 0 && currentInnerDiameter > 0) {
				int coilsInLayer = (int) (Math.PI * currentInnerDiameter / wireDiameter);
				
				currentWireLength += currentCoilLength * Math.min(coilsInLayer,restOfCoils);
				currentInnerDiameter -= wireDiameter;
				currentOuterDiameter += wireDiameter;
				currentHeight += 2 * wireDiameter;
				restOfCoils -= coilsInLayer;
				currentCoilLength += 4 * wireDiameter;
			}
			return new float[]{currentWireLength, currentInnerDiameter, currentOuterDiameter, currentHeight};
		}
	}
}
