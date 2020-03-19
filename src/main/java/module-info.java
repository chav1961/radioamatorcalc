module chav1961.radioamatorcalc {
	requires transitive chav1961.purelib;

	opens chav1961.calc to chav1961.purelib;
	opens chav1961.calc.windows to chav1961.purelib;
}
