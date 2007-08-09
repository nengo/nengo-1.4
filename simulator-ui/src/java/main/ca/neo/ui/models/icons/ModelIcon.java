package ca.neo.ui.models.icons;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ca.neo.ui.models.PModel;
import ca.shu.ui.lib.objects.GText;
import ca.shu.ui.lib.world.NamedObject;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.PNode;

/**
 * An UI Object with an Icon and a Label
 * 
 * TODO: Adaptively render the label, show more details when zoomed in
 * 
 * @author Shu Wu
 * 
 */
public class ModelIcon extends WorldObject implements NamedObject,
		PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	PNode iconReal;

	GText label;

	PModel parent;

	public PModel getModelParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            The Model the icon is representing
	 * @param icon
	 *            the UI representation
	 * @param scale
	 *            Scale of the Icon
	 */
	public ModelIcon(PModel parent, PNode icon) {
		super();
		this.parent = parent;
		this.iconReal = icon;

		addChild(icon);
		label = new GText();
		label.setConstrainWidthToTextWidth(true);
		updateLabel();
		addChild(label);

		if (icon instanceof WorldObject) {
			((WorldObject) icon).setDraggable(false);
		}

		// parent.addPropertyChangeListener(PROPERTY_NAME, this);
		parent.addPropertyChangeListener(PModel.PROPERTY_MODEL, this);
		setDraggable(false);

		/*
		 * The bounds of this object matches those of the real icon
		 */
		iconReal.addPropertyChangeListener(PROPERTY_FULL_BOUNDS, this);
		updateBounds();
	}

	private void updateBounds() {
		setBounds(iconReal.localToParent(iconReal.getBounds()));
	}

	boolean showTypeInLabel = true;

	public void configureLabel(boolean showType) {
		showTypeInLabel = showType;
		updateLabel();
	}

	@Override
	public void doubleClicked() {
		parent.doubleClicked();
	}

	public void setLabelVisible(boolean isVisible) {
		if (isVisible) {
			addChild(label);
		} else {
			if (label.getParent() != null)
				label.removeFromParent();

		}
		// label.setVisible(isVisible);
	}

	/**
	 * @return the name of the label
	 */
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
		} else if (propertyName == PModel.PROPERTY_MODEL) {
			modelUpdated();
		}

	}

	protected void modelUpdated() {
		updateLabel();
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

	@Override
	public void signalBoundsChanged() {

		super.signalBoundsChanged();

		/*
		 * Pass on the message to icon
		 */
		iconReal.signalBoundsChanged();
	}

	public PNode getIconReal() {
		return iconReal;
	}

}

// class IconTooltip extends WorldObject {
//
// /**
// *
// */
// private static final long serialVersionUID = 1L;
//
// public IconTooltip(ModelIcon icon) {
// super();
// PText tag = new PText(icon.getName() + " Icon");
// tag.setTextPaint(Style.COLOR_FOREGROUND);
// tag.setFont(Style.FONT_LARGE);
// addToLayout(tag);
// }
//
// }
