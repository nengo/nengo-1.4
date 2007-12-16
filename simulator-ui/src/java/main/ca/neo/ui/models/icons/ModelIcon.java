package ca.neo.ui.models.icons;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ca.neo.ui.models.UIModel;
import ca.shu.ui.lib.objects.PXText;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.PNode;

/**
 * An Icon which has a representation and an label. It is used to represent NEO
 * models. 
 * 
 * @author Shu Wu
 */
public class ModelIcon extends WorldObject implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	/**
	 * The inner icon node which contains the actual icon representation
	 */
	private PNode iconReal;

	/**
	 * Label of the icon
	 */
	private PXText label;

	/**
	 * Parent of this icon
	 */
	private UIModel parent;

	/**
	 * Whether to show the type of model in the label
	 */
	private boolean showTypeInLabel = true;

	/**
	 * @param parent
	 *            The Model the icon is representing
	 * @param icon
	 *            the UI representation
	 * @param scale
	 *            Scale of the Icon
	 */
	public ModelIcon(UIModel parent, PNode icon) {
		super();
		this.parent = parent;
		this.iconReal = icon;

		addChild(icon);
		label = new PXText();
		label.setConstrainWidthToTextWidth(true);
		updateLabel();
		addChild(label);

		if (icon instanceof WorldObject) {
			((WorldObject) icon).setSelectable(false);
		}

		// parent.addPropertyChangeListener(PROPERTY_NAME, this);
		parent.addPropertyChangeListener(UIModel.PROPERTY_MODEL, this);
		setSelectable(false);

		/*
		 * The bounds of this object matches those of the real icon
		 */
		iconReal.addPropertyChangeListener(PROPERTY_FULL_BOUNDS, this);
		updateBounds();
	}

	/**
	 * Updates the bounds of this node based on the inner icon
	 */
	private void updateBounds() {
		setBounds(iconReal.localToParent(iconReal.getBounds()));
	}

	protected PNode getIconReal() {
		return iconReal;
	}

	protected UIModel getModelParent() {
		return parent;
	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();

		/*
		 * Layout the icon and label
		 */
		double iconWidth = getWidth() * getScale();
		double labelWidth = label.getWidth();
		double offsetX = ((labelWidth - iconWidth) / 2.0) * -1;

		label.setOffset(offsetX, getHeight() * getScale());

	}

	/**
	 * Called when the NEO model has been updated
	 */
	protected void modelUpdated() {
		updateLabel();
	}

	/**
	 * Configures the label
	 * 
	 * @param showType
	 *            Whether to show the model type in the label
	 */
	public void configureLabel(boolean showType) {
		showTypeInLabel = showType;
		updateLabel();
	}

	@Override
	public void doubleClicked() {
		parent.doubleClicked();
	}

	// @Override
	// public void signalBoundsChanged() {
	//
	// super.signalBoundsChanged();
	//
	// /*
	// * Pass on the message to icon
	// */
	// iconReal.signalBoundsChanged();
	// }

	/**
	 * @return the name of the label
	 */
	@Override
	public String getName() {
		return label.getText();
	}

	/**
	 * Updates bounds and label name
	 */
	public void propertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();

		if (propertyName == PROPERTY_FULL_BOUNDS) {
			updateBounds();
		} else if (propertyName == UIModel.PROPERTY_MODEL) {
			modelUpdated();
		}

	}

	/**
	 * @param isVisible
	 *            Whether the label is visible
	 */
	public void setLabelVisible(boolean isVisible) {
		if (isVisible) {
			addChild(label);
		} else {
			if (label.getParent() != null)
				label.removeFromParent();

		}
	}

	/**
	 * Updates the label text
	 */
	public void updateLabel() {
		if (showTypeInLabel) {
			if (parent.getName().compareTo("") == 0)
				label.setText("unnamed " + parent.getTypeName());
			else
				label.setText(parent.getName() + " (" + parent.getTypeName()
						+ ")");
		} else {
			if (parent.getName().compareTo("") == 0)
				label.setText("unnamed");
			else
				label.setText(parent.getName());
		}
	}

}