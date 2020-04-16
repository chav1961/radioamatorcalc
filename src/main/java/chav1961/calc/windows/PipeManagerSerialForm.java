package chav1961.calc.windows;

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
	}
}
