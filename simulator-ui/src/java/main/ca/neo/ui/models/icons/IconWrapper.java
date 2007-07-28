package ca.neo.ui.models.icons;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ca.neo.ui.models.PModel;
import ca.neo.ui.style.Style;
import ca.shu.ui.lib.objects.GText;
import ca.shu.ui.lib.world.INamedObject;
import ca.shu.ui.lib.world.IWorldObject;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

/**
 * An UI Object with an Icon and a Label
 * 
 * TODO: Adaptively render the label, show more details when zoomed in
 * 
 * @author Shu Wu
 * 
 */
public class IconWrapper extends WorldObjectImpl implements INamedObject,
		PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	PNode icon;

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
		this.icon = icon;
		this.icon.setScale(scale);

		addChild(icon);
		label = new GText();
		label.setConstrainWidthToTextWidth(true);
		updateLabel();
		addChild(label);

		if (icon instanceof IWorldObject) {
			((IWorldObject) icon).setDraggable(false);
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
			setBounds(icon.localToParent(icon.getBounds()));
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
				label.setText(parent.getTypeName() + ": " + parent.getName());
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
		double iconWidth = icon.getWidth() * icon.getScale();
		double labelWidth = label.getWidth();
		double offsetX = ((labelWidth - iconWidth) / 2.0) * -1;

		label.setOffset(offsetX, icon.getHeight() * icon.getScale());

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
		tag.setTextPaint(Style.FOREGROUND_COLOR);
		tag.setFont(Style.FONT_LARGE);
		addToLayout(tag);
	}

}
