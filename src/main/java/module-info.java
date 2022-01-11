module chav1961.radioamatorcalc {
	requires transitive chav1961.purelib;
	requires java.desktop;
	requires java.base;
	requires java.sql;
	
	exports chav1961.calc.interfaces;
	
	opens chav1961.calc to chav1961.purelib;
	opens chav1961.calc.pipe to chav1961.purelib;
	opens chav1961.calc.windows to chav1961.purelib;
	
	uses chav1961.calc.interfaces.PluginInterface;
	provides chav1961.calc.interfaces.PluginInterface with 
		  chav1961.calc.plugins.calc.barfilter.BarFilterFactory
		, chav1961.calc.plugins.calc.activefilter.ActiveFilterFactory
		, chav1961.calc.plugins.calc.superget.SuperGetFactory
		, chav1961.calc.plugins.calc.resonant.tube.ResonantTubeFactory		
		, chav1961.calc.plugins.calc.contour.ContourFactory
		, chav1961.calc.plugins.calc.wienbridge.WienBridgeFactory
		, chav1961.calc.plugins.calc.phaseshift.PhaseShiftFactory
		, chav1961.calc.plugins.calc.timbre.TimbreFactory
		, chav1961.calc.plugins.details.coils.CoilsFactory
		, chav1961.calc.plugins.details.ringcoils.RingCoilsFactory
		, chav1961.calc.plugins.details.kerncoils.KernCoilsFactory
		, chav1961.calc.plugins.details.ringcurrenttrans.RingCurrentTransFactory
		, chav1961.calc.plugins.details.ringmagnetic.RingMagneticFactory
		, chav1961.calc.plugins.details.ringpulsetrans.RingPulseTransFactory
		, chav1961.calc.plugins.details.flybacktrans.FlybackTransFactory
		, chav1961.calc.plugins.devices.pulsestab.PulseStabFactory
		, chav1961.calc.plugins.devices.powerfactor34262.PowerFactor34262Factory
		, chav1961.calc.plugins.devices.stepupautogenerator.StepUpAutoGeneratorFactory
		, chav1961.calc.plugins.devices.forwardconvertor.ForwardConvertorFactory;
}
