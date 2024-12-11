package chav1961.calc.references.interfaces;


import javax.swing.Icon;

public interface TubeDescriptor {
	public static enum TubeParameter {
		
	}
	
	public interface Graphics {
		String getTitle();
		String getTooptip();
		Icon getPicture();
	}
	TubesType getType();	
	String getDescription();
	Icon getScheme();
	TubeCorpusType getCorpus();
	TubeParameter[] getParameters();
	float[] getValues();
	TubeParameter[] getParameters(int lampNo);
	float[] getValues(int lampNo);
	Graphics[] getGraphics();
	Graphics[] getGraphics(int lampNo);
}
