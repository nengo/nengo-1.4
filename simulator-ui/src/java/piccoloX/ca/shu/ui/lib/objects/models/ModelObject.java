package ca.shu.ui.lib.objects.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPopupMenu;

import ca.neo.ui.actions.RemoveModelAction;
import ca.neo.ui.models.tooltips.Tooltip;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.neo.ui.models.tooltips.TooltipTitle;
import ca.shu.ui.lib.activities.Pulsator;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;
import ca.shu.ui.lib.world.Interactable;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.elastic.ElasticObject;

/**
 * A UI Object which wraps a model
 * 
 * @author Shu Wu
 */
public abstract class ModelObject extends ElasticObject implements Interactable {

	private static final long serialVersionUID = 1L;

	/**
	 * The property name that identifies a change in this node's Model
	 */
	public static final String PROPERTY_MODEL = "uiModel";

	/**
	 * Icon for this model
	 */
	private WorldObject icon;

	/**
	 * Property Listener which listens to changes of the Icon's bounds and
	 * updates this node bounds accordingly
	 */
	private PropertyChangeListener iconPropertyChangeListener;

	private boolean isModelBusy = false;

	/**
	 * Model
	 */
	private Object model;

	private Pulsator pulsator = null;

	/**
	 * Creates a hollow wrapper without any model
	 */
	public ModelObject() {
		super();
	}

	/**
	 * Create a UI Wrapper around a Model
	 * 
	 * @param model
	 *            Model
	 */
	public ModelObject(Object model) {

		setModel(model);

	}

	/**
	 * @return Constructed Context Menu
	 */
	protected void constructMenu(PopupMenuBuilder menu) {
		menu.addAction(new RemoveModelAction("Remove this", this));
	}

	protected void constructTooltips(TooltipBuilder builder) {
		// do nothing
	}

	@Override
	protected void prepareForDestroy() {
		super.prepareForDestroy();
		setModelBusy(false);
	}

	/**
	 * @param newIcon
	 *            New Icon
	 */
	protected void setIcon(WorldObject newIcon) {
		if (icon != null) {
			icon.removePropertyChangeListener(PROPERTY_BOUNDS,
					iconPropertyChangeListener);
			icon.removeFromParent();
		}

		icon = newIcon;

		addChild(0, icon);
		icon.setSelectable(false);

		iconPropertyChangeListener = new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent arg0) {
				setBounds(icon.getBounds());
			}

		};
		setBounds(icon.getBounds());

		icon.addPropertyChangeListener(PROPERTY_BOUNDS,
				iconPropertyChangeListener);

	}

	/**
	 * Called if this object is double clicked on
	 */
	@Override
	public void doubleClicked() {
		super.doubleClicked();
		if (getWorld() != null) {
			getWorld().zoomToObject(this);
		}
	}

	/**
	 * Fires when the model has changed
	 */
	public void fireModelPropertyChanged() {
		firePropertyChange(0, PROPERTY_MODEL, null, null);
	}

	public String getFullName() {
		return getName() + " (" + getTypeName() + ")";
	}

	/**
	 * @return Icon of this node
	 */
	public WorldObject getIcon() {
		return icon;
	}

	/**
	 * @return Model
	 */
	public Object getModel() {
		return model;
	}

	@Override
	public final WorldObject getTooltip() {
		String toolTipTitle = getFullName();

		TooltipBuilder tooltipBuilder = new TooltipBuilder(toolTipTitle);
		if (isModelBusy()) {

			tooltipBuilder.addPart(new TooltipTitle("Currently busy"));

		} else if (!isModelExists()) {
			tooltipBuilder.addPart(new TooltipTitle("Model is not ready"));
		} else {

			constructTooltips(tooltipBuilder);
		}

		return new Tooltip(tooltipBuilder);
	}

	/**
	 * @return What this type of Model is called
	 */
	public abstract String getTypeName();

	public boolean isModelBusy() {
		return isModelBusy;
	}

	/**
	 * @return Whether the Model exists
	 */
	public boolean isModelExists() {
		return (model != null);
	}

	/**
	 * @param model
	 *            New Model
	 */
	public void setModel(Object model) {
		this.model = model;
		fireModelPropertyChanged();
	}

	/**
	 * @param isBusy
	 *            Whether the model is currently busy. If it is busy, the object
	 *            will not be interactable.
	 */
	public void setModelBusy(boolean isBusy) {
		if (isModelBusy != isBusy) {
			isModelBusy = isBusy;

			if (isModelBusy) {
				Util
						.Assert(pulsator == null,
								"Previous pulsator has not been disposed of properly);");
				pulsator = new Pulsator(this);
			} else {
				if (pulsator != null) {
					pulsator.finish();
					pulsator = null;
				}
			}

		}
	}

	/*
	 * (non-Javadoc) This method is final. To add items to the menu, override
	 * constructMenu() instead.
	 * 
	 * @see ca.shu.ui.lib.handlers.Interactable#showContextMenu(edu.umd.cs.piccolo.event.PInputEvent)
	 */
	public final JPopupMenu getContextMenu() {
		if (isModelBusy()) {
			return null;
		} else if (!isModelExists()) {
			UserMessages.showWarning("This model  is not configured yet");
			return null;
		} else {
			PopupMenuBuilder menu = new PopupMenuBuilder("Model: " + getName());
			constructMenu(menu);

			return menu.toJPopupMenu();
		}
	}

	/**
	 * Updates the UI from the model
	 */
	public final void updateModel() {
		if (getModel() != null) {
			setModel(getModel());
		}
	}

}
