package chav1961.calc.plugins;

import java.io.InputStream;

import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.interfaces.LoggerFacade;

public class PipeExecutor {
	
	public PipeExecutor() {
		
	}
	
	public void load(final InputStream is) {
		
	}

	public boolean isPipeValid(final LoggerFacade logger) {
		return true;
	}
	
	public void start() {
		// TODO Auto-generated method stub
		if (isPipeValid(PureLibSettings.NULL_LOGGER)) {
			
		}
	}

	public void stop() {
		// TODO Auto-generated method stub
	}
}
