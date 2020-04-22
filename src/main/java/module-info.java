module chav1961.radioamatorcalc {
	requires transitive chav1961.purelib;
	requires java.desktop;
	
	exports chav1961.calc.interfaces;
	
	opens chav1961.calc to chav1961.purelib;
	opens chav1961.calc.pipe to chav1961.purelib;
	opens chav1961.calc.windows to chav1961.purelib;
	
	uses chav1961.calc.interfaces.PluginInterface;
	provides chav1961.calc.interfaces.PluginInterface with 
		  chav1961.calc.plugins.calc.contour.ContourFactory
		, chav1961.calc.plugins.calc.wienbridge.WienBridgeFactory
		, chav1961.calc.plugins.calc.phaseshift.PhaseShiftFactory
		, chav1961.calc.plugins.details.coils.CoilsFactory
		, chav1961.calc.plugins.details.ringcoils.RingCoilsFactory
		, chav1961.calc.plugins.details.ringpulsetrans.RingPulseTransFactory;
}
