package chav1961.calc.windows;

import java.util.Arrays;

import chav1961.calc.interfaces.PipeContainerInterface.PipeItemType;
import chav1961.calc.interfaces.PipeSerialLinkType;
import chav1961.purelib.model.MutableContentNodeMetadata;

public class PipeManagerSerialForm {
	public int 					itemCount;
	public PluginSerialForm[]	itemList;
	public PluginSerialLink[]	links;
	
	public static class PluginSerialForm {
		public String			id;
		public PipeItemType		type;
		public PluginGeometry	geometry;
		public PluginSpecific	content;
	}
	
	public static class PluginGeometry {
		public int			x;
		public int			y;
		public int			width;
		public int			height;
		public boolean		iconized;
		public boolean		maximized;
	}
	
	public static class PluginSerialLink {
		public PipeSerialLinkType	type;
		public String		source;
		public String		sourceControl;
		public String		target;
		public String		targetControl;
	}
	
	public static class PluginSpecific {
		public String		initialCode;
		public String		program;
		public String		expression;
		public String		pluginClass;
		public String		message;
		public boolean		isError;
		public MutableContentNodeMetadata[] fields;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((expression == null) ? 0 : expression.hashCode());
			result = prime * result + Arrays.hashCode(fields);
			result = prime * result + ((initialCode == null) ? 0 : initialCode.hashCode());
			result = prime * result + (isError ? 1231 : 1237);
			result = prime * result + ((message == null) ? 0 : message.hashCode());
			result = prime * result + ((pluginClass == null) ? 0 : pluginClass.hashCode());
			result = prime * result + ((program == null) ? 0 : program.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			PluginSpecific other = (PluginSpecific) obj;
			if (expression == null) {
				if (other.expression != null) return false;
			} else if (!expression.equals(other.expression)) return false;
			if (!Arrays.equals(fields, other.fields)) return false;
			if (initialCode == null) {
				if (other.initialCode != null) return false;
			} else if (!initialCode.equals(other.initialCode)) return false;
			if (isError != other.isError) return false;
			if (message == null) {
				if (other.message != null) return false;
			} else if (!message.equals(other.message)) return false;
			if (pluginClass == null) {
				if (other.pluginClass != null) return false;
			} else if (!pluginClass.equals(other.pluginClass)) return false;
			if (program == null) {
				if (other.program != null) return false;
			} else if (!program.equals(other.program)) return false;
			return true;
		}

		@Override
		public String toString() {
			return "PluginSpecific [initialCode=" + initialCode + ", program=" + program + ", expression=" + expression
					+ ", pluginClass=" + pluginClass + ", message=" + message + ", isError=" + isError + ", fields="
					+ Arrays.toString(fields) + "]";
		}
	}
}
