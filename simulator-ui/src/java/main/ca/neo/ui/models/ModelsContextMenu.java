package ca.neo.ui.models;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JPopupMenu;

import ca.shu.ui.lib.objects.models.ModelObject;
import ca.shu.ui.lib.util.menus.AbstractMenuBuilder;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import ca.shu.ui.lib.world.WorldObject;

/**
 * Creates a Popup menu which applies to a collection of models
 * 
 * @author Shu Wu
 */
public class ModelsContextMenu {

	/**
	 * @param selectedObjects
	 *            Selected objects which a popup menu is created for
	 * @return Context menu for selected objects
	 */
	public static JPopupMenu getMenu(Collection<ModelObject> selectedObjects) {
		if (selectedObjects.size() == 0)
			return null;
		else if (selectedObjects.size() == 1) {
			return selectedObjects.iterator().next().getContextMenu();
			
		} else {
			ModelsContextMenu instance = new ModelsContextMenu(selectedObjects);

			return instance.getMenu();
		}
	}

	private JPopupMenu menu;

	private Collection<ModelObject> selectedObjects;

	private HashMap<Class<? extends ModelObject>, LinkedList<ModelObject>> selectionMap = new HashMap<Class<? extends ModelObject>, LinkedList<ModelObject>>();

	protected ModelsContextMenu(Collection<ModelObject> models) {
		super();
		this.selectedObjects = models;
		init();
	}

	private JPopupMenu getMenu() {
		return menu;
	}

	private void init() {
		initSelectionMap();
		constructMenu();
	}

	private synchronized JPopupMenu initSelectionMap() {

		selectionMap.clear();

		/*
		 * sort the selection by class type, so that for each class type a
		 * collection of models are of the same type (homogeneous)
		 */
		for (WorldObject object : selectedObjects) {
			if (object instanceof ModelObject) {
				ModelObject modelUI = (ModelObject) object;

				LinkedList<ModelObject> objects = selectionMap.get(modelUI
						.getClass());

				if (objects == null) {
					objects = new LinkedList<ModelObject>();
					selectionMap.put(modelUI.getClass(), objects);
				}

				objects.add(modelUI);

			}
		}

		return null;
	}

	protected void constructMenu() {
		PopupMenuBuilder menuBuilder = null;
		if (selectionMap.keySet().size() > 1) {

			menuBuilder = new PopupMenuBuilder("Selected Objects");
		}

		for (Class<? extends ModelObject> type : selectionMap.keySet().toArray(
				new Class[0])) {

			LinkedList<ModelObject> homogeneousModels = selectionMap.get(type);
			String typeName = homogeneousModels.getFirst().getTypeName();

			String typeMenuName = homogeneousModels.size() + " " + typeName
					+ "s";

			AbstractMenuBuilder typeMenu;
			if (menuBuilder == null) {
				typeMenu = new PopupMenuBuilder(typeMenuName);
				menuBuilder = (PopupMenuBuilder) typeMenu;
			} else {
				typeMenu = menuBuilder.addSubMenu(typeMenuName);

			}

			UIModels.constructMenuForModels(typeMenu, type, typeName,
					homogeneousModels);

		}

		menu = menuBuilder.toJPopupMenu();
	}
}
