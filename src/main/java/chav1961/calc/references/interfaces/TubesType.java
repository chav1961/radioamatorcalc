package chav1961.calc.references.interfaces;

import chav1961.calc.references.tubes.DiodeRecord;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.references.tubes.TubesType/chav1961/calculator/i18n/i18n.xml")
public enum TubesType {
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.diode",tooltip="chav1961.calc.references.tubes.tubesType.diode.tt")
	DIODE(DiodeRecord.class), 
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.doubleDiode",tooltip="chav1961.calc.references.tubes.tubesType.doubleDiode.tt")
	DOUBLE_DIODE(DiodeRecord.class),
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.triode",tooltip="chav1961.calc.references.tubes.tubesType.triode.tt")
	TRIODE(DiodeRecord.class),
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.doubleTriode",tooltip="chav1961.calc.references.tubes.tubesType.doubleTriode.tt")
	DOUBLE_TRIODE(DiodeRecord.class),
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.tetrode",tooltip="chav1961.calc.references.tubes.tubesType.tetrode.tt")
	TETRODE(DiodeRecord.class),
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.doubleTetrode",tooltip="chav1961.calc.references.tubes.tubesType.doubleTetrode.tt")
	DOUBLE_TETRODE(DiodeRecord.class),
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.pentode",tooltip="chav1961.calc.references.tubes.tubesType.pentode.tt")
	PENTODE(DiodeRecord.class),
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.doublePentode",tooltip="chav1961.calc.references.tubes.tubesType.doublePentode.tt")
	DOUBLE_PENTODE(DiodeRecord.class),
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.triodePentode",tooltip="chav1961.calc.references.tubes.tubesType.triodePentode.tt")
	TRIODE_PENTODE(DiodeRecord.class),
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.heptode",tooltip="chav1961.calc.references.tubes.tubesType.heptode.tt")
	HEPTODE(DiodeRecord.class),
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.triodeHeptode",tooltip="chav1961.calc.references.tubes.tubesType.triodeHeptode.tt")
	TRIODE_HEPTODE(DiodeRecord.class),
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.stabilovolt",tooltip="chav1961.calc.references.tubes.tubesType.stabilovolt.tt")
	STABILOVOLT(DiodeRecord.class),
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.baretter",tooltip="chav1961.calc.references.tubes.tubesType.baretter.tt")
	BARETTER(DiodeRecord.class);
	
	private final Class<?>	associated; 
	
	TubesType(Class<?> associated) {
		this.associated = associated;				
	}
	
	public Class<?> getClassAssociated() {
		return associated;
	}
}
