package chav1961.calc.references.tubes;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SpringLayout;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import chav1961.calc.references.interfaces.TubeDescriptor;
import chav1961.calc.references.interfaces.TubePanelGroup;
import chav1961.calc.references.interfaces.TubeParameter;
import chav1961.calc.references.interfaces.TubesType;
import chav1961.purelib.basic.NamedValue;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.model.FieldFormat;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.useful.JLocalizedOptionPane;

public class FilterForm extends JPanel {
	private static final long 	serialVersionUID = 2249124198926610489L;
	private static final String	COL_NAME = "chav1961.calc.reference.tubesReference.preview.table.name"; 
	private static final String	COL_VALUE = "chav1961.calc.reference.tubesReference.preview.table.value"; 
	private static final String	LABEL_ABBR_NAME = "chav1961.calc.reference.tubesReference.filterForm.abbr.name"; 
	private static final String	LABEL_DESCR_NAME = "chav1961.calc.reference.tubesReference.filterForm.descr.name"; 
	private static final String	PINOUT_TITLE = "chav1961.calc.reference.tubesReference.filterForm.pinout.title"; 

	private final Localizer				localizer;
	private final ParmTableModel		model;
	private final JLabel				abbrLabel = new JLabel();
	private final JTextField			abbrValue = new JTextField();
	private final JLabel				descLabel = new JLabel();
	private final JTextField			descValue = new JTextField();
	private final JList<TubesType>		types = new JList<>();
	private final JList<TubePanelGroup>	panels = new JList<>();
	private final JButton				pinouts = new JButton("...");
	
	public FilterForm(final Localizer localizer, final NamedValue<float[]>... parameters) {
		super(new BorderLayout(5, 5));
		this.localizer = localizer;
		
		this.model = new ParmTableModel(localizer, parameters);
		final JTable	parms = new JTable(this.model);

		final DefaultListModel<TubesType>		typesModel = new DefaultListModel<TubesType>();
		typesModel.addAll(Arrays.asList(TubesType.values()));
		types.setModel(typesModel);
		types.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		types.setCellRenderer(SwingUtils.getCellRenderer(TubesType.class, new FieldFormat(TubesType.class), ListCellRenderer.class));
		
		final DefaultListModel<TubePanelGroup>	panelsModel = new DefaultListModel<TubePanelGroup>();

		panelsModel.addAll(Arrays.asList(TubePanelGroup.values()));
		panels.setModel(panelsModel);
		panels.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		panels.setCellRenderer(SwingUtils.getCellRenderer(TubePanelGroup.class, new FieldFormat(TubePanelGroup.class), ListCellRenderer.class));
		final JPanel	left = new JPanel(new GridLayout(2, 1, 10, 10));

		left.add(new JScrollPane(types));
		left.add(new JScrollPane(panels));

		add(left, BorderLayout.WEST);
		
		final SpringLayout	sl = new SpringLayout();
		final JPanel		center = new JPanel(sl);
		final JScrollPane	pane = new JScrollPane(parms);
		
		center.add(abbrLabel);
		center.add(abbrValue);
		center.add(descLabel);
		center.add(descValue);
		center.add(pinouts);
		center.add(pane);
		sl.putConstraint(SpringLayout.NORTH, pinouts, 15, SpringLayout.NORTH, center);
		sl.putConstraint(SpringLayout.EAST, pinouts, -4, SpringLayout.EAST, center);
		
		sl.putConstraint(SpringLayout.NORTH, abbrValue, 5, SpringLayout.NORTH, center);
		sl.putConstraint(SpringLayout.NORTH, abbrLabel, 5, SpringLayout.NORTH, center);
		sl.putConstraint(SpringLayout.EAST, abbrValue, -10, SpringLayout.WEST, pinouts);
		sl.putConstraint(SpringLayout.WEST, abbrLabel, 0, SpringLayout.WEST, center);

		sl.putConstraint(SpringLayout.NORTH, descValue, 5, SpringLayout.SOUTH, abbrValue);
		sl.putConstraint(SpringLayout.NORTH, descLabel, 5, SpringLayout.SOUTH, abbrValue);
		sl.putConstraint(SpringLayout.EAST, descValue, 0, SpringLayout.EAST, abbrValue);
		sl.putConstraint(SpringLayout.WEST, descLabel, 0, SpringLayout.WEST, center);
		
		sl.putConstraint(SpringLayout.WEST, descValue, 10, SpringLayout.EAST, descLabel);
		sl.putConstraint(SpringLayout.WEST, abbrValue, 0, SpringLayout.WEST, descValue);

		sl.putConstraint(SpringLayout.NORTH, pane, 10, SpringLayout.SOUTH, descValue);
		sl.putConstraint(SpringLayout.WEST, pane, 10, SpringLayout.WEST, center);
		sl.putConstraint(SpringLayout.EAST, pane, -10, SpringLayout.EAST, center);
		sl.putConstraint(SpringLayout.SOUTH, pane, -10, SpringLayout.SOUTH, center);
		
		add(center, BorderLayout.CENTER);
	
		pinouts.addActionListener((e)->showPinout());
		fillLocalizedStrings();
	}

	public RowFilter<TubesModel, Integer> getFilter() {
		final List<Predicate<?>>	tests = new ArrayList<>();
		final Set<TubesType>		types2Check = new HashSet<>();
		final Set<TubePanelGroup>	panels2Check = new HashSet<>();
		
		for(TubesType value : types.getSelectedValuesList()) {
			types2Check.add(value);
		}
		if (types2Check.isEmpty()) {
			types2Check.addAll(Arrays.asList(TubesType.values()));
		}
		
		for(TubePanelGroup value : panels.getSelectedValuesList()) {
			panels2Check.add(value);
		}
		if (panels2Check.isEmpty()) {
			panels2Check.addAll(Arrays.asList(TubePanelGroup.values()));
		}
		
		final List<Predicate<String>>	testAbbr = new ArrayList<>();
		
		if (!Utils.checkEmptyOrNullString(abbrValue.getText())) {
			for(String item : abbrValue.getText().trim().split(",")) {
				final String	trimmed = item.trim();
				
				if (!trimmed.isEmpty()) {
					if (trimmed.contains("*") || trimmed.contains("?")) {
						final String	regex = Utils.fileMask2Regex(trimmed);
						
						testAbbr.add((s)->Pattern.matches(regex, s));
					}
					else {
						testAbbr.add((s)->s.toUpperCase().equalsIgnoreCase(trimmed));
					}
				}
			}
		}
		else {
			testAbbr.add((s)->true);
		}
		final Predicate<String>[]	abbrs = testAbbr.toArray(new Predicate[testAbbr.size()]);
		
		if (!Utils.checkEmptyOrNullString(descValue.getText())) {
			
		}

		final List<Predicate<TubeDescriptor>>	testParm = new ArrayList<>();
		
		for(int index = 0; index < model.getRowCount(); index++) {
			if (!Utils.checkEmptyOrNullString(model.values[index])) {
				testParm.add(buildParamTest(model.names[index], model.values[index]));
			}
		}
		final Predicate<TubeDescriptor>[]	parms = testParm.toArray(new Predicate[testParm.size()]);
		
		return new RowFilter<TubesModel, Integer>() {
			@Override
			public boolean include(final Entry<? extends TubesModel, ? extends Integer> entry) {
				final TubeDescriptor	desc = entry.getModel().getDescriptor(entry.getIdentifier());
				
				return types2Check.contains(desc.getType()) &&
						hasAbbr(desc.getAbbr(), abbrs) &&
						panels2Check.contains(desc.getPanelType().getGroup());
			}
		};
	}
	
	private Predicate<TubeDescriptor> buildParamTest(NamedValue<float[]> namedValue, String string) {
		// TODO Auto-generated method stub
		return null;
	}

	private void fillLocalizedStrings() {
		abbrLabel.setText(localizer.getValue(LABEL_ABBR_NAME));
		descLabel.setText(localizer.getValue(LABEL_DESCR_NAME));
	}

	private static boolean hasAbbr(final String abbr, final Predicate<String>... test) {
		for(Predicate<String> item : test) {
			if (item.test(abbr)) {
				return true;
			}
		}
		return false;
	}

	private static boolean hasParm(final String abbr, final Predicate<String>... test) {
		for(Predicate<String> item : test) {
			if (item.test(abbr)) {
				return true;
			}
		}
		return false;
	}
	
	private static float[] extractParameter(final TubeDescriptor desc, final int lampNo, final TubeParameter parameter) {
		final TubeParameter[]	parms = desc.getParameters(lampNo);
		
		for(int index = 0; index < parms.length; index++) {
			if (parameter == parms[index]) {
				return desc.getValues(index);
			}
		}
		return null;
	}
	
	private static boolean inList(final float[] values, final float[][] list) {
		for(float item : values) {
			for(float[] range : list) {
				if (item >= range[0] && item <= range[1]) {
					return true;
				}
			}
		}
		return false;
	}

	private void showPinout() {
		try {
			final ConnScreen cs = new ConnScreen(localizer);
			
			if (new JLocalizedOptionPane(localizer).confirm(this, cs, COL_NAME, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
				
			}
		} catch (ContentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	private static class ParmTableModel extends DefaultTableModel {
		private final Localizer				localizer;
		private final NamedValue<float[]>[]	names;
		private final String[]				values;
		
		public ParmTableModel(final Localizer localizer, final NamedValue<float[]>... names) {
			this.localizer = localizer;
			this.names = names.clone();
			this.values = new String[names.length];
			
			Arrays.fill(this.values, "");
		}

		@Override
		public int getRowCount() {
			return names == null ? 0 : names.length;
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
		public Class<?> getColumnClass(final int columnIndex) {
			return String.class;
		}

		@Override
		public boolean isCellEditable(final int rowIndex, final int columnIndex) {
			return columnIndex == 1;
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex) {
			switch (columnIndex) {
				case 0 :
					return localizer.getValue(names[rowIndex].getName());
				case 1 :
					return values[rowIndex];
				default :
					return null;
			}
		}

		@Override
		public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
			values[rowIndex] = aValue.toString();
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}
}
