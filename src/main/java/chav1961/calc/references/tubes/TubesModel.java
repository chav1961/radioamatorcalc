package chav1961.calc.references.tubes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.table.DefaultTableModel;

import chav1961.calc.references.interfaces.TubeDescriptor;
import chav1961.calc.references.interfaces.TubePanelGroup;
import chav1961.calc.references.interfaces.TubeParameter;
import chav1961.calc.references.interfaces.TubesType;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.i18n.interfaces.Localizer;

class TubesModel extends DefaultTableModel {
	private static final long serialVersionUID = -4800780353810497969L;
	
	private final Localizer			localizer;
	private final TubeDescriptor[]	content;
	private final TubesType			type;
	private final TubeParameter[][]	parms;
	private final TubeParameter[]	joinedParms;
	
	TubesModel(final Localizer localizer, final TubeDescriptor... content) {
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null"); 
		}
		else if (content == null || Utils.checkArrayContent4Nulls(content) >= 0) {
			throw new IllegalArgumentException("Content is null or contains nulls inside");
		}
		else {
			this.localizer = localizer;
			this.type = null;
			this.content = content.clone();
			this.parms = new TubeParameter[TubesType.getMaximumNumberOfLampTypes()][];  
					
			for(int index = 0; index < this.parms.length; index++) {
				this.parms[index] = extractParameters(this.content, index);
			}
			this.joinedParms = joinParameters(this.parms);
		}
	}
	
	public TubesModel(final Localizer localizer, final TubesType type, final TubeDescriptor... content) {
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null"); 
		}
		else if (type == null) {
			throw new NullPointerException("Content type can't be null");
		}
		else if (content == null || Utils.checkArrayContent4Nulls(content) >= 0) {
			throw new IllegalArgumentException("Content is null or contaons nulls inside");
		}
		else {
			this.localizer = localizer;
			this.type = type;
			int	count = 0;
			
			for(TubeDescriptor item : content) {
				if (item.getType() == type) {
					count++;
				}
			}
			this.content = new TubeDescriptor[count];
			count = 0;
			for(TubeDescriptor item : content) {
				if (item.getType() == type) {
					this.content[count++] = item;
				}
			}
			this.parms = new TubeParameter[TubesType.getMaximumNumberOfLampTypes()][];  
			
			for(int index = 0; index < this.parms.length; index++) {
				this.parms[index] = extractParameters(this.content, index);
			}
			this.joinedParms = joinParameters(this.parms);
		}
	}
	

	@Override
	public int getRowCount() {
		return content == null ? 0 : content.length;
	}

	@Override
	public int getColumnCount() {
		return type == null ? 0 : joinedParms.length + 2;
	}

	@Override
	public String getColumnName(final int columnIndex) {
		switch (columnIndex) {
			case 0 	:
				return localizer.getValue("");
			case 1 	:
				return localizer.getValue("");
			default :
				return localizer.getValue(joinedParms[columnIndex-2].name());
		}
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		switch (columnIndex) {
			case 0 	:
				return Icon.class;
			case 1 	:
				return TubePanelGroup.class;
			default :
				return float[].class;
		}
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0 	:
				return content[rowIndex].getScheme();
			case 1 	:
				return content[rowIndex].getPanelType().getGroup();
			default :
				final TubeDescriptor	desc  = content[rowIndex];
				final float[]			temp = new float[desc.getType().getNumberOfLampTypes()];
				
				for(int index = 0; index < temp.length; index++) {
					final TubeParameter	p = joinedParms[columnIndex-2];
					
					temp[index] = Float.NaN;
					for(int parmIndex = 0; parmIndex < parms[index].length; parmIndex++) {
						if (parms[index][parmIndex] == p) {
							temp[index] = desc.getValues(index)[parmIndex];
							break;
						}
					}
				}
				return temp;
		}
	}

	private TubeParameter[] extractParameters(final TubeDescriptor[] content, final int lampNo) {
		final Set<TubeParameter>	candidates = new HashSet<>();
		
		for(TubeDescriptor item : content) {
			candidates.addAll(Arrays.asList(item.getParameters(lampNo)));
		}
		final TubeParameter[]	result = candidates.toArray(new TubeParameter[candidates.size()]);
		
		Arrays.sort(result, (i1,i2)->i1.getOrderIndex() - i2.getOrderIndex());
		return result;
	}

	private TubeParameter[] joinParameters(final TubeParameter[]... parms) {
		final Set<TubeParameter>	candidates = new HashSet<>();
		
		for(TubeParameter[] item : parms) {
			candidates.addAll(Arrays.asList(item));
		}
		final TubeParameter[]	result = candidates.toArray(new TubeParameter[candidates.size()]);
		
		Arrays.sort(result, (i1,i2)->i1.getOrderIndex() - i2.getOrderIndex());
		return result;
	}

	public TubeDescriptor getDescriptor(final int rowIndex) {
		return content[rowIndex];
	}
}
