package chav1961.calc.plugins.math;

import chav1961.calc.interfaces.RingMyu;
import chav1961.calc.interfaces.RingType;

public class MathUtils {
	public static final double	ETHA = 0.9f; 

	/**
	 * <p>Calculate overall power of ring for the given frequency, power and induction</p>
	 * @param ringType type of the ring (can't be null)
	 * @param ringMyu ring permability (can't be null)
	 * @param induction induction inside the ring [Tesla] (must be positive)
	 * @param frequency conversion frequency [kHz] (must be positive)
	 * @param power target power [Watt] (must be positive)
	 * @return overall power available [Watt]
	 * @throws NullPointerException ringType or ringMyu is null
	 * @throws IllegalArgumentException double arguments are not positive
	 */
	public static double calculateOverallPower(final RingType ringType, final RingMyu ringMyu, final double induction, final double frequency, final double power) throws NullPointerException, IllegalArgumentException {
		if (ringType == null) {
			throw new NullPointerException("Ring type can't be null");
		}
		else if (ringMyu == null) {
			throw new NullPointerException("Ring permeability can't be null");
		}
		else if (induction <= 0) {
			throw new IllegalArgumentException("Induction must have positive value");
		}
		else if (frequency <= 0) {
			throw new IllegalArgumentException("Induction must have positive value");
		}
		else if (power <= 0) {
			throw new IllegalArgumentException("Power must have positive value");
		}
		else {
			final double	squareC = ringType.getSquare() * 1e-6;
			final double	squareO = Math.PI * ringType.getInnerDiameter() * ringType.getInnerDiameter() / 4e6;
			final double	bm = 0.625 * induction;
			final double	s = 1;
			final double	kc = 1;
			final double	km = power < 15 ? 0.1 : 0.15;
			final double	kf = 1;
			double	j = 1.87, jOld = 0;	// 1.5 + 24/Math.sqrt(pGab); 
			double	pGab = 2e9 * squareC * squareO * frequency * bm * ETHA * j * s * kc * km * kf;
	
			while (Math.abs(jOld - j)/j > 0.1) {	// Iterate value...
				jOld = j;
				j = 1.5 + 24/Math.sqrt(pGab);
				pGab = 2e9 * squareC * squareO * frequency * bm * ETHA * j * s * kc * km * kf;
			}
			return pGab;
		}
	}
}
