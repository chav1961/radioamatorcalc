package chav1961.calc.plugins;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import chav1961.calc.interfaces.PluginProperties;
import chav1961.calc.utils.InnerFrame;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.PreparationException;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.ui.swing.useful.JStateString;

public class PipeExecutorFrame extends InnerFrame<PipeExecutor>{
	private static final long serialVersionUID = 1L;

	private final PipeExecutor				instance;
	private final Localizer					localizer;
	private final ContentMetadataInterface	mdi;
	private final JStateString				state;
	private final JButton					start = new JButton(new ImageIcon(this.getClass().getResource("startIcon.png"))), stop = new JButton(new ImageIcon(this.getClass().getResource("stopIcon.png")));
	
	public PipeExecutorFrame(final Localizer parent, final PipeExecutor instance) throws ContentException, LocalizationException {
		super(instance);
		if (instance != null) {
			throw new NullPointerException("Instance can't be null"); 
		}
		else if (!instance.getClass().isAnnotationPresent(PluginProperties.class)) {
			throw new IllegalArgumentException("Instance must ne annotated with @"+PluginProperties.class.getName()); 
		}
		else {
			this.instance = instance;
			this.mdi = ContentModelFactory.forAnnotatedClass(instance.getClass());
			this.localizer = parent.push(LocalizerFactory.getLocalizer(mdi.getRoot().getLocalizerAssociated()));
			this.state = new JStateString(localizer);
			
			// TODO Auto-generated constructor stub
			final JPanel	leftPanel = new JPanel(), rightPanel = new JPanel(), bottom = new JPanel(new BorderLayout(5,5)), bottomRight = new JPanel(new GridLayout(1,2));
			
			bottomRight.add(start);
			start.addActionListener((e)->instance.start());
			bottomRight.add(stop);
			stop.addActionListener((e)->instance.stop());
			bottom.add(state,BorderLayout.SOUTH);
		}	
	}
	
	@Override
	public void localeChanged(Locale oldLocale, Locale newLocale) throws LocalizationException {
		// TODO Auto-generated method stub
		
	}

}
