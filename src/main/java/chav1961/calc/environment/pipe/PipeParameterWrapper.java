package chav1961.calc.environment.pipe;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.ui.interfacers.Format;

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
	private Class<?>	pluginFieldtype;
	
	public PipeParameterWrapper(final String pluginId, final String pluginInstanceName, final String pluginFieldName, final Class<?> pluginFieldtype) {
		this.pluginId = pluginId;
		this.pluginInstanceName = pluginInstanceName;
		this.pluginFieldName = pluginFieldName;
		this.pluginFieldtype = pluginFieldtype;
	}

	public String getPluginId() {
		return pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	public String getPluginInstanceName() {
		return pluginInstanceName;
	}

	public void setPluginInstanceName(String pluginInstanceName) {
		this.pluginInstanceName = pluginInstanceName;
	}

	public String getPluginFieldName() {
		return pluginFieldName;
	}

	public void setPluginFieldName(String pluginFieldName) {
		this.pluginFieldName = pluginFieldName;
	}

	public Class<?> getPluginFieldType() {
		return pluginFieldtype;
	}

	public void setPluginFieldType(Class<?> pluginFieldtype) {
		this.pluginFieldtype = pluginFieldtype;
	}

	@Override
	public String toString() {
		return "PipeParameterWrapper [pluginId=" + pluginId + ", pluginInstanceName=" + pluginInstanceName + ", pluginFieldName=" + pluginFieldName + ", pluginFieldtype=" + pluginFieldtype + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pluginFieldName == null) ? 0 : pluginFieldName.hashCode());
		result = prime * result + ((pluginFieldtype == null) ? 0 : pluginFieldtype.hashCode());
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
		if (pluginFieldtype == null) {
			if (other.pluginFieldtype != null) return false;
		} else if (!pluginFieldtype.equals(other.pluginFieldtype)) return false;
		if (pluginId == null) {
			if (other.pluginId != null) return false;
		} else if (!pluginId.equals(other.pluginId)) return false;
		if (pluginInstanceName == null) {
			if (other.pluginInstanceName != null) return false;
		} else if (!pluginInstanceName.equals(other.pluginInstanceName)) return false;
		return true;
	}
}
