package chav1961.calc.references.interfaces;

import javax.swing.Icon;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.references.tubes.TubesType/chav1961/calculator/i18n/i18n.xml")
public enum TubesType {
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.diode",tooltip="chav1961.calc.references.tubes.tubesType.diode.tt")
	DIODE(1, 1), 
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.doubleDiode",tooltip="chav1961.calc.references.tubes.tubesType.doubleDiode.tt")
	DOUBLE_DIODE(1, 2),
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.triode",tooltip="chav1961.calc.references.tubes.tubesType.triode.tt")
	TRIODE(1, 1),
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.doubleTriode",tooltip="chav1961.calc.references.tubes.tubesType.doubleTriode.tt")
	DOUBLE_TRIODE(1, 2),
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.tetrode",tooltip="chav1961.calc.references.tubes.tubesType.tetrode.tt")
	TETRODE(1, 1),
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.doubleTetrode",tooltip="chav1961.calc.references.tubes.tubesType.doubleTetrode.tt")
	DOUBLE_TETRODE(1, 2),
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.pentode",tooltip="chav1961.calc.references.tubes.tubesType.pentode.tt")
	PENTODE(1, 1),
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.doublePentode",tooltip="chav1961.calc.references.tubes.tubesType.doublePentode.tt")
	DOUBLE_PENTODE(1, 2),
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.triodePentode",tooltip="chav1961.calc.references.tubes.tubesType.triodePentode.tt")
	TRIODE_PENTODE(2, 1),
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.heptode",tooltip="chav1961.calc.references.tubes.tubesType.heptode.tt")
	HEPTODE(1, 1),
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.triodeHeptode",tooltip="chav1961.calc.references.tubes.tubesType.triodeHeptode.tt")
	TRIODE_HEPTODE(2, 1),
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.stabilovolt",tooltip="chav1961.calc.references.tubes.tubesType.stabilovolt.tt")
	STABILOVOLT(1, 1),
	@LocaleResource(value="chav1961.calc.references.tubes.tubesType.baretter",tooltip="chav1961.calc.references.tubes.tubesType.baretter.tt")
	BARETTER(1, 1);
	
	private final int		numberOfLampTypes;
	private final int		numberOfLamps;
	
	TubesType(final int numberOfLampTypes, final int numberOfLamps) {
		this.numberOfLampTypes = numberOfLampTypes;
		this.numberOfLamps = numberOfLamps;
	}
	
	public int getNumberOfLampTypes() {
		return numberOfLampTypes;
	}

	public int getNumberOfLamps() {
		return numberOfLamps;
	}
	
	public Icon getIcon() {
		return null;
	}
	
	public static int getMaximumNumberOfLampTypes() {
		int	result = -1;
		
		for(TubesType item : values()) {
			if (result < item.getNumberOfLampTypes()) {
				result = item.getNumberOfLampTypes();
			}
		}
		return result;
	}
}
