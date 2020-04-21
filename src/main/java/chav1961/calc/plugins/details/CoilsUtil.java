package chav1961.calc.plugins.details;

import chav1961.calc.plugins.calc.contour.ContourPlugin;

// Большая часть кода взята из https://github.com/radioacoustick/Coil64 с некоторыми доработками под синтаксис Java
public class CoilsUtil {
	public static final double		MYU0 = 4e-7 * Math.PI;

    private static final double		SQRT_2 = Math.sqrt(2); 

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// aluminium, copper, silver, tin -> (Rho, Chi, Alpha) array of material parameters
	/// from tables http://www.g3ynh.info/zdocs/comps/part_1.html
	/// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public enum Material {
    	ALUMINIUM(2.824e-8, 2.21e-5, 0.0039),
    	COPPER(1.7241e-8, - 9.56e-6, 0.00393),
    	SILVER(1.59e-8, - 2.63e-5, 0.0038),
    	TIN(1.15e-7, 2.4e-6, 0.0042);
    	
    	private final double	rho;
    	private final double	chi;
    	private final double	alpha;
    	
    	Material(final double rho, final double chi, final double alpha) {
    		this.rho = rho;
    		this.chi = chi;
    		this.alpha = alpha;
    	}
    	
    	public double getRho() {
    		return rho;
    	}
    	
    	public double getChi() {
    		return chi;
    	}
    	
    	public double getAlpha() {
    		return alpha;
    	}
    }

    private static double commonLinearInterpolation(final double argument, final double[] rangeTable, final double[] resultTable, final double outBoundValue) {
	    int 	zi;
	    double 	result = 0;
	    
	    if (argument > rangeTable[rangeTable.length-1]) {
	        result = outBoundValue;
	    } else {
	        zi = 0;
	        while ((argument >= rangeTable[zi]) && (zi < rangeTable.length - 1)) {
	            zi++;
	            if (zi > 0) {
	                result = ((resultTable[zi] - resultTable[zi - 1]) / (rangeTable[zi] - rangeTable[zi - 1])) * (argument - rangeTable[zi - 1]) + resultTable[zi - 1];
	            }
	        }
	    }
	    return result;
    }
    
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// LINEAR INTERPOLATION FUNCTION FROM ARNOLD'S TABLES///
	/// https://ieeexplore.ieee.org/document/5240803
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
    private static final double 	ZA_TABLE7_PHI[] = {0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2.0, 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 2.8, 2.9,
            								3.0, 3.2, 3.4, 3.6, 3.8, 4.0, 4.2, 4.4, 4.6, 4.8, 5.0, 5.2, 5.4, 5.6, 5.8, 6.0, 6.5, 7.0, 7.5, 8.0, 8.5, 9.0, 9.5, 10.0};
    private static final double		FI_TABLE7_PHI[] = {0.001, 0.003, 0.005, 0.008, 0.013, 0.02, 0.029, 0.041, 0.055, 0.072, 0.092, 0.114, 0.139, 0.167, 0.196, 0.226, 0.257,
								            0.288, 0.319, 0.349, 0.378, 0.406, 0.432, 0.457, 0.48, 0.501, 0.539, 0.571, 0.599, 0.622, 0.643, 0.661, 0.677, 0.691,
								            0.704, 0.716, 0.727, 0.737, 0.746, 0.755, 0.763, 0.782, 0.797, 0.811, 0.823, 0.833, 0.843, 0.851, 0.858};


	private static double LinearInterpolation_Table7_Phi(double z){
	    return commonLinearInterpolation(z,ZA_TABLE7_PHI,FI_TABLE7_PHI,1 - (SQRT_2 / z));
	}

    private static final double 	ZA_TABLE7_CHI[] = {0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2.0, 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 2.8, 2.9,
            								3.0, 3.2, 3.4, 3.6, 3.8, 4.0, 4.2, 4.4, 4.6, 4.8, 5.0, 5.2, 5.4, 5.6, 5.8, 6.0, 6.5, 7.0, 7.5, 8.0, 8.5, 9.0, 9.5, 10.0};
    private static final double 	CI_TABLE7_CHI[] = {0.001, 0.002, 0.004, 0.006, 0.010, 0.015, 0.022, 0.03, 0.041, 0.053, 0.067, 0.083, 0.101, 0.12, 0.14, 0.16, 0.18, 0.199,
            								0.218, 0.235, 0.251, 0.265, 0.278, 0.289, 0.299, 0.307, 0.32, 0.33, 0.337, 0.343, 0.348, 0.353, 0.357, 0.361, 0.365, 0.37,
            								0.374, 0.378, 0.382, 0.386, 0.389, 0.397, 0.404, 0.41, 0.416, 0.42, 0.425, 0.428, 0.432};
	
	private static double LinearInterpolation_Table7_Chi(double z){
	    return commonLinearInterpolation(z,ZA_TABLE7_CHI,CI_TABLE7_CHI,0.5 - (1 / (z * SQRT_2)));
	}
	
    private static final double 	NA_TABLE8_V[] = {2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 16, 18, 20, 25, 30, 35, 40, 45, 50, 67, 100, 125, 167, 250, 500};
    private static final double		V_TABLE8_V[] ={-1, -0.25, 0.21, 0.52, 0.76, 0.94, 1.09, 1.22, 1.33, 1.5, 1.63, 1.73, 1.82, 1.91, 2.06, 2.17, 2.26, 2.33, 2.39, 2.44,
                 						2.56, 2.71, 2.77, 2.85, 2.94, 3.05};
	
	private static double LinearInterpolation_Table8_v(double N){
	    return commonLinearInterpolation(N,NA_TABLE8_V,V_TABLE8_V,3.29);
	}

    private static final double 	NA_TABLE8_W[] = {2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 16, 18, 20, 25, 30, 35, 40, 45, 50, 67, 100, 125, 167, 250, 500};
    private static final double 	W_TABLE8_W[] = {1, 2.4, 3.5, 4.3, 4.9, 5.4, 5.9, 6.3, 6.6, 7.0, 7.4, 7.8, 8.0, 8.2, 8.6, 8.9, 9.1, 9.3, 9.4, 9.5, 9.7, 10.0, 10.2, 10.3, 10.4, 10.6};
	
	private static double LinearInterpolation_Table8_w(double N){
	    return commonLinearInterpolation(N,NA_TABLE8_W,W_TABLE8_W,10.8);
	}

    private static final double 	NA_TABLE3[] = {2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 16, 18, 20, 25, 34, 50, 100};
    private static final double 	P_TABLE3[] = {2.29, 1.79, 1.48, 1.28, 1.12, 1.01, 0.91, 0.84, 0.78, 0.68, 0.60, 0.54, 0.50, 0.46, 0.39, 0.31, 0.22, 0.12};
	
	private static double LinearInterpolation_Table3(double N){
	    return commonLinearInterpolation(N,NA_TABLE3,P_TABLE3,1e-6);
	}

	private static final double 	ROA_TABLE5[] = {0.1, 0.2, 0.3, 0.4, 0.6, 0.8, 1, 1.2, 1.4, 1.6, 1.8, 2, 2.2, 2.4, 2.6, 2.8, 3, 3.2, 3.4, 3.6, 3.8, 4, 4.2, 4.4, 4.6, 4.8, 5.0,
            								5.6, 6.25, 7.14, 7.7, 8.33, 9.1, 10, 11.1, 12.5, 14.28, 16.66, 20, 25, 33.33, 50, 100};
	private static final double 	G_TABLE5[] = {0.06, 0.18, 0.32, 0.49, 0.85, 1.23, 1.61, 1.99, 2.36, 2.71, 3.04, 3.35, 3.63, 3.90, 4.16, 4.39, 4.61, 4.82, 5.01, 5.19,
											5.36, 5.51, 5.66, 5.80, 5.93, 6.06, 6.18, 6.46, 6.76, 7.09, 7.26, 7.43, 7.61, 7.79, 7.98, 8.17, 8.37, 8.57, 8.78, 9.00, 9.21, 9.43, 9.65};
	
	private static double LinearInterpolation_Table5(double ro){
	    return commonLinearInterpolation(ro,ROA_TABLE5,G_TABLE5,9.87);
	}
	
    private static final double 	RA_TABLE4_A[] = {0, 0.2, 0.4, 0.6, 0.8, 1.0, 1.2, 1.4, 1.6, 1.8, 2.0, 2.2, 2.4, 2.6, 2.8, 3.0, 3.5, 4.0, 4.5, 5.0, 5.5, 6.0, 6.5, 7.0, 7.5, 8.0};
    private static final double 	A_TABLE4_A[] = {3.29, 3.24, 3.13, 2.99, 2.83, 2.67, 2.51, 2.36, 2.21, 2.07, 1.95, 1.83, 1.72, 1.63, 1.54, 1.46, 1.28, 1.14, 1.02, 0.93,
                  										0.85, 0.78, 0.72, 0.67, 0.63, 0.59};
	
	private static double LinearInterpolation_Table4_a(double r){
	    return commonLinearInterpolation(r,RA_TABLE4_A,A_TABLE4_A,19 / (4 * r));
	}
	
    private static final double 	RA_TABLE4_B[] = {0, 0.2, 0.4, 0.6, 0.8, 1.0, 1.2, 1.4, 1.6, 1.8, 2.0, 2.2, 2.4, 2.6, 2.8, 3.0, 3.5, 4.0, 4.5, 5.0, 5.5, 6.0, 6.5, 7.0, 7.5, 8.0};
    private static final double 	B_TABLE4_B[] = {0, -0.03, -0.04, -0.04, -0.03, -0.02, -0.01, 0, 0.01, 0.02, 0.03, 0.04, 0.04, 0.05, 0.06, 0.06, 0.08, 0.09, 0.1, 0.11, 0.12,
                  							0.12, 0.13, 0.13, 0.13, 0.14};
	
	private static double LinearInterpolation_Table4_b(double r){
	    return commonLinearInterpolation(r,RA_TABLE4_B,B_TABLE4_B,4 / 19 - 3 / (5 * r));
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///CALCULATING PROXIMITY EFFECT FACTOR BY ARNOLD RESEARCH
	///https://ieeexplore.ieee.org/document/5240803
	/// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static double lookup_Psi(double Df, double dw, double pt, double N, double fm, Material mt){
	    //el->winding length [mm], Df->coilformer diameter [mm], pt->winding pitch [mm], Dw->wire diameter [mm], fm -> frequency [kHz]
	    final double 	d, P, rc, f, si, zeta, eta, ro, z, Phi, Chi, v, w, alpha, betta, gamma, pm, a, b, u1, u2, g;

	    d = dw / 10;
	    P = pt / 10;
	    rc = Df / 20;
	    f = fm * 1e3;
	    si = 1e-11 / mt.getRho();
	    eta = d / P;
	    zeta = P / rc;
	    ro = (N - 1) * zeta;
	    z = Math.PI * d * Math.sqrt(2 * f * si);
	    Phi = LinearInterpolation_Table7_Phi(z);
	    Chi = LinearInterpolation_Table7_Chi(z);
	    v = LinearInterpolation_Table8_v(N);
	    w = LinearInterpolation_Table8_w(N);
	    alpha = (1 + 0.209 * w * Math.pow(Phi, 2) * Math.pow(eta, 4)) / (1 + 0.084 * w * Math.pow(Phi, 2) * Math.pow(eta, 4));
	    betta = 1 / (Math.pow((1 - 0.278 * v * Phi * eta * eta), 1.8));
	    gamma = 1 / (Math.pow((1 + 0.385 * v * Phi * eta * eta), 1.3));
	    pm = LinearInterpolation_Table3(N);
	    a = LinearInterpolation_Table4_a(ro);
	    b = LinearInterpolation_Table4_b(ro);
	    u1 = a - (1 / (1 / pm + ro * b));
	    g = LinearInterpolation_Table5(ro);
	    u2 = g / (1 + (0.42 + (0.3 / ro)) * Math.sqrt(zeta) + (1.35 + (2.8 / Math.sqrt(ro))) * Math.pow(zeta, 1.5));
	    
	    return alpha + eta * eta * Chi * (betta * u1 + gamma * u2);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// CALCULATING SKIN-EFFECT FACTOR FOR ROUND WIRE BY D.KNIGHT RESEARCH
	///http://www.g3ynh.info/zdocs/comps/Zint.pdf
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static double get_Xir(Material mt, double fm, double dw){
	    //AC resistance factor. TED-MLD formula (skin effect factor)
	    final double y, z, f, r, delta_i, delta_i_prim;

	    f = fm * 1e3;
	    r = dw / 2000;
	    delta_i = Math.sqrt(mt.getRho() / (f * Math.PI * MYU0 * (1 + mt.getChi())));
	    delta_i_prim = delta_i * (1 - Math.exp(-r / delta_i));
	    z = 0.62006 * r / delta_i;
	    y = 0.189774 / Math.pow((1 + 0.272481 * Math.pow((Math.pow(z, 1.82938) - Math.pow(z, -0.99457)), 2)), 1.0941);
	    return r * r / (2 * r * delta_i_prim - delta_i_prim * delta_i_prim) * (1 - y);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// CALCULATING SKIN-EFFECT FACTOR FOR RECTANGULAR CONDUCTOR BY ALAN PAYNE RESEARCH
	/// http://g3rbj.co.uk/wp-content/uploads/2017/06/The-ac-Resistance-of-Rectangular-Conductors-Payne-Issue-3.pdf
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static double get_Xic(Material mt, double f, double w, double t){
	    //AC resistance factor formula 4.8.1
	    final double delta = Math.sqrt(mt.getRho() / (f * Math.PI * MYU0 * (1 + mt.getChi())));
	    final double p = Math.sqrt(w * t)/(1.26 * delta);
	    final double Ff = 1 - Math.exp(-0.026 * p);
	    final double x = ((2 * delta / t) * (1 + t / w) + 8 * Math.pow(delta / t, 3)/(w / t)) / (Math.pow(w / t, 0.33)*Math.exp(-3.5*(t / delta)) + 1);
	    final double Kc = 1 + Ff * (1.2 / Math.exp(2.1 * (t / w)) + 1.2 / Math.exp(2.1 * (w / t)));
	    
	    return Kc / (1 - Math.exp(-x));
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// PUBLIC FUNCTIONS REALIZATION
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// Q-FACTOR OF THE ONE-LAYER COIL WITH ROUND WIRE (WITH PROXIMITY EFFECT)
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * <p>Pacчет конструктивной добротности однослойной катушки с круглым проводом.</p>
	 * @param I индуктивность катушки (uH)
	 * @param Df внутренний диаметр катушки (мм)
	 * @param pm шаг намотки (мм)
	 * @param dw диаметр провода (мм)
	 * @param fa частота для расчета добротности (кГц)
	 * @param N число витков катушки
	 * @param mt материал катушки
	 * @return рассчитанная конструктивная добротность
	 */
	public static long solve_Qr(final double I, final double Df, final double pm, final double dw, final double fa, final double N, final Material mt){
	    //I->inductance ┬╡H
	    //l->winding length mm, Df->coilwinding diameter mm, pm->winding pitch mm, dw->wire diameter mm
	    //fm->frequency MHz, N->number of turns, mt->material of wire
		final double Cs = ContourPlugin.capacityByFrequencyAndInductance(fa*1000,I);
	    double Induct, fm, f, D, r, p, WireLength, Rdc, Rac0, Rac, Xi, Psi, kQ, R_ind, Rl, Rc;

	    Induct = I * 1.0e-6;
	    fm = fa * 1e3;
	    f = fm * 1e3;
	    D = Df / 1000;
	    r = dw / 2000;
	    p = pm / 1000;
	    WireLength = Math.PI * N * Math.sqrt(D * D + p * p / 4);
	    Rdc = mt.getRho() * WireLength / (Math.PI * r * r);
	    Xi = get_Xir(mt, fm, dw);
	    Psi = lookup_Psi(Df, dw, pm, N, fm, mt);
	    Rac0 = Rdc * (1 + ((Xi - 1) * Psi * (N - 1 + 1 / Psi)) / N);
	    kQ = Math.pow((1 - (fm * I * Cs) / 2.53e7), 2);
	    Rac = Rac0 / kQ;
	    Rl = 2 * Math.PI * f * Induct;
	    Rc = 1 / (2 * Math.PI * f * Cs * 1e-12);
	    R_ind = 1 / (1 / Rl + 1 / Rc);
	    return Math.round(R_ind / Rac);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// Q-FACTOR OF THE ONE-LAYER COIL WITH RECTANGULAR WIRE
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static long solve_Qc(double I, double Df, double pm, double _w, double _t, double fa,  double N, double Cs, Material mt){
	    //I->inductance ┬╡H
	    //l->winding length mm, Df->coilwinding diameter mm, pm->winding pitch mm, _w->wire width mm, _t->wire thickness mm
	    //fm->frequency MHz, N->number of turns, mt->material of wire
	    final double Induct, fm, f, D, w, t, p, WireLength, Rdc, Rac0, Rac, Xi, kQ, R_ind, Rl, Rc, Psi;

	    Induct = I * 1e-6;
	    fm = fa * 1e3;
	    f = fm * 1e3;
	    D = Df / 1000;
	    w = _w / 1000;
	    t = _t / 1000;
	    p = pm / 1000;
	    WireLength = Math.PI * N * Math.sqrt(D * D + p * p / 4);
	    Rdc = mt.getRho() * WireLength / (w * t);
	    Xi = get_Xic(mt, f, w, t);
	    Psi = lookup_Psi(Df, _w, pm, N, fm, mt);
	    Rac0 = Rdc * (1 + ((Xi - 1) * Psi * (N - 1 + 1 / Psi)) / N);
	    kQ = Math.pow((1 - (fm * I * Cs) / 2.53e7), 2);
	    Rac = Rac0 / kQ;
	    Rl = 2 * Math.PI * f * Induct;
	    Rc = 1 / (2 * Math.PI * f * Cs * 1e-12);
	    R_ind = 1 / (1 / Rl + 1 / Rc);
	    return Math.round(R_ind / Rac);
	}

    private static final double 	WIREPITCH_INDUCT[] = {0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9, 0.95, 1.0};
    private static final double 	A_INDUCT[] = {-2.2, -1.8, -1.4, -1.1, -0.8, -0.65, -0.5, -0.35, -0.2, -0.15, -0.05, 0.05, 0.1, 0.18, 0.25, 0.3, 0.37, 0.45, 0.5, 0.55};
	
	public static double getAForInductance(final double wireDiameter, final double step) {
	    return commonLinearInterpolation(wireDiameter/step,WIREPITCH_INDUCT,A_INDUCT,0.6);
	}

	private static final double		COILS_INDUCT[] = {0,1,2,3,4,5,6,7,8,10,15,20,30,40,50,80};
	private static final double		B_INDUCT[] = {0,0,0.12,0.16,0.19,0.22,0.23,0.24,0.25,0.26,0.28,0.295,0.31,0.315,0.32,0.325};
	
	public static double getBForInductance(final double numberOfCoils) {
	    return commonLinearInterpolation(numberOfCoils,COILS_INDUCT,B_INDUCT,0.327);
	}
	
	private static final double		DIAMETER_FF[] = {0.08,0.11,0.15,0.25,0.35,0.41,0.51,0.91};
	private static final double		K_FF[] = {1.35,1.3,1.275,1.25,1.225,1.2,1.15,1.1};
	
	public static double getFillFactorForWire(final float wireDiameter) {
	    return commonLinearInterpolation(wireDiameter,DIAMETER_FF,K_FF,1.05);
	}
}
