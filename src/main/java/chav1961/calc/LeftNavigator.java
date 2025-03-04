package chav1961.calc;

import java.net.URI;
import java.util.ServiceLoader;
import java.util.regex.Pattern;

import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import chav1961.calc.interfaces.PluginInterface;
import chav1961.purelib.basic.SubstitutableProperties;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.swing.SimpleNavigatorTree;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.interfaces.OnAction;

class LeftNavigator extends SimpleNavigatorTree<ContentNodeMetadata>{
	private static final long 		serialVersionUID = 1L;
	private static final String		FAVORITE_NAME = "menu.favorites";
	private static final String		FAVORITE_ITEM = "favorite";
	private static final Pattern	CLEAR_ALL = Pattern.compile("\\Q"+FAVORITE_ITEM+"\\E\\d+");

	private final JPopupMenu	clearAllMenu;
	private final JPopupMenu	removeMenu;
	private final JPopupMenu	toFavoritesMenu;
	private final SubstitutableProperties props;
	private TreePath	favorites;
	private TreePath	lastMenu = null;
	
	LeftNavigator(final Localizer localizer, final ContentNodeMetadata root, final SubstitutableProperties props) throws NullPointerException, IllegalArgumentException {
		super(localizer, root);
		this.props = props;
		this.clearAllMenu = SwingUtils.toJComponent(root.getOwner().byUIPath(URI.create("ui:/model/navigation.top.navigator.favorite")),JPopupMenu.class);		
		this.removeMenu = SwingUtils.toJComponent(root.getOwner().byUIPath(URI.create("ui:/model/navigation.top.navigator.favorite.item")),JPopupMenu.class);		
		this.toFavoritesMenu = SwingUtils.toJComponent(root.getOwner().byUIPath(URI.create("ui:/model/navigation.top.navigator.plugin")),JPopupMenu.class);
		SwingUtils.assignActionListeners(clearAllMenu, this);
		SwingUtils.assignActionListeners(removeMenu, this);
		SwingUtils.assignActionListeners(toFavoritesMenu, this);
		
		for(int index = 1; props.containsKey(FAVORITE_ITEM+index); index++) {
			final String	name = props.getValue(FAVORITE_ITEM+index);
			
			for (PluginInterface<?> item : ServiceLoader.load(PluginInterface.class)) {
				if (item.getPluginName().equals(name)) {
					addFavorite(item.getMetadata());
					break;
				}
			}
		}
	}

	@Override
	protected void appendNodes(final ContentNodeMetadata submenu, final DefaultMutableTreeNode node) {
		if (FAVORITE_NAME.equals(submenu.getName())) {
			favorites = new TreePath(node.getPath());
		}
		else {
			final String	namePrefix = submenu.getName()+'.';
			
			for (PluginInterface<?> item : ServiceLoader.load(PluginInterface.class)) {
				if (item.getPluginName().startsWith(namePrefix)) {
					node.add(new DefaultMutableTreeNode(item.getMetadata(),false));
				}
			}
		}
	}
	
	@Override
	protected JPopupMenu getPopupMenu(final TreePath path, final ContentNodeMetadata meta) {
		boolean	favorites = false;
		int		depth = -1, count = 0;
		
		for(Object item : path.getPath()) {
			count++;
			if (FAVORITE_NAME.equals(get(item).getName())) {
				favorites = true;
				depth = count;
			}
		}
		if (!favorites) {
			final ContentNodeMetadata	info = get(path.getLastPathComponent()); 
					
			if (FormManager.class.isAssignableFrom(info.getType())) {
				lastMenu = path;
				return toFavoritesMenu;
			}
			else {
				lastMenu = null;
				return null;
			}
		}
		else {
			if (depth == count) {
				lastMenu = path;
				return clearAllMenu;
			}
			else {
				lastMenu = path;
				return removeMenu;
			}
		}
	}
	
	@OnAction("action:/clearAllFavorites")
	private void clearAllFavorites() {
		final DefaultMutableTreeNode	favorite = (DefaultMutableTreeNode)favorites.getLastPathComponent();
		
		favorite.removeAllChildren();
		((DefaultTreeModel)getModel()).nodeStructureChanged(favorite);
		props.removeAll(CLEAR_ALL);
	}

	@OnAction("action:/removeFavorite")
	private void removeFavorite() {
		final DefaultMutableTreeNode	favorite = (DefaultMutableTreeNode)favorites.getLastPathComponent();
		final ContentNodeMetadata		mdRemove = get(lastMenu.getLastPathComponent());
		final DefaultMutableTreeNode	toRemove = (DefaultMutableTreeNode)lastMenu.getLastPathComponent();

		favorite.remove(toRemove);
		((DefaultTreeModel)getModel()).nodeStructureChanged(favorite);
		boolean	found = false;
		
		for(int index = 1; props.containsKey(FAVORITE_ITEM+index); index++) {
			if (props.getValue(FAVORITE_ITEM+index).equals(mdRemove.getName())) {
				found = true;
				props.remove(FAVORITE_ITEM+index);
			}
			else if (found) {
				props.setProperty(FAVORITE_ITEM+(index-1), props.remove(FAVORITE_ITEM+index).toString());
			}
		}
	}

	@OnAction("action:/addFavorite")
	private void addFavorite() {
		final ContentNodeMetadata		ref = get(lastMenu.getLastPathComponent()); 
		int	index;
		
		addFavorite(ref);
		for(index = 1; props.containsKey(FAVORITE_ITEM+index); index++) {
		}
		props.setProperty(FAVORITE_ITEM+index, ref.getName());
	}

	private void addFavorite(final ContentNodeMetadata ref) {
		final DefaultMutableTreeNode	toAdd = new DefaultMutableTreeNode(ref);
		final DefaultMutableTreeNode	favorite = (DefaultMutableTreeNode)favorites.getLastPathComponent();
		
		favorite.add(toAdd);
		((DefaultTreeModel)getModel()).nodeStructureChanged(favorite);
	}
	
	private ContentNodeMetadata get(final Object item) {
		return (ContentNodeMetadata)((DefaultMutableTreeNode)item).getUserObject();
	}
}
