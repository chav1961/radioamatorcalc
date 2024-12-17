package chav1961.calc.references.tubes;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.lang.classfile.constantpool.NameAndTypeEntry;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import chav1961.calc.references.interfaces.TubeDescriptor;
import chav1961.calc.references.interfaces.TubePanelGroup;
import chav1961.calc.references.interfaces.TubePanelType;
import chav1961.purelib.basic.NamedValue;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;

class TubesPreview extends JPanel implements LocaleChangeListener {
	private static final long 	serialVersionUID = -2153048973150602961L;
	private static final int	PARM_COUNT = 2;
	private static final String	COL_NAME = "chav1961.calc.reference.tubesReference.preview.table.name"; 
	private static final String	COL_VALUE = "chav1961.calc.reference.tubesReference.preview.table.value"; 

	private final Localizer	localizer;
	
	private JLabel				scheme = new JLabel();
	private JLabel				panel = new JLabel();
	private JLabel				abbr = new JLabel("", JLabel.CENTER);
	private JEditorPane			description = new JEditorPane("text/html","");
	private final JPanel		tabArea = new JPanel(new GridLayout(1, PARM_COUNT));
	private final JScrollPane[]	scrolls = new JScrollPane[PARM_COUNT];
	private Parameters[]		parms = new Parameters[PARM_COUNT];
	private JTable[]			tables = new JTable[PARM_COUNT];
	private TubeDescriptor		desc = null;
	
	TubesPreview(final Localizer localizer) {
		super(new BorderLayout(5, 5));
		this.localizer = localizer;

		final JPanel		pictures = new JPanel(new GridLayout(2, 1, 5, 5));
		final JPanel		center = new JPanel(new BorderLayout(5, 5));

		for(int index = 0; index < PARM_COUNT; index++) {
			parms[index] = new Parameters();
			tables[index] = new JTable(parms[index]);
			scrolls[index] = new JScrollPane(tables[index]);
		}
		add(abbr, BorderLayout.NORTH);
		pictures.add(scheme);
		pictures.add(panel);
		add(pictures, BorderLayout.WEST);
		description.setEditable(false);
		description.setOpaque(false);
		center.add(description, BorderLayout.NORTH);
		center.add(tabArea, BorderLayout.CENTER);
		add(center, BorderLayout.CENTER);
		fillLocalizedStrings();
	}
	
	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		for(int index = 0; index < parms.length; index++) {
			parms[index].fireTableStructureChanged();
		}
		fillLocalizedStrings();
	}

	public void refreshDesc(final TubeDescriptor desc) {
		this.desc = desc;
		if (desc != null) {
			fillContent();
		}
	}
	
	private void fillContent() {
		tabArea.removeAll();
		for(int index = 0; index < tables.length; index++) {
			if (index < desc.getType().getNumberOfLampTypes()) {
				tabArea.add(scrolls[index]);
			}
		}
		scheme.setIcon(desc.getScheme());
		try {
			panel.setIcon(new ImageIcon(URI.create(TubePanelGroup.class.getField(desc.getPanelType().getGroup().name()).getAnnotation(LocaleResource.class).icon()).toURL()));
		} catch (NoSuchFieldException | SecurityException | MalformedURLException e) {
			panel.setIcon(null);
		}
		fillLocalizedStrings();
	}

	private void fillLocalizedStrings() {
		if (desc != null) {
			abbr.setText(desc.getAbbr()+" - "+desc.getType());
			description.setText(desc.getDescription());
		}
	}

	private class Parameters extends DefaultTableModel {
		private static final long serialVersionUID = 5633892490065085578L;
		
		private final CopyOnWriteArrayList<NamedValue<Float>>	content = new CopyOnWriteArrayList<>();

		@Override
		public int getRowCount() {
			return content == null ? 0 : content.size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(final int columnIndex) {
			switch (columnIndex) {
				case 0 :
					return localizer.getValue(COL_NAME);
				case 1 :
					return localizer.getValue(COL_VALUE);
				default :
					return null;
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
				case 0 :
					return String.class;
				case 1 :
					return Float.class;
				default :
					return null;
			}
		}

		@Override
		public boolean isCellEditable(final int rowIndex, final int columnIndex) {
			return false;
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex) {
			switch (columnIndex) {
				case 0 :
					return content.get(rowIndex).getName();
				case 1 :
					return content.get(rowIndex).getValue();
				default :
					return null;
			}
		}

		public void setContent(final Collection<NamedValue<Float>> values) {
			if (values == null) {
				throw new NullPointerException("Values to add can't be null");
			}
			else {
				content.clear();
				content.addAll(values);
				fireTableDataChanged();
			}
		}
			
	}
}
