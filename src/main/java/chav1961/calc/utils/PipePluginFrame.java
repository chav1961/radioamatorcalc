package chav1961.calc.utils;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Locale;

import chav1961.calc.interfaces.DragMode;
import chav1961.calc.interfaces.PipeContainerInterface;
import chav1961.calc.interfaces.PipeContainerItemInterface;
import chav1961.calc.interfaces.PipeItemRuntime;
import chav1961.calc.windows.PipeManager;
import chav1961.calc.windows.PipeManagerSerialForm;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.PrintingException;
import chav1961.purelib.basic.growablearrays.GrowableCharArray;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.streams.JsonStaxPrinter;
import chav1961.purelib.ui.swing.SwingUtils;

public abstract class PipePluginFrame<T> extends InnerFrame<T> implements PipeContainerInterface, PipeItemRuntime {
	private static final long serialVersionUID = 1L;

	public static final String		JSON_PIPE_CONTENT = "content";
	public static final String		JSON_PIPE_LINKS = "links";
	
	public static final String		JSON_PIPE_ITEM_INITIAL_CODE = "initialCode";
	public static final String		JSON_PIPE_ITEM_PROGRAM = "program";
	public static final String		JSON_PIPE_ITEM_EXPRESSION = "expression";
	public static final String		JSON_PIPE_ITEM_PLUGIN_CLASS = "pluginClass";
	public static final String		JSON_PIPE_ITEM_MESSAGE = "message";
	public static final String		JSON_PIPE_ITEM_IS_ERROR = "isError";
	public static final String		JSON_PIPE_ITEM_FIELDS = "fields";
	
	private final PipeManager		parent;
	private final Localizer			localizer;
	private final PipeItemType		itemType;
	private final int 				uniqueId;
	
	public PipePluginFrame(final int uniqueId, final PipeManager parent, final Localizer localizer, final Class<T> classInstance, final PipeItemType itemType) throws ContentException {
		super(classInstance);
		if (parent == null) {
			throw new NullPointerException("Parent manager can't be null");
		}
		else if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (itemType == null) {
			throw new NullPointerException("Item type can't be null");
		}
		else {
			this.uniqueId = uniqueId;
			this.parent = parent;
			this.localizer = localizer;
			this.itemType = itemType;
		}
	}

	public abstract void removeLink(final PipeLink link);
	public abstract void serializeFrame(PipeManagerSerialForm.PluginSpecific specific) throws IOException;
	public abstract void deserializeFrame(PipeManagerSerialForm.PluginSpecific specific) throws IOException;
	
	@Override
	public String getPipeItemName() {
		return itemType+" #"+uniqueId;
	}
	
	@Override
	public PipeItemType getType() {
		return itemType;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Iterable<PipeContainerItemInterface> getItems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasComponentAt(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PipeContainerItemInterface at(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		// TODO Auto-generated method stub
		
	}

	protected void prepareTitle(final String titleId, final String titleTooltipId) throws LocalizationException {
		setTitle(localizer.getValue(titleId)+" ("+getPipeItemName()+")");
		if (titleTooltipId != null) {
			setToolTipText(localizer.getValue(titleTooltipId));
		}
	}
	
	protected PipeManager getParentManager() {
		return parent;
	}

	protected void showHelp(final String helpId) {
		try{SwingUtils.showCreoleHelpWindow(this,
				URIUtils.convert2selfURI(new GrowableCharArray<>(false).append(localizer.getContent(helpId)).extract(),"UTF-8")
			);
		} catch (LocalizationException | NullPointerException | IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void assignDndLink(final Component component) {
		component.addMouseListener(new MouseListener() {
			private Cursor		oldCursor;
			private DragMode	oldDragMode;
			
			@Override public void mouseReleased(MouseEvent e) {}
			@Override public void mousePressed(MouseEvent e) {}
			@Override public void mouseClicked(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {
				parent.setDragMode(oldDragMode);
//				component.setCursor(oldCursor);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				oldCursor = component.getCursor();
				oldDragMode = parent.setDragMode(DragMode.LINKS);
//				component.setCursor(CursorsLibrary.DRAG_HAND);
			}
			
		});
	}

	protected void assignDndComponent(final Component component) {
		component.addMouseListener(new MouseListener() {
			private Cursor		oldCursor;
			private DragMode	oldDragMode;
			
			@Override public void mouseReleased(MouseEvent e) {}
			@Override public void mousePressed(MouseEvent e) {}
			@Override public void mouseClicked(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {
				parent.setDragMode(oldDragMode);
//				component.setCursor(oldCursor);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				oldCursor = component.getCursor();
				oldDragMode = parent.setDragMode(DragMode.CONTROLS);
//				component.setCursor(CursorsLibrary.DRAG_HAND);
			}
		});
	}

	protected String buildVarName(final ContentNodeMetadata var) {
		return "#"+uniqueId+"."+var.getName();
	}
	
	protected String buildVarName(final int pluginId, final String name) {
		return "#"+(pluginId < 0 ? uniqueId : pluginId)+"."+name;
	}
	
	protected int getPluginId() {
		return uniqueId;
	}
	
	protected static String toSerial(final String source) {
		if (source == null || source.trim().isEmpty()) {
			return null;
		}
		else {
			return source.trim();
		}
	}
}
