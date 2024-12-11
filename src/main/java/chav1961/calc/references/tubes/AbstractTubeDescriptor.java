package chav1961.calc.references.tubes;

import javax.swing.Icon;

import org.w3c.dom.Element;

import chav1961.calc.references.interfaces.TubeCorpusType;
import chav1961.calc.references.interfaces.TubeDescriptor;
import chav1961.calc.references.interfaces.TubesType;

public class AbstractTubeDescriptor implements TubeDescriptor {
	public AbstractTubeDescriptor(final Element root)  {
		
	}
	
	@Override
	public TubesType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Icon getScheme() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TubeCorpusType getCorpus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TubeParameter[] getParameters() {
		return getParameters(0);
	}

	@Override
	public float[] getValues() {
		return getValues(0);
	}

	@Override
	public TubeParameter[] getParameters(int lampNo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float[] getValues(int lampNo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graphics[] getGraphics() {
		return getGraphics(0);
	}

	@Override
	public Graphics[] getGraphics(int lampNo) {
		// TODO Auto-generated method stub
		return null;
	}
}
