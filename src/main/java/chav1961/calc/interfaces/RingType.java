package chav1961.calc.interfaces;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.interfaces.RingType/chav1961/calculator/i18n/i18n.xml")
public enum RingType {
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K4x2d5x1d2",tooltip="chav1961.calc.interfaces.ringtype.K4x2d5x1d2.tt")
	K4x2d5x1d2(4.0f,2.5f,1.2f,9.84f,0.884f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K4x2d5x1d2pair",tooltip="chav1961.calc.interfaces.ringtype.K4x2d5x1d2pair.tt")
	K4x2d5x1d2pair(4.0f,2.5f,2*1.2f,9.84f,2*0.884f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K4x2d5x1d6",tooltip="chav1961.calc.interfaces.ringtype.K4x2d5x1d6.tt")
	K4x2d5x1d6(4.0f,2.5f,1.6f,9.84f,1.178f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K4x2d5x1d6pair",tooltip="chav1961.calc.interfaces.ringtype.K4x2d5x1d6pair.tt")
	K4x2d5x1d6pair(4.0f,2.5f,2*1.6f,9.84f,2*1.178f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K5x3x1",tooltip="chav1961.calc.interfaces.ringtype.K5x3x1.tt")
	K5x3x1(5.0f,3.0f,1.0f,12.04f,0.978f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K5x3x1pair",tooltip="chav1961.calc.interfaces.ringtype.K5x3x1pair.tt")
	K5x3x1pair(5.0f,3.0f,2*1.0f,12.04f,2*0.978f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K5x3x1d5",tooltip="chav1961.calc.interfaces.ringtype.K5x3x1d5.tt")
	K5x3x1d5(5.0f,3.0f,1.5f,12.04f,1.47f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K5x3x1d5pair",tooltip="chav1961.calc.interfaces.ringtype.K5x3x1d5pair.tt")
	K5x3x1d5pair(5.0f,3.0f,2*1.5f,12.04f,2*1.47f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K7x4x2",tooltip="chav1961.calc.interfaces.ringtype.K7x4x2.tt")
	K7x4x2(7.0f,4.0f,2.0f,16.41f,2.92f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K7x4x2pair",tooltip="chav1961.calc.interfaces.ringtype.K7x4x2.ttpair")
	K7x4x2pair(7.0f,4.0f,2*2.0f,16.41f,2*2.92f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K10x6x3",tooltip="chav1961.calc.interfaces.ringtype.K10x6x3.tt")
	K10x6x3(10.0f,6.0f,3.0f,24.07f,5.90f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K10x6x3pair",tooltip="chav1961.calc.interfaces.ringtype.K10x6x3pair.tt")
	K10x6x3pair(10.0f,6.0f,2*3.0f,24.07f,2*5.90f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K10x6x4d5",tooltip="chav1961.calc.interfaces.ringtype.K10x6x4d5.tt")
	K10x6x4d5(10.0f,6.0f,4.5f,24.07f,8.81f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K10x6x4d5pair",tooltip="chav1961.calc.interfaces.ringtype.K10x6x4d5pair.tt")
	K10x6x4d5pair(10.0f,6.0f,2*4.5f,24.07f,2*8.81f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K10x6x5",tooltip="chav1961.calc.interfaces.ringtype.K10x6x5.tt")
	K10x6x5(10.0f,6.0f,5.0f,24.07f,9.63f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K10x6x5pair",tooltip="chav1961.calc.interfaces.ringtype.K10x6x5pair.tt")
	K10x6x5pair(10.0f,6.0f,2*5.0f,24.07f,2*9.63f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K12x5x5d5",tooltip="chav1961.calc.interfaces.ringtype.K12x5x5d5.tt")
	K12x5x5d5(12.0f,5.0f,5.5f,23.57f,18.07f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K12x5x5d5pair",tooltip="chav1961.calc.interfaces.ringtype.K12x5x5d5pair.tt")
	K12x5x5d5pair(12.0f,5.0f,2*5.5f,23.57f,2*18.07f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K12x6x4d5",tooltip="chav1961.calc.interfaces.ringtype.K12x6x4d5.tt")
	K12x6x4d5(12.0f,6.0f,4.5f,26.13f,12.97f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K12x6x4d5pair",tooltip="chav1961.calc.interfaces.ringtype.K12x6x4d5pair.tt")
	K12x6x4d5pair(12.0f,6.0f,2*4.5f,26.13f,2*12.97f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K12x8x3",tooltip="chav1961.calc.interfaces.ringtype.K12x8x3.tt")
	K12x8x3(12.0f,8.0f,3.0f,30.57f,5.92f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K12x8x3pair",tooltip="chav1961.calc.interfaces.ringtype.K12x8x3pair.tt")
	K12x8x3pair(12.0f,8.0f,2*3.0f,30.57f,2*5.92f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K12x8x6",tooltip="chav1961.calc.interfaces.ringtype.K12x8x6.tt")
	K16x8x6(16.0f,8.0f,6.0f,34.84f,23.06f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K12x8x6pair",tooltip="chav1961.calc.interfaces.ringtype.K12x8x6pair.tt")
	K16x8x6pair(16.0f,8.0f,2*6.0f,34.84f,2*23.06f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K16x10x4d5",tooltip="chav1961.calc.interfaces.ringtype.K16x10x4d5.tt")
	K16x10x4d5(16.0f,10.0f,4.5f,39.37f,13.25f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K16x10x4d5pair",tooltip="chav1961.calc.interfaces.ringtype.K16x10x4d5pair.tt")
	K16x10x4d5pair(16.0f,10.0f,2*4.5f,39.37f,2*13.25f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K17d5x8x5",tooltip="chav1961.calc.interfaces.ringtype.K17d5x8x5.tt")
	K17d5x8d2x5(17.0f,8.2f,5.0f,36.75f,22.17f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K17d5x8x5pair",tooltip="chav1961.calc.interfaces.ringtype.K17d5x8x5pair.tt")
	K17d5x8d2x5pair(17.0f,8.2f,2*5.0f,36.75f,2*22.17f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K20x10x5",tooltip="chav1961.calc.interfaces.ringtype.K20x10x5.tt")
	K20x10x5(20.0f,10.0f,5.0f,43.55f,24.02f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K20x10x5pair",tooltip="chav1961.calc.interfaces.ringtype.K20x10x5pair.tt")
	K20x10x5pair(20.0f,10.0f,2*5.0f,43.55f,2*24.02f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K20x12x6",tooltip="chav1961.calc.interfaces.ringtype.K20x12x6.tt")
	K20x12x6(20.0f,12.0f,6.0f,48.14f,23.48f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K20x12x6pair",tooltip="chav1961.calc.interfaces.ringtype.K20x12x6pair.tt")
	K20x12x6pair(20.0f,12.0f,2*6.0f,48.14f,2*23.48f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K28x16x9",tooltip="chav1961.calc.interfaces.ringtype.K28x16x9.tt")
	K28x16x9(28.0f,16.0f,9.0f,65.64f,52.61f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K28x16x9pair",tooltip="chav1961.calc.interfaces.ringtype.K28x16x9pair.tt")
	K28x16x9pair(28.0f,16.0f,2*9.0f,65.64f,2*52.61f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K31x18d5x7",tooltip="chav1961.calc.interfaces.ringtype.K31x18d5x7.tt")
	K31x18d5x7(31.0f,18.5f,7.0f,36.75f,22.17f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K31x18d5x7pair",tooltip="chav1961.calc.interfaces.ringtype.K31x18d5x7pair.tt")
	K31x18d5x7pair(31.0f,18.5f,2*7.0f,36.75f,2*22.17f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K32x16x8",tooltip="chav1961.calc.interfaces.ringtype.K32x16x8.tt")
	K32x16x8(32.0f,16.0f,8.0f,69.68f,61.50f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K32x16x8pair",tooltip="chav1961.calc.interfaces.ringtype.K32x16x8pair.tt")
	K32x16x8pair(32.0f,16.0f,2*8.0f,69.68f,2*61.50f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K32x16x12",tooltip="chav1961.calc.interfaces.ringtype.K32x16x12.tt")
	K32x16x12(32.0f,16.0f,12.0f,69.68f,92.25f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K32x16x12pair",tooltip="chav1961.calc.interfaces.ringtype.K32x16x12pair.tt")
	K32x16x12pair(32.0f,16.0f,2*12.0f,69.68f,2*92.25f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K32x20x6",tooltip="chav1961.calc.interfaces.ringtype.K32x20x6.tt")
	K32x20x6(32.0f,20.0f,6.0f,78.75f,35.34f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K32x20x6pair",tooltip="chav1961.calc.interfaces.ringtype.K32x20x6pair.tt")
	K32x20x6pair(32.0f,20.0f,2*6.0f,78.75f,2*35.34f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K32x20x9",tooltip="chav1961.calc.interfaces.ringtype.K32x20x9.tt")
	K32x20x9(32.0f,20.0f,9.0f,78.75f,53.02f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K32x20x9pair",tooltip="chav1961.calc.interfaces.ringtype.K32x20x9pair.tt")
	K32x20x9pair(32.0f,20.0f,2*9.0f,78.75f,2*53.02f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K38x24x7",tooltip="chav1961.calc.interfaces.ringtype.K38x24x7.tt")
	K38x24x7(38.0f,24.0f,7.0f,94.04f,48.15f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K38x24x7pair",tooltip="chav1961.calc.interfaces.ringtype.K38x24x7pair.tt")
	K38x24x7pair(38.0f,24.0f,2*7.0f,94.04f,2*48.15f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K40x25x7d5",tooltip="chav1961.calc.interfaces.ringtype.K40x25x7d5.tt")
	K40x25x7d5(40.0f,25.0f,7.5f,98.44f,55.23f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K40x25x7d5pair",tooltip="chav1961.calc.interfaces.ringtype.K40x25x7d5pair.tt")
	K40x25x7d5pair(40.0f,25.0f,2*7.5f,98.44f,2*55.23f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K40x25x11",tooltip="chav1961.calc.interfaces.ringtype.K40x25x11.tt")
	K40x25x11(40.0f,25.0f,11.0f,98.44f,81.11f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K40x25x11pair",tooltip="chav1961.calc.interfaces.ringtype.K40x25x11pair.tt")
	K40x25x11pair(40.0f,25.0f,2*11.0f,98.44f,2*81.11f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K45x28x8",tooltip="chav1961.calc.interfaces.ringtype.K45x28x8.tt")
	K45x28x8(45.0f,28.0f,8.0f,110.47f,66.74f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K45x28x8pair",tooltip="chav1961.calc.interfaces.ringtype.K45x28x8pair.tt")
	K45x28x8pair(45.0f,28.0f,2*8.0f,110.47f,2*66.74f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K45x28x12",tooltip="chav1961.calc.interfaces.ringtype.K45x28x12.tt")
	K45x28x12(45.0f,28.0f,12.0f,110.47f,97.83f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K45x28x12pair",tooltip="chav1961.calc.interfaces.ringtype.K45x28x12pair.tt")
	K45x28x12pair(45.0f,28.0f,2*12.0f,110.47f,2*97.83f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K45x28x16",tooltip="chav1961.calc.interfaces.ringtype.K45x28x16.tt")
	K45x28x16(45.0f,28.0f,16.0f,110.47f,133.39f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K45x28x16pair",tooltip="chav1961.calc.interfaces.ringtype.K45x28x16pair.tt")
	K45x28x16pair(45.0f,28.0f,2*16.0f,110.47f,2*133.39f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K65x40x6",tooltip="chav1961.calc.interfaces.ringtype.K65x40x6.tt")
	K65x40x6(65.0f,40.0f,6.0f,158.62f,73.54f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K65x40x6pair",tooltip="chav1961.calc.interfaces.ringtype.K65x40x6pair.tt")
	K65x40x6pair(65.0f,40.0f,2*6.0f,158.62f,2*73.54f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K65x40x9",tooltip="chav1961.calc.interfaces.ringtype.K65x40x9.tt")
	K65x40x9(65.0f,40.0f,9.0f,158.62f,110.31f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K65x40x9pair",tooltip="chav1961.calc.interfaces.ringtype.K65x40x9pair.tt")
	K65x40x9pair(65.0f,40.0f,2*9.0f,158.62f,2*110.31f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K65x40x10",tooltip="chav1961.calc.interfaces.ringtype.K65x40x10.tt")
	K65x40x10(65.0f,40.0f,10.0f,158.62f,122.51f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K65x40x10pair",tooltip="chav1961.calc.interfaces.ringtype.K65x40x10pair.tt")
	K65x40x10pair(65.0f,40.0f,2*10.0f,158.62f,2*122.51f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K65x40x12",tooltip="chav1961.calc.interfaces.ringtype.K65x40x12.tt")
	K65x40x12(65.0f,40.0f,12.0f,158.62f,147.02f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K65x40x12pair",tooltip="chav1961.calc.interfaces.ringtype.K65x40x12pair.tt")
	K65x40x12pair(65.0f,40.0f,2*12.0f,158.62f,2*147.02f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K65x40x15",tooltip="chav1961.calc.interfaces.ringtype.K65x40x15.tt")
	K65x40x15(65.0f,40.0f,15.0f,158.62f,181.74f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K65x40x15pair",tooltip="chav1961.calc.interfaces.ringtype.K65x40x15pair.tt")
	K65x40x15pair(65.0f,40.0f,2*15.0f,158.62f,2*181.74f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K65x50x6",tooltip="chav1961.calc.interfaces.ringtype.K65x50x6.tt")
	K65x50x6(65.0f,50.0f,6.0f,178.58f,44.85f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K65x50x6pair",tooltip="chav1961.calc.interfaces.ringtype.K65x50x6pair.tt")
	K65x50x6pair(65.0f,50.0f,2*6.0f,178.58f,2*44.85f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K65x50x9",tooltip="chav1961.calc.interfaces.ringtype.K65x50x9.tt")
	K65x50x9(65.0f,50.0f,6.0f,178.58f,67.05f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K65x50x9pair",tooltip="chav1961.calc.interfaces.ringtype.K65x50x9pair.tt")
	K65x50x9pair(65.0f,50.0f,2*6.0f,178.58f,2*67.05f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K65x50x12",tooltip="chav1961.calc.interfaces.ringtype.K65x50x12.tt")
	K65x50x12(65.0f,50.0f,12.0f,178.58f,89.39f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K65x50x12pair",tooltip="chav1961.calc.interfaces.ringtype.K65x50x12pair.tt")
	K65x50x12pair(65.0f,50.0f,2*12.0f,178.58f,2*89.39f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K80x50x7d5",tooltip="chav1961.calc.interfaces.ringtype.K80x50x7d5.tt")
	K80x50x7d5(80.0f,50.0f,7.5f,196.87f,110.45f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K80x50x7d5pair",tooltip="chav1961.calc.interfaces.ringtype.K80x50x7d5pair.tt")
	K80x50x7d5pair(80.0f,50.0f,2*7.5f,196.87f,2*110.45f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K80x50x11",tooltip="chav1961.calc.interfaces.ringtype.K80x50x11.tt")
	K80x50x11(80.0f,50.0f,11.0f,196.87f,161.99f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K80x50x11pair",tooltip="chav1961.calc.interfaces.ringtype.K80x50x11pair.tt")
	K80x50x11pair(80.0f,50.0f,2*11.0f,196.87f,2*161.99f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K80x50x12",tooltip="chav1961.calc.interfaces.ringtype.K80x50x12.tt")
	K80x50x12(80.0f,50.0f,12.0f,196.87f,176.72f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K80x50x12pair",tooltip="chav1961.calc.interfaces.ringtype.K80x50x12pair.tt")
	K80x50x12pair(80.0f,50.0f,2*12.0f,196.87f,2*176.72f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K100x60x7d5",tooltip="chav1961.calc.interfaces.ringtype.K100x60x7d5.tt")
	K100x60x7d5(100.0f,60.0f,7.5f,240.72f,148.26f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K100x60x7d5pair",tooltip="chav1961.calc.interfaces.ringtype.K100x60x7d5pair.tt")
	K100x60x7d5pair(100.0f,60.0f,2*7.5f,240.72f,2*148.26f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K100x60x10",tooltip="chav1961.calc.interfaces.ringtype.K100x60x10.tt")
	K100x60x10(100.0f,60.0f,10.0f,240.72f,195.70f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K100x60x10pair",tooltip="chav1961.calc.interfaces.ringtype.K100x60x10pair.tt")
	K100x60x10pair(100.0f,60.0f,2*10.0f,240.72f,2*195.70f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K100x60x15",tooltip="chav1961.calc.interfaces.ringtype.K100x60x15.tt")
	K100x60x15(100.0f,60.0f,15.0f,240.72f,289.13f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K100x60x15pair",tooltip="chav1961.calc.interfaces.ringtype.K100x60x15pair.tt")
	K100x60x15pair(100.0f,60.0f,2*15.0f,240.72f,2*289.13f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K125x80x8",tooltip="chav1961.calc.interfaces.ringtype.K125x80x8.tt")
	K125x80x8(125.0f,80.0f,8.0f,311.56f,177.04f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K125x80x8pair",tooltip="chav1961.calc.interfaces.ringtype.K125x80x8pair.tt")
	K125x80x8pair(125.0f,80.0f,2*8.0f,311.56f,2*177.04f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K125x80x12",tooltip="chav1961.calc.interfaces.ringtype.K125x80x12.tt")
	K125x80x12(125.0f,80.0f,12.0f,311.56f,265.56f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K125x80x12pair",tooltip="chav1961.calc.interfaces.ringtype.K125x80x12pair.tt")
	K125x80x12pair(125.0f,80.0f,2*12.0f,311.56f,2*265.56f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K125x80x18",tooltip="chav1961.calc.interfaces.ringtype.K125x80x18.tt")
	K125x80x18(125.0f,80.0f,18.0f,311.56f,398.34f),
	@LocaleResource(value="chav1961.calc.interfaces.ringtype.K125x80x18pair",tooltip="chav1961.calc.interfaces.ringtype.K125x80x18pair.tt")
	K125x80x18pair(125.0f,80.0f,2*18.0f,311.56f,2*398.34f);
	
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
	
	public float getSquareWindow() {
		return getHeight() * getInnerDiameter();
	}
}
