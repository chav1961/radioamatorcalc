package chav1961.calc.plugins.details.ringcoils;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.plugins.details.ringcoils.RingType/chav1961/calculator/i18n/i18n.xml")
public enum RingType {
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K4x2d5x1d2",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K4x2d5x1d2.tt")
	K4x2d5x1d2(4.0f,2.5f,1.2f,9.84f,0.884f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K4x2d5x1d6",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K4x2d5x1d6.tt")
	K4x2d5x1d6(4.0f,2.5f,1.6f,9.84f,1.178f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K5x3x1",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K5x3x1.tt")
	K5x3x1(5.0f,3.0f,1.0f,12.04f,0.978f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K5x3x1d5",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K5x3x1d5.tt")
	K5x3x1d5(5.0f,3.0f,1.5f,12.04f,1.47f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K7x4x2",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K7x4x2.tt")
	K7x4x2(7.0f,4.0f,2.0f,16.41f,2.92f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K10x6x3",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K10x6x3.tt")
	K10x6x3(10.0f,6.0f,3.0f,24.07f,5.90f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K10x6x4d5",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K10x6x4d5.tt")
	K10x6x4d5(10.0f,6.0f,4.5f,24.07f,8.81f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K10x6x5",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K10x6x5.tt")
	K10x6x5(10.0f,6.0f,5.0f,24.07f,9.63f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K12x5x5d5",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K12x5x5d5.tt")
	K12x5x5d5(12.0f,5.0f,5.5f,23.57f,18.07f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K12x6x4d5",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K12x6x4d5.tt")
	K12x6x4d5(12.0f,6.0f,4.5f,26.13f,12.97f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K12x8x3",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K12x8x3.tt")
	K12x8x3(12.0f,8.0f,3.0f,30.57f,5.92f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K12x8x6",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K12x8x6.tt")
	K16x8x6(16.0f,8.0f,6.0f,34.84f,23.06f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K16x10x4d5",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K16x10x4d5.tt")
	K16x10x4d5(16.0f,10.0f,4.5f,39.37f,13.25f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K17d5x8x5",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K17d5x8x5.tt")
	K17d5x8d2x5(17.0f,8.2f,5.0f,36.75f,22.17f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K20x10x5",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K20x10x5.tt")
	K20x10x5(20.0f,10.0f,5.0f,43.55f,24.02f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K20x12x6",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K20x12x6.tt")
	K20x12x6(20.0f,12.0f,6.0f,48.14f,23.48f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K28x16x9",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K28x16x9.tt")
	K28x16x9(28.0f,16.0f,9.0f,65.64f,52.61f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K31x18d5x7",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K31x18d5x7.tt")
	K31x18d5x7(31.0f,18.5f,7.0f,36.75f,22.17f),// ?????
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K32x16x8",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K32x16x8.tt")
	K32x16x8(32.0f,16.0f,8.0f,69.68f,61.50f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K32x16x12",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K32x16x12.tt")
	K32x16x12(32.0f,16.0f,12.0f,69.68f,92.25f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K32x20x6",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K32x20x6.tt")
	K32x20x6(32.0f,20.0f,6.0f,78.75f,35.34f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K32x20x9",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K32x20x9.tt")
	K32x20x9(32.0f,20.0f,9.0f,78.75f,53.02f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K38x24x7",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K38x24x7.tt")
	K38x24x7(38.0f,24.0f,7.0f,94.04f,48.15f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K40x25x7d5",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K40x25x7d5.tt")
	K40x25x7d5(40.0f,25.0f,7.5f,98.44f,55.23f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K40x25x11",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K40x25x11.tt")
	K40x25x11(40.0f,25.0f,11.0f,98.44f,81.11f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K45x28x8",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K45x28x8.tt")
	K45x28x8(45.0f,28.0f,8.0f,110.47f,66.74f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K45x28x12",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K45x28x12.tt")
	K45x28x12(45.0f,28.0f,12.0f,110.47f,97.83f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K45x28x16",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K45x28x16.tt")
	K45x28x16(45.0f,28.0f,16.0f,110.47f,133.39f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K65x40x6",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K65x40x6.tt")
	K65x40x6(65.0f,40.0f,6.0f,158.62f,73.54f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K65x40x9",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K65x40x9.tt")
	K65x40x9(65.0f,40.0f,9.0f,158.62f,110.31f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K65x40x10",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K65x40x10.tt")
	K65x40x10(65.0f,40.0f,10.0f,158.62f,122.51f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K65x40x12",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K65x40x12.tt")
	K65x40x12(65.0f,40.0f,12.0f,158.62f,147.02f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K65x40x15",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K65x40x15.tt")
	K65x40x15(65.0f,40.0f,15.0f,158.62f,181.74f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K65x50x6",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K65x50x6.tt")
	K65x50x6(65.0f,50.0f,6.0f,178.58f,44.85f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K65x50x9",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K65x50x9.tt")
	K65x50x9(65.0f,50.0f,6.0f,178.58f,67.05f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K65x50x12",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K65x50x12.tt")
	K65x50x12(65.0f,50.0f,12.0f,178.58f,89.39f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K80x50x7d5",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K80x50x7d5.tt")
	K80x50x7d5(80.0f,50.0f,7.5f,196.87f,110.45f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K80x50x11",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K80x50x11.tt")
	K80x50x11(80.0f,50.0f,11.0f,196.87f,161.99f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K80x50x12",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K80x50x12.tt")
	K80x50x12(80.0f,50.0f,12.0f,196.87f,176.72f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K100x60x7d5",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K100x60x7d5.tt")
	K100x60x7d5(100.0f,60.0f,7.5f,240.72f,148.26f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K100x60x10",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K100x60x10.tt")
	K100x60x10(100.0f,60.0f,10.0f,240.72f,195.70f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K100x60x15",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K100x60x15.tt")
	K100x60x15(100.0f,60.0f,15.0f,240.72f,289.13f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K125x80x8",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K125x80x8.tt")
	K125x80x8(125.0f,80.0f,8.0f,311.56f,177.04f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K125x80x12",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K125x80x12.tt")
	K125x80x12(125.0f,80.0f,12.0f,311.56f,265.56f),
	@LocaleResource(value="chav1961.calc.plugins.details.ringcoils.ringtype.K125x80x18",tooltip="chav1961.calc.plugins.details.ringcoils.ringtype.K125x80x18.tt")
	K125x80x18(125.0f,80.0f,18.0f,311.56f,398.34f);
	
	private final float	dn;
	private final float	dvn;
	private final float	height;
	private final float	lMid;
	private final float	sqA;
	
	RingType(final float dn, final float dvn, final float height, final float lMid, final float sqA) {
		this.dn = dn;
		this.dvn = dvn;
		this.height = height;
		this.lMid = lMid;
		this.sqA = sqA;
	}
	
	public float getOuterDiameter() {
		return dn;
	}

	public float getInnerDiameter() {
		return dvn;
	}

	public float getHeight() {
		return height;
	}

	public float getMiddleLen() {
		return lMid;
	}

	public float getSquare() {
		return sqA;
	}
}
