package chav1961.calc.formulas;

import chav1961.calc.LocalizationKeys;
import chav1961.calc.interfaces.UseAsFormulaTag;

/**
 * <p>This class contains most popular formulas to use in the calculations</p> 
 * @author Alexander Chernomyrdin aka chav1961
 * @since 0.0.1
 */ 
public class Utils {
	public static final float	MYU_0 = 1.257e-3f; 
	
	/**
	 * <p>Calculate inductance of the one layer coil.</p>
	 * @param coils
	 * @param diameter diameter (in mm)
	 * @param length length (in mm)
	 * @param wireDiameter diameter (in mm)
	 * @return inductance (in microhenry)
	 * @throws IllegalArgumentException if any parameter is not positive  
	 */
	@UseAsFormulaTag(LocalizationKeys.FORMULA_INDUCTANCE_ONE_LAYER_COIL)
	public static float inductanceOneLayerCoil(final int coils, final float diameter, final float length, final float wireDiameter) {
		if (coils <= 0) {
			throw new IllegalArgumentException("Number of coils ["+coils+"] must be positive"); 
		}
		else if (diameter <= 0) {
			throw new IllegalArgumentException("Diameter ["+diameter+"] must be positive"); 
		}
		else if (length <= 0) {
			throw new IllegalArgumentException("Length ["+length+"] must be positive"); 
		}
		else if (wireDiameter <= 0) {
			throw new IllegalArgumentException("Wire diameter ["+wireDiameter+"] must be positive"); 
		}
		else {
			return length > diameter / 2 
					? diameter * diameter * coils * coils / (45 * diameter + 100 * length) 
					: diameter * diameter * coils * coils / (10 * (4 * diameter + 11 * length));
		}
	}

	/**
	 * <p>Calculate number of coils of the one layer coil</p>
	 * @param inductance (microhenry)
	 * @param diameter diameter (in mm)
	 * @param length length (in mm)
	 * @param wireDiameter diameter (in mm)
	 * @return number of coils
	 * @throws IllegalArgumentException if any parameter is not positive  
	 */
	@UseAsFormulaTag(LocalizationKeys.FORMULA_NUMBER_OF_COILS_ONE_LAYER_COIL)
	public static int numberOfCoilsOneLayerCoil(final float inductance, final float diameter, final float length, final float wireDiameter) {
		if (inductance <= 0) {
			throw new IllegalArgumentException("Inductance ["+inductance+"] must be positive"); 
		}
		else if (diameter <= 0) {
			throw new IllegalArgumentException("Diameter ["+diameter+"] must be positive"); 
		}
		else if (length <= 0) {
			throw new IllegalArgumentException("Length ["+length+"] must be positive"); 
		}
		else if (wireDiameter <= 0) {
			throw new IllegalArgumentException("Wire diameter ["+wireDiameter+"] must be positive"); 
		}
		else {
			return (int) (length > diameter / 2
						? Math.sqrt(5 * inductance * (9 * diameter + 20 * length))/diameter 
						: Math.sqrt(10 * inductance * (4 * diameter + 11 * length))/diameter);
		}
	}

//	public static double inductanceMultiLayerCoil(final double minDiameter, final double maxDiameter, final double length, final double wireDiameter, final double numberOfCoils) {
//		return 0;
//	}
//
//	public static double numberOfCoilsMultiLayerCoil(final double minDiameter, final double maxDiameter, final double length, final double wireDiameter, final double inductance) {
//		return 0;
//	}
//
//	public static double inductanceRoundedFlatCoil(final double minDiameter, final double maxDiameter, final double wireWidth, final double numberOfCoils) {
//		return 0;
//	}
//
//	public static double numberOfCoilsRoundedFlatCoil(final double minDiameter, final double maxDiameter, final double wireWidth, final double inductance) {
//		return 0;
//	}
//
//	public static double inductanceSquaredFlatCoil(final double minWidth, final double maxWidth, final double wireWidth, final double numberOfCoils) {
//		return 0;
//	}
//
//	public static double numberOfCoilsSquaredFlatCoil(final double minWidth, final double maxWidth, final double wireWidth, final double inductance) {
//		return 0;
//	}

	/**
	 * <p>Calculate inductance of the ring coil by it's coils</p>
	 * @param coils coils of the ring coil
	 * @param outerDiameter outer diameter of the ring (in mm)
	 * @param innerDiameter inner diameter of the ring (in mm)
	 * @param height height of the ring (in mm)
	 * @param permability magnetic permability of the ring
	 * @return inductance of the ring (in microhenry)
	 * @throws IllegalArgumentException if any parameter is not positive or outer diameter less than inner one  
	 */
	@UseAsFormulaTag(LocalizationKeys.FORMULA_INDUCTANCE_RING_COIL)
	public static double inductanceRingCoil(final int coils, final float outerDiameter, final float innerDiameter, final float height, final int permability) {
		if (coils <= 0) {
			throw new IllegalArgumentException("Number of coils ["+coils+"] must be positive");
		}
		else if (outerDiameter <= 0) {
			throw new IllegalArgumentException("Outer diameter ["+outerDiameter+"] must be positive");
		}
		else if (innerDiameter <= 0) {
			throw new IllegalArgumentException("Inner diameter ["+innerDiameter+"] must be positive");
		}
		else if (innerDiameter >= outerDiameter) {
			throw new IllegalArgumentException("Outer diameter ["+outerDiameter+"] must be greater than inner one ["+innerDiameter+"]");
		}
		else if (height <= 0) {
			throw new IllegalArgumentException("Height ["+height+"] must be positive");
		}
		else if (permability <= 0) {
			throw new IllegalArgumentException("Permability ["+permability+"] must be positive");
		}
		else if (outerDiameter/innerDiameter > 1.75) {
			return 0.0002f * permability * height * coils * coils * Math.log(outerDiameter/innerDiameter);
		}
		else {
			return 0.0004f * permability * height * coils * coils * (outerDiameter - innerDiameter) / (outerDiameter + innerDiameter);
		}
	}

	/**
	 * <p>Calculate coils of the ring coil by it's inductance</p>
	 * @param inductance inductance of the ring coil (in microhenry)
	 * @param outerDiameter outer diameter of the ring (in mm)
	 * @param innerDiameter inner diameter of the ring (in mm)
	 * @param height height of the ring (in mm)
	 * @param permability magnetic permability of the ring
	 * @return number of coils for the ring
	 * @throws IllegalArgumentException if any parameter is not positive or outer diameter less than inner one  
	 */
	@UseAsFormulaTag(LocalizationKeys.FORMULA_COILS_RING_COIL)
	public static int coilsRingCoil(final float inductance, final float outerDiameter, final float innerDiameter, final float height, final int permability) {
		if (inductance <= 0) {
			throw new IllegalArgumentException("Inductance ["+inductance+"] must be positive");
		}
		else if (outerDiameter <= 0) {
			throw new IllegalArgumentException("Outer diameter ["+outerDiameter+"] must be positive");
		}
		else if (innerDiameter <= 0) {
			throw new IllegalArgumentException("Inner diameter ["+innerDiameter+"] must be positive");
		}
		else if (innerDiameter >= outerDiameter) {
			throw new IllegalArgumentException("Outer diameter ["+outerDiameter+"] must be greater than inner one ["+innerDiameter+"]");
		}
		else if (height <= 0) {
			throw new IllegalArgumentException("Height ["+height+"] must be positive");
		}
		else if (permability <= 0) {
			throw new IllegalArgumentException("Permability ["+permability+"] must be positive");
		}
		else if (outerDiameter/innerDiameter > 1.75) {
			return (int) Math.sqrt(inductance / (0.0002f * permability * height * Math.log(outerDiameter/innerDiameter)));  
		}
		else {
			return (int) Math.sqrt(inductance / (0.0004f * permability * height * (outerDiameter - innerDiameter) / (outerDiameter + innerDiameter)));
		}
	}

	/**
	 * <p>Calculate magnetic field density inside the ring coil body</p>
	 * @param current current of the ring coil (in ampers)
	 * @param coils number if coils of the ring coil
	 * @param outerDiameter outer diameter of the ring (in mm)
	 * @param innerDiameter inner diameter of the ring (in mm)
	 * @param permability magnetic permability of the ring
	 * @return magnetic field induction value inside the ring (in teslas)
	 * @throws IllegalArgumentException if any parameter is not positive or outer diameter less than inner one  
	 */
	@UseAsFormulaTag(LocalizationKeys.FORMULA_INDUCTION_RING_COIL)
	public static float inductionRingCoil(final float current, final int coils, final float outerDiameter, final float innerDiameter, final int permability) {
		if (current <= 0) {
			throw new IllegalArgumentException("Current ["+current+"] must be positive"); 
		}
		else if (coils <= 0) {
			throw new IllegalArgumentException("Number of coils ["+coils+"] must be positive"); 
		}
		else if (outerDiameter <= 0) {
			throw new IllegalArgumentException("Outer diameter ["+outerDiameter+"] must be positive");
		}
		else if (innerDiameter <= 0) {
			throw new IllegalArgumentException("Inner diameter ["+innerDiameter+"] must be positive");
		}
		else if (innerDiameter >= outerDiameter) {
			throw new IllegalArgumentException("Outer diameter ["+outerDiameter+"] must be greater than inner one ["+innerDiameter+"]");
		}
		else if (permability <= 0) {
			throw new IllegalArgumentException("Permability ["+permability+"] must be positive");
		}
		else {
			return (float) (MYU_0*permability*current*coils/(Math.PI*(outerDiameter+innerDiameter)/2));
		}
	}
	
	/**
	 * <p>Calculate wire length, new inner and new outer diameter for the ring coil.</p>
	 * @param coils number of coils on the ring 
	 * @param wireDiameter wire diameter (in mm)
	 * @param outerDiameter initial outer diameter of the ring (in mm)
	 * @param innerDiameter initial inner diameter of the ring (in mm)
	 * @param height initial height of the ring (in mm)
	 * @return array of wire length (in mm), new outer diameter of the ring (in mm), new inner diameter of the ring (in mm) and new height of the ring (in mm). If the given number of coils can't
	 * be placed on the given ring, returns null instead of array
	 * @throws IllegalArgumentException if any parameter is not positive or outer diameter less than inner one  
	 */
	public static float[] wireLength4Ring(final int coils, final float wireDiameter, final float outerDiameter, final float innerDiameter, final float height) {
		if (coils <= 0) {
			throw new IllegalArgumentException("Number of coils ["+coils+"] must be positive");
		}
		else if (wireDiameter <= 0) {
			throw new IllegalArgumentException("Wire diameter ["+wireDiameter+"] must be positive");
		}
		else if (outerDiameter <= 0) {
			throw new IllegalArgumentException("Outer diameter ["+outerDiameter+"] must be positive");
		}
		else if (innerDiameter <= 0) {
			throw new IllegalArgumentException("Inner diameter ["+innerDiameter+"] must be positive");
		}
		else if (innerDiameter >= outerDiameter) {
			throw new IllegalArgumentException("Outer diameter ["+outerDiameter+"] must be greater than inner one ["+innerDiameter+"]");
		}
		else if (height <= 0) {
			throw new IllegalArgumentException("Height ["+height+"] must be positive");
		}
		else {
			int 	restOfCoils = coils;
			float 	currentInnerDiameter = innerDiameter - wireDiameter, currentOuterDiameter, currentHeight;
			
			while (restOfCoils > 0 && currentInnerDiameter > 0) {	// Calculate number of layers on the ring
				restOfCoils -= Math.PI * currentInnerDiameter / wireDiameter;
				currentInnerDiameter -= wireDiameter;
			}
			
			if (currentInnerDiameter <= 0) {	// Ring window was filled fully...
				return null;
			}
			else {
				float currentWireLength = 0.0f, currentCoilLength = outerDiameter - innerDiameter + 2 * height + 4 * wireDiameter;
				
				restOfCoils = coils;
				currentInnerDiameter = innerDiameter - wireDiameter;
				currentOuterDiameter = outerDiameter + wireDiameter;
				currentHeight = height + 2 * wireDiameter; 
	
				while (restOfCoils > 0) {
					int coilsInLayer = (int) (Math.PI * currentInnerDiameter / wireDiameter);
					
					currentWireLength += currentCoilLength * Math.min(coilsInLayer,restOfCoils);
					currentInnerDiameter -= wireDiameter;
					currentOuterDiameter += wireDiameter;
					currentHeight += 2 * wireDiameter;
					restOfCoils -= coilsInLayer;
					currentCoilLength += 4 * wireDiameter;
				}
				return new float[]{currentWireLength, currentOuterDiameter, currentInnerDiameter, currentHeight};
			}
		}
	}
}
