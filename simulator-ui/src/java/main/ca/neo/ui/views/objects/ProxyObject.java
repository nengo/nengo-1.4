package ca.neo.ui.views.objects;

import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.text.SimpleAttributeSet;

import ca.neo.model.Node;
import ca.neo.ui.views.icons.Icon;
import ca.sw.graphics.nodes.WorldObject;
import ca.sw.graphics.world.WorldFrame;
import ca.sw.util.Util;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

public abstract class ProxyObject<E> extends WorldObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected E proxy;

	Icon icon;

	private JDialog dialog;

	SimpleAttributeSet properties;

	public ProxyObject() {
		super();

		initProperties();

	}

	public void saveProperties() {
		Util.saveObject(properties, getDefaultPropertiesName());
	}

	private String getDefaultPropertiesName() {
		return this.getClass() + "_" + "defaults";
	}

	private void initProperties() {
		// MetaProperty[] metaProperties = getMetaProperties();
		properties = (SimpleAttributeSet) Util
				.loadObject(getDefaultPropertiesName());

		if (properties == null) {
			properties = new SimpleAttributeSet();
		}
		// if (metaProperties != null) {
		//
		// if (metaProperties != null) {
		// for (int i = 0; i < metaProperties.length; i++) {
		// MetaProperty metaProperty = metaProperties[i];
		// PropertyWrapper property = new PropertyWrapper(metaProperty);
		//
		// propertiesLookup.addAttribute(metaProperty.getName(),
		// property);
		// }
		// }
		// }
	}

	public void initProxy0() {
		proxy = createProxy();

		updateRepresentation();

		saveProperties();
	}

	LineEndWell lineEndWell;

	protected void updateRepresentation() {
		if (proxy instanceof Node) {
			if (lineEndWell == null) {
				lineEndWell = new LineEndWell();

				lineEndWell.setOffset(icon.getBounds().getMaxY() + 5, 0);
				addChild(lineEndWell);

			}
		}

	}

	protected abstract E createProxy();

	public E getProxy() {
		return proxy;
	}

	public void setIcon(Icon icon) {
		if (this.icon != null) {

			this.icon.removeFromParent();
		}

		this.icon = icon;
		icon.setDraggable(false);
		this.addChild(icon);
		setBounds(getFullBounds());

	}

	protected abstract MetaProperty[] getMetaProperties();

	public void initProxy() {
		if (getMetaProperties() == null || getMetaProperties().length == 0) {
			initProxy0();
			return;
		}

		if (dialog == null) {
			WorldFrame frame = getWorldFrame();
			dialog = new PropertiesDialog(frame, this);

			dialog.setVisible(true);
		}
	}

	public Object getProperty(String name) {
		return properties.getAttribute(name);
	}

	public void setProperty(String name, Object value) {
		properties.addAttribute(name, value);
	}

	public Icon getIcon() {
		return icon;
	}

	// public Vector<PropertyWrapper> getProperties() {
	// return properties;
	// }

}

/*
 * Enables type checking and adds styling meta data to a Property
 */
class MetaProperty {

	String name;

	Class propertyType;

	public MetaProperty(String name, Class propertyType) {
		super();
		this.name = name;
		this.propertyType = propertyType;
	}

	public String getName() {
		return name;
	}

	public Class getType() {

		return propertyType;
	}

}
