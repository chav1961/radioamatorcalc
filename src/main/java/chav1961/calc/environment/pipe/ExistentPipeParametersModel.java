package chav1961.calc.environment.pipe;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import chav1961.purelib.basic.exceptions.ContentException;

public class ExistentPipeParametersModel extends AbstractPipeParametersModel {
	private final CreateInstance4PipeParameterModeInterface	toCreate;
	private static final Set<SupportedOperations>	OPERATIONS;
	
	static {
		final Set<SupportedOperations>	temp = new HashSet<>();
		
		temp.add(SupportedOperations.INSERT);
		temp.add(SupportedOperations.UPDATE);
		temp.add(SupportedOperations.DELETE);
		OPERATIONS = Collections.unmodifiableSet(temp);
	}
	
	public ExistentPipeParametersModel(final List<PipeParameterWrapper> content, final CreateInstance4PipeParameterModeInterface toCreate) throws ContentException {
		super(content);
		if (toCreate == null) {
			throw new NullPointerException("Interface to create can't be null");
		}
		else {
			this.toCreate = toCreate;
		}
	}

	@Override
	public Set<SupportedOperations> getOperationsSupported() {
		return OPERATIONS;
	}
	
	@Override
	public PipeParameterWrapper createInstance(final Integer id) throws ContentException {
		if (id == null) {
			throw new NullPointerException("Old id can't be null"); 
		}
		else {
			final int	index = getIndexById(id);
			
			if (index < 0) {
				return toCreate.newInstance(id.intValue());
			}
			else {
				throw new IllegalArgumentException("Attempt to create instance failed: dupliacate key ["+id+"] in the content");
			}
		}
	}

	@Override
	public PipeParameterWrapper duplicateInstance(final Integer oldId, final Integer newId) throws ContentException {
		throw new IllegalStateException("This operation is not used with the given model");
	}
}
