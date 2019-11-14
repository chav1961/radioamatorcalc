package chav1961.calc.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import chav1961.calc.utils.MutableLocalizer.LocalKeyValue;
import chav1961.purelib.i18n.AbstractLocalizer;

public class JLocalKeyValueEditor extends JComboBox<LocalKeyValue> {
	private static final long serialVersionUID = -1530215458568897534L;
	
	public JLocalKeyValueEditor() {
		setEditable(true);
		setRenderer((list, value, index, isSelected, cellHasFocus) -> {
			final LocalKeyValue	currentVal = ((LocalKeyValue)value);
			final JLabel		result = new JLabel(currentVal.getValue(),SwingConstants.LEFT);
			
			AbstractLocalizer.enumerateLocales((lang, langName, icon)->{
				if (currentVal.getLocaleAssociated().getLanguage().equals(langName)) {
					result.setIcon(icon);
				}	
			});
			result.setOpaque(false);
			result.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
			result.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
			
			if (cellHasFocus) {
				result.setBorder(new LineBorder(Color.GREEN));
			}
			return result;
		});
		setEditor(new ComboBoxEditorImpl());
	}

    private static class ComboBoxEditorImpl implements ComboBoxEditor {
        private final JTextField	contentEditor = new JTextField(20);
        private LocalKeyValue		lastValue = null;

		@Override
		public Component getEditorComponent() {
			return contentEditor;
		}

		@Override
		public void setItem(final Object anObject) {
			if (anObject instanceof LocalKeyValue) {
				lastValue = (LocalKeyValue)anObject;
				contentEditor.setText(lastValue.getValue());
			}
			else {
				contentEditor.setText("");
			}
		}

		@Override
		public Object getItem() {
			if (lastValue != null) {
				lastValue.setValue(contentEditor.getText());
				return lastValue;
			}
			else {
				return null;
			}
		}

		@Override
		public void selectAll() {
			if (lastValue != null) {
				contentEditor.selectAll();
			}
		}

		@Override
		public void addActionListener(final ActionListener l) {
			contentEditor.addActionListener(l);
		}

		@Override
		public void removeActionListener(final ActionListener l) {
			contentEditor.removeActionListener(l);
		}
    }
}
