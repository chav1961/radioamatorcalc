package chav1961.calc.references.tubes;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Locale;
import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

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
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.table.setRowHeight(32);
		this.table.setDefaultRenderer(Icon.class, new DefaultTableCellRenderer() {
				private static final long serialVersionUID = 1L;

				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					return new JLabel((Icon)value);
				}
		});
		this.table.setDefaultRenderer(float[].class, new DefaultTableCellRenderer() {
				private static final long serialVersionUID = 1L;
	
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					if (value != null) {
						final StringBuilder	result = new StringBuilder("<html><body>");
						
						for(float item : (float[])value) {
							result.append("<p><b>").append(item).append("</b></p>");
						}
						return new JLabel(result.append("</body></html>").toString(), JLabel.RIGHT);
					}
					else {
						return new JLabel("-", JLabel.CENTER);
					}
				}
		});
		
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
