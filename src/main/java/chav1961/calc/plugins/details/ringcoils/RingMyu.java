package chav1961.calc.plugins.details.ringcoils;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.details.ringcoils.RingMyu/chav1961/calculator/i18n/i18n.xml")
public enum RingMyu {
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringmyu.32",tooltip="chav1961.calc.plugins.details.ringcoils.ringmyu.32.tt")
	MYU_32(32), 
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringmyu.50",tooltip="chav1961.calc.plugins.details.ringcoils.ringmyu.50.tt")
	MUI_50(50), 
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringmyu.60",tooltip="chav1961.calc.plugins.details.ringcoils.ringmyu.60.tt")
	MUI_60(60),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringmyu.75",tooltip="chav1961.calc.plugins.details.ringcoils.ringmyu.75.tt")
	MIU_75(75),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringmyu.100",tooltip="chav1961.calc.plugins.details.ringcoils.ringmyu.100.tt")
	MYU_100(100), 
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringmyu.140",tooltip="chav1961.calc.plugins.details.ringcoils.ringmyu.140.tt")
	MUI_140(140),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringmyu.400",tooltip="chav1961.calc.plugins.details.ringcoils.ringmyu.400.tt")
	MUI_400(400),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringmyu.600",tooltip="chav1961.calc.plugins.details.ringcoils.ringmyu.600.tt")
	MUI_600(600),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringmyu.1000",tooltip="chav1961.calc.plugins.details.ringcoils.ringmyu.1000.tt")
	MUI_1000(1000),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringmyu.1500",tooltip="chav1961.calc.plugins.details.ringcoils.ringmyu.1500.tt")
	MUI_1500(1500),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringmyu.2000",tooltip="chav1961.calc.plugins.details.ringcoils.ringmyu.2000.tt")
	MUI_2000(2000),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringmyu.2500",tooltip="chav1961.calc.plugins.details.ringcoils.ringmyu.2500.tt")
	MUI_2500(2500),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringmyu.3000",tooltip="chav1961.calc.plugins.details.ringcoils.ringmyu.3000.tt")
	MUI_3000(3000),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringmyu.6000",tooltip="chav1961.calc.plugins.details.ringcoils.ringmyu.6000.tt")
	MUI_6000(6000);
	
	private final float 	myu;
	
	RingMyu(final float myu) {
		this.myu = myu;
	}
	
	public float getMyu() {
		return myu;
	}
}
