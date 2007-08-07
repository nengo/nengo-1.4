package ca.neo.ui.models.icons;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ca.neo.ui.models.PModel;
import ca.neo.ui.style.Style;
import ca.shu.ui.lib.objects.GText;
import ca.shu.ui.lib.world.NamedObject;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * An UI Object with an Icon and a Label
 * 
 * TODO: Adaptively render the label, show more details when zoomed in
 * 
 * @author Shu Wu
 * 
 */
public class IconWrapper extends WorldObjectImpl implements NamedObject,
		PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	PNode iconReal;

	GText label;

	PModel parent;

	public IconWrapper(PModel parent, PNode innerNode) {
		this(parent, innerNode, 1);
	}

	/**
	 * @param parent
	 *            The Model the icon is representing
	 * @param icon
	 *            the UI representation
	 * @param scale
	 *            Scale of the Icon
	 */
	public IconWrapper(PModel parent, PNode icon, float scale) {
		super();
		this.parent = parent;
		this.iconReal = icon;
		this.iconReal.setScale(scale);

		addChild(icon);
		label = new GText();
		label.setConstrainWidthToTextWidth(true);
		updateLabel();
		addChild(label);

		if (icon instanceof WorldObject) {
			((WorldObject) icon).setDraggable(false);
		}

		parent.addPropertyChangeListener(PROPERTY_NAME, this);
		this.setDraggable(false);

		this.addPropertyChangeListener(PROPERTY_FULL_BOUNDS, this);

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

	@Override
	public WorldObjectImpl getTooltipObject() {
		return new IconTooltip(this);
	}

	/**
	 * Is called when the parent's name is changed
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName() == PROPERTY_NAME) {
			updateLabel();
		} else if (event.getPropertyName() == PROPERTY_FULL_BOUNDS) {
			setBounds(iconReal.localToParent(iconReal.getBounds()));
		}

	}

	@Override
	public void removedFromWorld() {
		super.removedFromWorld();
		parent.removePropertyChangeListener(PROPERTY_NAME, this);
		this.removePropertyChangeListener(PROPERTY_FULL_BOUNDS, this);
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
		double iconWidth = iconReal.getWidth() * iconReal.getScale();
		double labelWidth = label.getWidth();
		double offsetX = ((labelWidth - iconWidth) / 2.0) * -1;

		label.setOffset(offsetX, iconReal.getHeight() * iconReal.getScale());

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

class IconTooltip extends WorldObjectImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IconTooltip(IconWrapper icon) {
		super();
		PText tag = new PText(icon.getName() + " Icon");
		tag.setTextPaint(Style.COLOR_FOREGROUND);
		tag.setFont(Style.FONT_LARGE);
		addToLayout(tag);
	}

}
