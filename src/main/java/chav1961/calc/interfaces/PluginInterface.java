package chav1961.calc.interfaces;

import java.net.URI;

import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public interface PluginInterface {
	public interface CalculationModeDescriptor {
		String getName();
		String getDescription();
		URI[] getInputParameters();
		URI[] getOutputParameters();
		String getFormula();
	}	
	
	String getPluginName();
	URI getPluginNavigatorPath();
	ContentNodeMetadata getPluginModel();
}
