package chav1961.calc;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class ComboBoxItem extends JFrame implements ActionListener {

    public ComboBoxItem() {
        final Vector model = new Vector();
        model.addElement(new Item(1, "car"));
        model.addElement(new Item(2, "plane"));
        model.addElement(new Item(3, "train"));
        model.addElement(new Item(4, "boat"));

        JComboBox comboBox;

        comboBox = new JComboBox(model);
        comboBox.addActionListener(this);
        this.getContentPane().add(comboBox, BorderLayout.NORTH);

        comboBox = new JComboBox(model);

        // I want the comboBox editable.
        comboBox.setEditable(true);

        comboBox.setRenderer(new ItemRenderer());
        comboBox.setEditor(new MyComboBoxEditor()); ///<------- Quick'n'Dirty editor added
        comboBox.addActionListener(this);
        this.getContentPane().add(comboBox, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final JComboBox comboBox = (JComboBox) e.getSource();
        final Item item = (Item) comboBox.getSelectedItem();
        System.out.println(item.getId() + " : " + item.getDescription());
    }

    class MyComboBoxEditor implements ComboBoxEditor {

        JTextField editor;
        Item       editedItem;

        @Override
        public void addActionListener(final ActionListener l) {

        }

        @Override
        public Component getEditorComponent() {
            if (this.editor == null) {
                this.editor = new JTextField();
            }
            return this.editor;
        }

        @Override
        public Object getItem() {
            return this.editedItem;
        }

        @Override
        public void removeActionListener(final ActionListener l) {
            // TODO Auto-generated method stub

        }

        @Override
        public void selectAll() {
            // TODO Auto-generated method stub

        }

        @Override
        public void setItem(final Object anObject) {
            this.editedItem = (Item) anObject;
            this.editor.setText(String.valueOf(this.editedItem.getId()));
        }

    }

    class ItemRenderer extends BasicComboBoxRenderer {

        @Override
        public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected,
                final boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value != null) {
                final Item item = (Item) value;
                this.setText(item.getDescription().toUpperCase());
            }

            if (index == -1) {
                final Item item = (Item) value;
                this.setText("" + item.getId());
            }

            return this;
        }
    }

    class Item {

        private final int    id;
        private final String description;

        public Item(final int id, final String description) {
            this.id = id;
            this.description = description;
        }

        public int getId() {
            return this.id;
        }

        public String getDescription() {
            return this.description;
        }

        @Override
        public String toString() {
            return this.description;
        }
    }

    public static void main(final String[] args) {
        final JFrame frame = new ComboBoxItem();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}