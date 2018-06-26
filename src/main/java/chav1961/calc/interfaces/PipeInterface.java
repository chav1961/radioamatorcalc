package chav1961.calc.interfaces;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

public interface PipeInterface extends Closeable {
	public interface UserDefinedField {
		String getFieldLabel();
		int getFieldType();
		Object getGetterAndSetter();
	}
	public interface DataLinkDescription {
		String getLinkId();
		String getSource();
		String getTarget();
		String getExpression();
		Object calculate();
		Object getCargoAssociated();
	}
	public interface ControlLinkDescription {
		String getLinkId();
		String getSource();
		String getTarget();
		DataLinkDescription[] incomingLinks();
		DataLinkDescription[] outgoingLinks();
		boolean isReady();
		Object getCargoAssociated();
	}
	public interface ContentDescription {
		String[] getComponentIds();
		boolean contains(String componentId);
		Object getComponentById(String componentId);
		ControlLinkDescription[] getControlLinks();
		ControlLinkDescription[] getControlLinks(String componentId);
		boolean containsLink(String linkId);
		ControlLinkDescription getControlLink(String linkId);
		Object getCargoAssociated();
	}
	String getPipeNameId();
	String getPipeCaptionId();
	URI getPipeLocation() throws IOException;
	URI getComponentsRequired() throws IOException;
	String getPipeTooltipId();
	String getPipeHelpId();
	UserDefinedField[] getSourceFields();
	UserDefinedField[] getDestinationFields();
	ContentDescription getContentDescription();
	void serialize(Writer writer) throws IOException;
	void deserialize(Reader reader) throws IOException;
	boolean isReadyToPlay();
	void play() throws IOException;
}
