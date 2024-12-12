package chav1961.calc.references.interfaces;


import javax.swing.Icon;

public interface TubeDescriptor {
	TubesType getType();
	String getAbbr();
	String getDescription();
	Icon getScheme();
	TubePanelType getPanelType();
	TubeConnector[] getConnectors();
	TubeCorpusType getCorpus();
	TubeParameter[] getParameters();
	float[] getValues();
	TubeParameter[] getParameters(int lampNo);
	float[] getValues(int lampNo);
	Graphic[] getGraphics();
	Graphic[] getGraphics(int lampNo);
}
