package chav1961.calc.references.tubes;

import java.net.URI;

import chav1961.calc.interfaces.ReferenceColumn;
import chav1961.calc.references.interfaces.TubeCorpusType;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.ui.interfaces.Format;

@LocaleResourceLocation("i18n:xml:root://chav1961.calc.references.tubes.DiodeRecord/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value="menu.details.ringpulsetrans",tooltip="menu.details.ringpulsetrans.tt",help="help.aboutApplication")
public class DiodeRecord {
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage.tt")
	@Format("30")
	@ReferenceColumn("������������")
	public String		name;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage.tt")
	@Format("30")
	@ReferenceColumn("����������")
	public String		description;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage.tt")
	@Format("20")
	public TubeCorpusType 	corpus;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage.tt")
	@Format("30")
	@ReferenceColumn("*�������")
	public String		analogs;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage.tt")
	@Format("9.2mpzs")
	@ReferenceColumn("*PDF")
	public URI			docRef;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage.tt")
	@Format("9.2mpzs")
	@ReferenceColumn("Ua")	
	public float		Ua;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage.tt")
	@Format("9.2mpzs")
	@ReferenceColumn("Uamax")	
	public float		Uaimp;

	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage.tt")
	@Format("9.2mpzs")
	@ReferenceColumn("U�")	
	public float		Uf;

	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage.tt")
	@Format("9.2mpzs")
	@ReferenceColumn("Ia")	
	public float		Ia;

	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage.tt")
	@Format("9.2mpzs")
	@ReferenceColumn("I�")	
	public float		If;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage.tt")
	@Format("9.2mpzs")
	@ReferenceColumn("Ia")	
	public float		Ifimp;

	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage.tt")
	@Format("9.2mpzs")
	@ReferenceColumn("R��")	
	public float		Rinner;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage.tt")
	@Format("9.2mpzs")
	@ReferenceColumn("�")	
	public float		Capacity;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage.tt")
	@Format("9.2mpzs")
	@ReferenceColumn("����")	
	public float		Unoise;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage.tt")
	@Format("9.2mpzs")
	@ReferenceColumn("Uamax")	
	public float		Uamax;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage.tt")
	@Format("9.2mpzs")
	@ReferenceColumn("U��max")	
	public float		Ufcmax;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage.tt")
	@Format("9.2mpzs")
	@ReferenceColumn("I�max")	
	public float		Icmax;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage.tt")
	@Format("9.2mpzs")
	@ReferenceColumn("I��max")	
	public float		Icimax;
	
	@LocaleResource(value="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage",tooltip="chav1961.calc.plugins.details.ringpulsetrans.inputVoltage.tt")
	@Format("9.2mpzs")
	@ReferenceColumn("Pmax")	
	public float		Pmax;

	@Override
	public String toString() {
		return "DiodeRecord [name=" + name + ", description=" + description + ", corpus=" + corpus + ", analogs="
				+ analogs + ", docRef=" + docRef + ", Ua=" + Ua + ", Uaimp=" + Uaimp + ", Uf=" + Uf + ", Ia=" + Ia
				+ ", If=" + If + ", Ifimp=" + Ifimp + ", Rinner=" + Rinner + ", Capacity=" + Capacity + ", Unoise="
				+ Unoise + ", Uamax=" + Uamax + ", Ufcmax=" + Ufcmax + ", Icmax=" + Icmax + ", Icimax=" + Icimax
				+ ", Pmax=" + Pmax + "]";
	}
}
