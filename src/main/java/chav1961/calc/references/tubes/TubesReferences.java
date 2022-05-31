package chav1961.calc.references.tubes;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import chav1961.calc.references.ReferenceUtil;
import chav1961.purelib.basic.GettersAndSettersFactory;
import chav1961.purelib.basic.GettersAndSettersFactory.BooleanGetterAndSetter;
import chav1961.purelib.basic.GettersAndSettersFactory.ByteGetterAndSetter;
import chav1961.purelib.basic.GettersAndSettersFactory.CharGetterAndSetter;
import chav1961.purelib.basic.GettersAndSettersFactory.DoubleGetterAndSetter;
import chav1961.purelib.basic.GettersAndSettersFactory.FloatGetterAndSetter;
import chav1961.purelib.basic.GettersAndSettersFactory.GetterAndSetter;
import chav1961.purelib.basic.GettersAndSettersFactory.IntGetterAndSetter;
import chav1961.purelib.basic.GettersAndSettersFactory.LongGetterAndSetter;
import chav1961.purelib.basic.GettersAndSettersFactory.ObjectGetterAndSetter;
import chav1961.purelib.basic.GettersAndSettersFactory.ShortGetterAndSetter;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.cdb.CompilerUtils;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.sql.content.ResultSetFactory;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;
import chav1961.purelib.ui.swing.AutoBuiltForm;


@LocaleResourceLocation("i18n:xml:root://chav1961.calc.Application/chav1961/calculator/i18n/i18n.xml")
@LocaleResource(value = "chav1961.calc.reference.tubesReference", tooltip = "chav1961.calc.reference.tubesReference.tt", icon = "root:/WorkbenchTab!")
public class TubesReferences implements FormManager<Object,TubesReferences>, LocaleChangeListener {
	public static final String						REFERENCE_NAME = "chav1961.calc.reference.tubesReference";	
	
	@LocaleResource(value="chav1961.calc.reference.tubesReference.typesType",tooltip="chav1961.calc.reference.tubesReference.typesType.tt")
	@Format("50r")
	public TubesType								tubesType = TubesType.DIODE;
	
	private final Localizer							localizer;
	private final LoggerFacade						logger;
	private final ContentMetadataInterface 			mdi;
	private final AutoBuiltForm<TubesReferences,?>	form;
	private final InnerTableModel					model = new InnerTableModel();
	private final JTable							table = new JTable(model);
	
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
			this.form = new AutoBuiltForm<TubesReferences,Object>(mdi,localizer,PureLibSettings.INTERNAL_LOADER,this,this);
			
			for (Module m : form.getUnnamedModules()) {
				this.getClass().getModule().addExports(this.getClass().getPackageName(),m);
			}
			try{onField(this, null, "tubesType", TubesType.DIODE, false);
			} catch (FlowException e) {
				throw new ContentException(e.getLocalizedMessage());
			}
		}
	}
	
	public JTable getTable() {
		return table;
	}

	public AutoBuiltForm<TubesReferences,?> getForm() {
		return this.form;
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		form.localeChanged(oldLocale, newLocale);
		model.refreshModel(tubesType,tubesType.getClassAssociated(),localizer);
	}
	
	@Override
	public RefreshMode onField(final TubesReferences inst, final Object id, final String fieldName, final Object oldValue, final boolean beforeCommit) throws FlowException, LocalizationException {
		switch (fieldName) {
			case "tubesType" :
				model.refreshModel(tubesType,tubesType.getClassAssociated(),localizer);
				return RefreshMode.TOTAL;
			default :
				return RefreshMode.DEFAULT;
		}
	}

	@Override
	public LoggerFacade getLogger() {
		return logger;
	}
	
	private class InnerTableModel extends DefaultTableModel {
		private Localizer					localizer;
		private ContentMetadataInterface	mdi = null;
		private Object[]					content = null;
		private GetterAndSetter[]			gas;
		
		public void refreshModel(final TubesType type, final Class<?> contentClass, final Localizer localizer) {
			try{final URL	zip = ReferenceUtil.class.getResource("test.zip"); 
				final URI	ref = URI.create(ResultSetFactory.RESULTSET_PARSERS_SCHEMA+":csv:jar:"+zip.toURI()+"!/diodes/content.csv?allowemptycolumn=true");

				this.mdi = ContentModelFactory.forAnnotatedClass(contentClass);
				this.localizer = LocalizerFactory.getLocalizer(mdi.getRoot().getLocalizerAssociated());
				this.content = ReferenceUtil.loadCSV(contentClass,ref);
				this.gas = new GetterAndSetter[mdi.getRoot().getChildrenCount()];
				
				for (int index = 0; index < gas.length; index++) {
					gas[index] = GettersAndSettersFactory.buildGetterAndSetter(contentClass,this.mdi.getRoot().getChild(index).getName());
				}
				
				fireTableStructureChanged();
			} catch (LocalizationException | ContentException | URISyntaxException e) {
				logger.message(Severity.error,"Error loading reference for ["+type+"]: "+e.getLocalizedMessage(),e);
			}
		}
		
		@Override
		public int getRowCount() {
			if (content == null) {
				return 0;
			}
			else {
				return content.length;
			}
		}

		@Override
		public int getColumnCount() {
			if (content != null) {
				return mdi.getRoot().getChildrenCount();
			}
			else {
				return 0;
			}
		}

		@Override
		public String getColumnName(int columnIndex) {
			return mdi.getRoot().getChild(columnIndex).getName();
//			if (content != null) {
//				try{return localizer.getValue(mdi.getRoot().getChild(columnIndex).getLabelId());
//				} catch (LocalizationException e) {
//					return mdi.getRoot().getChild(columnIndex).getLabelId();
//				}
//			}
//			else {
//				return "";
//			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (content != null) {
				return mdi.getRoot().getChild(columnIndex).getType();
			}
			else {
				return Object.class;
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (content != null) {
				final GetterAndSetter	g = gas[columnIndex];
				
				try{switch (g.getClassType()) {
						case CompilerUtils.CLASSTYPE_BOOLEAN	:
							return ((BooleanGetterAndSetter)g).get(content[rowIndex]);
						case CompilerUtils.CLASSTYPE_REFERENCE	:
							return ((ObjectGetterAndSetter<?>)g).get(content[rowIndex]);
						case CompilerUtils.CLASSTYPE_BYTE		:
							return ((ByteGetterAndSetter)g).get(content[rowIndex]);
						case CompilerUtils.CLASSTYPE_SHORT		:
							return ((ShortGetterAndSetter)g).get(content[rowIndex]);
						case CompilerUtils.CLASSTYPE_CHAR		:	
							return ((CharGetterAndSetter)g).get(content[rowIndex]);
						case CompilerUtils.CLASSTYPE_INT		:	
							return ((IntGetterAndSetter)g).get(content[rowIndex]);
						case CompilerUtils.CLASSTYPE_LONG		:	
							return ((LongGetterAndSetter)g).get(content[rowIndex]);
						case CompilerUtils.CLASSTYPE_FLOAT		:	
							return ((FloatGetterAndSetter)g).get(content[rowIndex]);
						case CompilerUtils.CLASSTYPE_DOUBLE		:
							return ((DoubleGetterAndSetter)g).get(content[rowIndex]);
						default :
							return null;
					}
				} catch (ContentException e) {
					logger.message(Severity.warning,"Row ["+rowIndex+"], col["+columnIndex+"]: error reading value ("+e.getLocalizedMessage()+")",e);
					return null;
				}
			}
			else {
				return null;
			}
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		}
	}
}
