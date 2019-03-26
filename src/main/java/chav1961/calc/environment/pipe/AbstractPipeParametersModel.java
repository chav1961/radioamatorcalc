package chav1961.calc.environment.pipe;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import chav1961.calc.environment.PipeParameterWrapper;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.ui.interfaces.FormModel;

public abstract class AbstractPipeParametersModel implements FormModel<Integer,PipeParameterWrapper>, Closeable {
	protected final List<PipeParameterWrapperAndKey>		content = new ArrayList<>();
	
	private final List<List<PipeParameterWrapperAndKey>>	history = new ArrayList<>();
	private String	filter = null, ordering = null;
	private int		uniqueId, currentIndex;
	
	public AbstractPipeParametersModel(final List<PipeParameterWrapper> content) throws ContentException {
		if (content == null) {
			throw new NullPointerException("Content can't be null"); 
		}
		else {
			for (int index = 0, maxIndex = content.size(); index < maxIndex; index++) {
				this.content.add(new PipeParameterWrapperAndKey(content.get(index),index));
			}
			uniqueId = content.size();
			currentIndex = -1;
			refresh();
		}
	}

	public List<PipeParameterWrapper> getCurrentValue() {
		final List<PipeParameterWrapper>	result = new ArrayList<>();
		
		for (PipeParameterWrapperAndKey item : content) {
			result.add(item.content);
		}
		return result;
	}

	@Override public abstract PipeParameterWrapper createInstance(final Integer id) throws ContentException;
	@Override public abstract PipeParameterWrapper duplicateInstance(final Integer oldId, final Integer newId) throws ContentException ;
	
	@Override
	public void close() throws IOException {
		history.clear();
		content.clear();
	}
	
	@Override 
	public Class<PipeParameterWrapper> getInstanceType() {
		return PipeParameterWrapper.class;		
	}
	
	@Override
	public Integer createUniqueId() throws ContentException {
		return uniqueId++;
	}

	@Override
	public PipeParameterWrapper getInstance(final Integer Id) throws ContentException {
		if (Id == null) {
			throw new NullPointerException("Id to update can't be null"); 
		}
		else {
			final int	index = getIndexById(Id);
			
			if (index >= 0) {
				return content.get(index).content;
			}
			else {
				throw new ContentException("Attempt to get non-existent key ["+Id+"]");
			}
		}
	}

	@Override
	public PipeParameterWrapper updateInstance(final Integer Id, final PipeParameterWrapper inst) throws ContentException {
		if (Id == null) {
			throw new NullPointerException("Id to update can't be null"); 
		}
		else if (inst == null) {
			throw new NullPointerException("Instance to update can't be null"); 
		}
		else {
			for (PipeParameterWrapperAndKey item : content) {
				if (item.key == Id.intValue()) {
					final PipeParameterWrapper	oldValue = item.content; 
	
					toHistory();
					item.content = inst;
					refresh();
					return oldValue;
				}
			}
			throw new ContentException("Attempt to update instance failed: unknown key ["+Id+"]");
		}
	}

	@Override
	public PipeParameterWrapper removeInstance(final Integer Id) throws ContentException {
		if (Id == null) {
			throw new NullPointerException("Id to update can't be null"); 
		}
		else {
			final int	index = getIndexById(Id);
			
			if (index >= 0) {
				toHistory();
				
				final PipeParameterWrapper	desc = content.remove(index).content;
				
				if (index >= size()) {
					if (size() > 0) {
						setCurrentIndex(index-1);
					}
					else {
						currentIndex = -1;
					}
				}
				return desc;
			}
			else {
				throw new ContentException("Attempt to remove non-existent key ["+Id+"]");
			}
		}
	}

	@Override
	public void refresh() throws ContentException {
		if (filter != null || ordering != null) {
			content.sort(new Comparator<PipeParameterWrapperAndKey>(){
				@Override
				public int compare(final PipeParameterWrapperAndKey o1, final PipeParameterWrapperAndKey o2) {
					// TODO Auto-generated method stub
					return o1.key - o2.key;
				}
			});
		}
	}

	@Override
	public void undo() throws ContentException {
		if (history.size() > 0) {
			fromHistory();
			refresh();
		}
	}

	@Override
	public int size() {
		return content.size();
	}

	@Override
	public int getCurrentIndex() {
		return currentIndex;
	}

	@Override
	public void setCurrentIndex(final int index) {
		if (index < 0 || index >= content.size()) {
			throw new IllegalArgumentException(content.isEmpty() ? "Attempt to set index in empty content" : "Index ["+index+"] out of range 0.."+(content.size()-1));
		}
		else {
			currentIndex = index;
		}
	}

	@Override
	public int getIndexById(final Integer Id) {
		if (Id == null) {
			throw new NullPointerException("Id to update can't be null"); 
		}
		else {
			for (int index = 0, maxIndex = content.size(); index < maxIndex; index++) {
				if (content.get(index).key == Id.intValue()) {
					return index;
				}
			}
			return -1;
		}
	}

	@Override
	public Integer getCurrentId() {
		return getIdByIndex(getCurrentIndex());
	}

	@Override
	public Integer getIdByIndex(final int index) {
		if (index < 0 || index >= content.size()) {
			throw new IllegalArgumentException(content.isEmpty() ? "Attempt to get Id from empty content" : "Index ["+index+"] out of range 0.."+(content.size()-1));
		}
		else {
			return content.get(index).key;
		}
	}

	@Override
	public Iterable<Integer> contentIds() throws ContentException {
		int		amount = 0;
		
		for (PipeParameterWrapperAndKey item : content) {
			if (item.content != null) {
				amount++;
			}
		}
		
		final Integer[]	result = new Integer[amount];
		
		amount = 0;
		for (PipeParameterWrapperAndKey item : content) {
			if (item.content != null) {
				result[amount++] = item.key;
			}
		}
		return new Iterable<Integer>(){
			@Override
			public Iterator<Integer> iterator() {
				return new Iterator<Integer>(){
					int	index = 0;
					
					@Override
					public boolean hasNext() {
						return index < result.length;
					}

					@Override
					public Integer next() {
						return result[index++];
					}
				};
			}
		};
	}

	@Override
	public void setFilterAndOrdering(final String filter, final String ordering) throws ContentException {
		this.filter = filter;
		this.ordering = ordering;
		refresh();
	}

	private void toHistory() {
		final List<PipeParameterWrapperAndKey>	temp = new ArrayList<>();
		
		temp.addAll(content);
		history.add(0,temp);
	}

	private void fromHistory() throws ContentException {
		content.clear();
		content.addAll(history.remove(0));
	}
	
	protected static class PipeParameterWrapperAndKey {
		PipeParameterWrapper	content;
		int						key;
		
		PipeParameterWrapperAndKey(final PipeParameterWrapper content, final int key) {
			this.content = content;
			this.key = key;
		}
	}
}
