package chav1961.calc.references.tubes;

import java.awt.BorderLayout;
import java.util.Locale;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import chav1961.calc.references.interfaces.TubeDescriptor;
import chav1961.calc.references.interfaces.TubesType;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;

class TubesTab extends JPanel implements LocaleChangeListener {
	private static final long 		serialVersionUID = 8720981840520863203L;

	private final TubesType			type;
	private final Consumer<TubeDescriptor>	selection;
	private final TubesModel		model;
	private final JScrollPane		scroll;
	private final JTable			table; 
	
	TubesTab(final Localizer localizer, final TubesType type, final Consumer<TubeDescriptor> selection, final TubeDescriptor... content) {
		super(new BorderLayout(5, 5));
		this.type = type;
		this.selection = selection;
		this.model = type == null ? new TubesModel(localizer, content) : new TubesModel(localizer, type, content);
		this.table = new JTable(model);
		this.table.setCellSelectionEnabled(true);
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.table.getSelectionModel().addListSelectionListener((e)->fireSelection());
		this.scroll = new JScrollPane(table);
		
		add(scroll, BorderLayout.CENTER);
	}
	
	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		model.fireTableStructureChanged();
	}

	public TubesType getType() {
		return type;
	}
	
	public TubeDescriptor getSelection() {
		final int 	row = table.getSelectedRow();
		
		if (row < 0) {
			return null;
		}
		else {
			return model.getDescriptor(row);
		}
	}
	
	private void fireSelection() {
		selection.accept(getSelection());
	}
}
