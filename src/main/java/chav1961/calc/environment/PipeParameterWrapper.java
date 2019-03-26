package chav1961.calc.environment;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.ui.interfaces.Format;

/**
 * <p>This class is a wrapper for the plugin parameters</p>
 * @author Alexander Chernomyrdin aka chav1961
 * @since 0.0.1
 */
public class PipeParameterWrapper {
@LocaleResource(value="pipe.pluginId",tooltip="pipe.pluginIdTooltip")	
@Format("10ms")
	private String		pluginId;
@LocaleResource(value="pipe.pluginInstanceName",tooltip="pipe.pluginInstanceNameTooltip")	
@Format("10ms")
	private String		pluginInstanceName;
@LocaleResource(value="pipe.pluginFieldName",tooltip="pipe.pluginFieldNameTooltip")	
@Format("10ms")
	private String		pluginFieldName;
@LocaleResource(value="pipe.pluginFieldtype",tooltip="pipe.pluginFieldtypeTooltip")	
@Format("10ms")
	private Class<?>	pluginFieldType;
	
	/**
	 * <p>Constructor of the class instance</p> 
	 * @param pluginId plugin id for the parameter. Can't be null or empty
	 * @param pluginInstanceName plugin instance name for the parameter. Can't be null or empty
	 * @param pluginFieldName plugin field name for the parameter. Can't be null or empty
	 * @param pluginFieldType plugin field type for the parameter. Can't be null
	 * @throws NullPointerException when plugin field type is null
	 * @throws IllegalArgumentException when any string argument is null or empty string
	 */
	public PipeParameterWrapper(final String pluginId, final String pluginInstanceName, final String pluginFieldName, final Class<?> pluginFieldType) throws IllegalArgumentException, NullPointerException {
		if (pluginId == null || pluginId.isEmpty()) {
			throw new IllegalArgumentException("Plugin id can't be null or empty"); 
		}
		else if (pluginInstanceName == null || pluginInstanceName.isEmpty()) {
			throw new IllegalArgumentException("Plugin instance name can't be null or empty"); 
		}
		else if (pluginFieldName == null || pluginFieldName.isEmpty()) {
			throw new IllegalArgumentException("Plugin field name can't be null or empty"); 
		}
		else if (pluginFieldType == null) {
			throw new NullPointerException("Plugin field type can't be null"); 
		}
		else {
			this.pluginId = pluginId;
			this.pluginInstanceName = pluginInstanceName;
			this.pluginFieldName = pluginFieldName;
			this.pluginFieldType = pluginFieldType;
		}
	}

	/**
	 * <p>Get plugin id</p>
	 * @return plugin id. Can't be null or empty
	 */
	public String getPluginId() {
		return pluginId;
	}

	/**
	 * <p>Set plugin id</p>
	 * @param pluginId plugin id to set. Can't be null or empty
	 * @throws IllegalArgumentException if parameter is null or empty
	 */
	public void setPluginId(final String pluginId) throws IllegalArgumentException {
		if (pluginId == null || pluginId.isEmpty()) {
			throw new IllegalArgumentException("Plugin id can't be null or empty"); 
		}
		else {
			this.pluginId = pluginId;
		}
	}

	/**
	 * <p>Get plugin instance name</p>
	 * @return plugin instance name. Can't be null or empty
	 */
	public String getPluginInstanceName() {
		return pluginInstanceName;
	}

	/**
	 * <p>Set plugin instance name</p>
	 * @param pluginInstanceName plugin instance name to set. Can't be null or empty
	 * @throws IllegalArgumentException if parameter is null or empty
	 */
	public void setPluginInstanceName(final String pluginInstanceName) throws IllegalArgumentException {
		if (pluginInstanceName == null || pluginInstanceName.isEmpty()) {
			throw new IllegalArgumentException("Plugin instance name can't be null or empty"); 
		}
		else {
			this.pluginInstanceName = pluginInstanceName;
		}
	}

	/**
	 * <p>Get plugin field name</p>
	 * @return plugin field name. Can' be null or empty
	 */
	public String getPluginFieldName() {
		return pluginFieldName;
	}

	/**
	 * <p>Set plugin field name</p>
	 * @param pluginFieldName plugin field name to set. Can't be null or empty
	 * @throws IllegalArgumentException if parameter is null or empty
	 */
	public void setPluginFieldName(final String pluginFieldName) throws IllegalArgumentException  {
		if (pluginFieldName == null || pluginFieldName.isEmpty()) {
			throw new IllegalArgumentException("Plugin field name can't be null or empty"); 
		}
		else {
			this.pluginFieldName = pluginFieldName;
		}
	}

	/**
	 * <p>Get plugin finel type.</p>
	 * @return plugin field type. Can't be null
	 */
	public Class<?> getPluginFieldType() {
		return pluginFieldType;
	}

	/**
	 * <p>Set plugin field type.</p>
	 * @param pluginFieldtype Plugin field type. Can't be null
	 * @throws NullPointerException if parameter is null
	 */
	public void setPluginFieldType(final Class<?> pluginFieldType) throws NullPointerException {
		if (pluginFieldType == null) {
			throw new NullPointerException("Plugin field type can't be null"); 
		}
		else {
			this.pluginFieldType = pluginFieldType;
		}
	}

	@Override
	public String toString() {
		return "PipeParameterWrapper [pluginId=" + pluginId + ", pluginInstanceName=" + pluginInstanceName + ", pluginFieldName=" + pluginFieldName + ", pluginFieldtype=" + pluginFieldType + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result + ((pluginFieldName == null) ? 0 : pluginFieldName.hashCode());
		result = prime * result + ((pluginFieldType == null) ? 0 : pluginFieldType.hashCode());
		result = prime * result + ((pluginId == null) ? 0 : pluginId.hashCode());
		result = prime * result + ((pluginInstanceName == null) ? 0 : pluginInstanceName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		PipeParameterWrapper other = (PipeParameterWrapper) obj;
		if (pluginFieldName == null) {
			if (other.pluginFieldName != null) return false;
		} else if (!pluginFieldName.equals(other.pluginFieldName)) return false;
		if (pluginFieldType == null) {
			if (other.pluginFieldType != null) return false;
		} else if (!pluginFieldType.equals(other.pluginFieldType)) return false;
		if (pluginId == null) {
			if (other.pluginId != null) return false;
		} else if (!pluginId.equals(other.pluginId)) return false;
		if (pluginInstanceName == null) {
			if (other.pluginInstanceName != null) return false;
		} else if (!pluginInstanceName.equals(other.pluginInstanceName)) return false;
		return true;
	}
}
