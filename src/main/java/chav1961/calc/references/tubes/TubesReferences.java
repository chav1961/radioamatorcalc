package chav1961.calc.references.tubes;

import javax.swing.JTable;

import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.PreparationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.model.ContentMetadataFilter;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;
import chav1961.purelib.ui.swing.AutoBuiltForm;


@LocaleResourceLocation("i18n:xml:root://chav1961.calc.Application/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value = "chav1961.calc.reference.tubesReference", tooltip = "chav1961.calc.reference.tubesReference.tt", icon = "root:/WorkbenchTab!")
public class TubesReferences implements FormManager<Object,TubesReferences> {
	@LocaleResource(value="chav1961.calc.reference.tubesReference.typesType",tooltip="chav1961.calc.reference.tubesReference.typesType.tt")
	@Format("50r")
	public TubesType								tubesType = TubesType.DIODE;
	
	private final Localizer							localizer;
	private final LoggerFacade						logger;
	private final ContentMetadataInterface 			mdi;
	private final AutoBuiltForm<TubesReferences>	form;
	
	public TubesReferences(final Localizer localizer, final LoggerFacade logger) throws LocalizationException, ContentException {
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null"); 
		}
		else if (logger == null) {
			throw new NullPointerException("Logger can't be null"); 
		}
		else {
			this.localizer = localizer;
			this.logger = logger;
			this.mdi = ContentModelFactory.forAnnotatedClass(this.getClass());
			this.form = new AutoBuiltForm<TubesReferences>(mdi, localizer,this,this);
			
			for (Module m : form.getUnnamedModules()) {
				this.getClass().getModule().addExports(this.getClass().getPackageName(),m);
			}
		}
	}
	
	public JTable getTable() {
		return new JTable();
	}

	public AutoBuiltForm<TubesReferences> getForm() {
		return this.form;
	}
	
	@Override
	public RefreshMode onField(TubesReferences inst, Object id, String fieldName, Object oldValue) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public LoggerFacade getLogger() {
		return logger;
	}
}
