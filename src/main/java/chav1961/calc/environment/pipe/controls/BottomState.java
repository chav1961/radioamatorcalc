package chav1961.calc.environment.pipe.controls;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import chav1961.calc.LocalizationKeys;
import chav1961.calc.environment.Constants;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;

public class BottomState extends JPanel implements LocaleChangeListener {
	private static final long 	serialVersionUID = -1257813031100858706L;
	
	private final Localizer 	localizer; 
	private final JLabel		nameLabel = new JLabel();
	private final JTextField	nameField = new JTextField();

	public BottomState(final Localizer localizer, final Icon left, final Icon right) throws LocalizationException, IllegalArgumentException {
		if (left == null) {
			throw new NullPointerException("Left icon can't be null"); 
		}
		else if (right == null) {
			throw new NullPointerException("Right icon can't be null"); 
		}
		else {
			this.localizer = localizer;
			prepareContent(left,Constants.LEFT_ADVANCED,right,null);
		    fillLocalizationStrings();
		}
	}

	public BottomState(final Localizer localizer, final Icon left, final Icon right1, final Icon right2) throws LocalizationException, IllegalArgumentException {
		if (left == null) {
			throw new NullPointerException("Left icon can't be null"); 
		}
		else if (right1 == null) {
			throw new NullPointerException("Right icon [1] can't be null"); 
		}
		else if (right2 == null) {
			throw new NullPointerException("Right icon [2] can't be null"); 
		}
		else {
			this.localizer = localizer;
			prepareContent(left,Constants.LEFT_ADVANCED,right1,right2);
		    fillLocalizationStrings();
		}
	}

	@Override
	public void localeChanged(Locale oldLocale, Locale newLocale) throws LocalizationException {
	    fillLocalizationStrings();
	}
	
	private void prepareContent(final Icon left1, final Icon left2, final Icon right1, final Icon right2) {
	    final JPanel	statePanel = new JPanel(new BorderLayout()); 
	    final JPanel	innerStatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    
		setLayout(new BorderLayout());
		
	    innerStatePanel.add(nameLabel);
	    innerStatePanel.add(nameField);
	    innerStatePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
	    innerStatePanel.setBackground(Color.GRAY);
	    
	    final JPanel	tempLeft = new JPanel(new GridLayout(1,2)), tempRight = new JPanel(new GridLayout(1,right2 == null ? 1 : 2));
	    
	    tempLeft.add(new JLabel(left1));
	    tempLeft.add(new JLabel(left2));
	    tempRight.add(new JLabel(right1));
	    if (right2 != null) {
		    tempRight.add(new JLabel(left2));
	    }
	    statePanel.add(tempLeft,BorderLayout.WEST);
	    statePanel.add(innerStatePanel,BorderLayout.CENTER);
	    statePanel.add(tempRight,BorderLayout.EAST);
		
	    nameField.setColumns(20);
	    nameField.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent e) {
			}

			@Override
			public void focusLost(FocusEvent e) {
				setName(nameField.getText());
			}
		});
	    setName(nameField.getText());
	}

	
	private void fillLocalizationStrings() throws LocalizationException, IllegalArgumentException {
		nameLabel.setText(localizer.getValue(LocalizationKeys.DESKTOP_STATE_NAME_LABEL));
	}
}
