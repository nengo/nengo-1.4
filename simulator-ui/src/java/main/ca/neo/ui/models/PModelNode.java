package ca.neo.ui.models;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Vector;

import javax.swing.AbstractAction;

import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.StructuralException;
import ca.neo.ui.models.wrappers.PDecodedTermination;
import ca.neo.ui.models.wrappers.POrigin;
import ca.neo.ui.views.objects.configurable.managers.IConfigurationManager;
import ca.neo.ui.views.objects.configurable.struct.PropertyStructure;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolo.PNode;

public abstract class PModelNode extends PModelConfigurable {

	public PModelNode(boolean useDefaultConfigManager) {
		super(useDefaultConfigManager);
		// TODO Auto-generated constructor stub
	}

	public PModelNode() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PModelNode(IConfigurationManager configManager) {
		super(configManager);
	}

	@Override
	public PopupMenuBuilder constructMenu() {

		PopupMenuBuilder menu = super.constructMenu();

		// menu.addSubMenu(label)

		Origin[] origins = getNode().getOrigins();
		if (origins.length > 0) {
			menu.addSection("Node");

			MenuBuilder originsMenu = menu.createSubMenu("Show origins");

			originsMenu.addAction(new ShowAllOriginAction());
			for (int i = 0; i < origins.length; i++) {
				originsMenu
						.addAction(new ShowOriginAction(origins[i].getName()));
			}

		}

		return menu;
	}

	public Node getNode() {
		return (Node) getModel();
	}

	@Override
	public PropertyStructure[] getPropertiesSchema() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Shows all the origins on the Node model
	 */
	public void showAllOrigins() {

		Origin[] origins = getNode().getOrigins();

		for (int i = 0; i < origins.length; i++) {
			showOrigin(origins[i].getName());
		}

	}

	/**
	 * @param name
	 *            Name of an Origin on the Node model
	 * 
	 */
	public void showOrigin(String originName) {
		POrigin obj;
		try {
			obj = new POrigin(getNode().getOrigin(originName));
			obj.setScale(0.5);

			addWidget(obj);

		} catch (StructuralException e) {
			Util.Error(e.toString());
		}
	}

	// Vector<POrigin> origins;

	Vector<WorldObjectImpl> widgets;

	protected void addWidget(WorldObjectImpl widget) {
		if (widgets == null) {
			widgets = new Vector<WorldObjectImpl>(3);
		}
		widgets.add(widget);
		addChild(widget);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void layoutChildren() {
		// TODO Auto-generated method stub
		super.layoutChildren();

		layoutWidgets();

	}

	/**
	 * layout widgets such as Origins and Terminations
	 */
	protected void layoutWidgets() {
		if (widgets != null) {
			double originX = -20;
			double originY = getIcon().getHeight();

			double termX = getIcon().getWidth() + 5;
			double termY = originY;

			Iterator<WorldObjectImpl> it = widgets.iterator();
			Vector<WorldObjectImpl> widgetsToRemove = new Vector<WorldObjectImpl>(
					0);
			/*
			 * Lays out origin objects
			 */
			while (it.hasNext()) {
				WorldObjectImpl obj = it.next();

				if (obj.getParent() == null) {
					/*
					 * Check to see that the origin has not been removed from
					 * the world
					 */
					widgetsToRemove.add(obj);
				} else {
					if (obj instanceof POrigin) {
						originY -= obj.getScale() * obj.getHeight() + 5;
						obj.setOffset(originX, originY);

					} else if (obj instanceof PDecodedTermination) {
						termY -= obj.getScale() * obj.getHeight() + 5;
						obj.setOffset(termX, termY);
					}
				}
			}

			it = widgetsToRemove.iterator();
			while (it.hasNext()) {
				widgets.remove(it.next());
			}
		}
	}

	class ShowOriginAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		String originName;

		public ShowOriginAction(String originName) {
			super(originName);
			this.originName = originName;
		}

		public void actionPerformed(ActionEvent e) {
			showOrigin(originName);
		}
	}

	class ShowAllOriginAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ShowAllOriginAction() {
			super("Show all");
		}

		public void actionPerformed(ActionEvent e) {
			showAllOrigins();
		}
	}
	// Vector<POrigin> origins;
	// public void showOrigin(Origin origin) {
	// POrigin originObject = new POrigin(origin);
	// addChild(originObject);
	//		
	// if (origins == null) {
	// origins = new Vector<POrigin>();
	// }
	// origins.add(e)
	//		
	// }

}
