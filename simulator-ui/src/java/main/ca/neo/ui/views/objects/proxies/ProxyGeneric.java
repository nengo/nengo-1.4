package ca.neo.ui.views.objects.proxies;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.text.SimpleAttributeSet;

import ca.neo.model.Node;
import ca.neo.ui.views.icons.Icon;
import ca.neo.ui.views.objects.properties.IPropertiesConfigurable;
import ca.neo.ui.views.objects.properties.PropertiesDialog;
import ca.neo.ui.views.objects.properties.PropertySchema;
import ca.sw.graphics.basics.GDefaults;
import ca.sw.graphics.basics.GText;
import ca.sw.graphics.nodes.WorldObject;
import ca.sw.graphics.nodes.lines.ILineAcceptor;
import ca.sw.graphics.nodes.lines.LineEnd;
import ca.sw.graphics.nodes.lines.LineEndWell;
import ca.sw.graphics.nodes.lines.LineIn;
import ca.sw.graphics.world.WorldFrame;
import ca.sw.util.Util;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

public abstract class ProxyGeneric<E> extends WorldObject implements
		ILineAcceptor, IPropertiesConfigurable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JDialog dialog;

	protected E proxy;

	Icon icon;

	LineEndWell lineEndWell;

	LineIn lineIn;

	SimpleAttributeSet properties;

	public ProxyGeneric() {
		super();

		initProperties();

		this.addPropertyChangeListener(PNode.PROPERTY_FULL_BOUNDS,
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent arg0) {
						if (lineEndWell != null)
							lineEndWell.layoutEdges();

						if (lineIn != null) {
							lineIn.layoutEdges();
						}
					}
				});
	}

	public Icon getIcon() {
		return icon;
	}

	public abstract PropertySchema[] getMetaProperties();

	public Object getProperty(String name) {
		return properties.getAttribute(name);
	}

	public E getProxy() {
		return proxy;
	}

	public void initProxy() {
		if (getMetaProperties() == null || getMetaProperties().length == 0) {
			configurationComplete();
			return;
		}

		if (dialog == null) {
			WorldFrame frame = getWorldFrame();
			dialog = new PropertiesDialog(frame, this);

			// dialog.pack();
			
		}
	}

	public void saveProperties() {
		Util.saveObject(properties, getDefaultPropertiesName());
	}

	public void setIcon(Icon icon) {
		if (this.icon != null) {

			this.icon.removeFromParent();
		}

		this.icon = icon;

		this.addChild(icon);
		icon.setDraggable(false);
		icon.setPickable(false);

		setBounds(getFullBounds());

	}

	public void setProperty(String name, Object value) {
		properties.addAttribute(name, value);
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

	}

	protected abstract E createProxy();

	
	/*
	 * Updates the drawing of the proxyObject 
	 */
	protected void updateSymbol() {
	

	}

	public boolean accept(LineEnd lineEnd) {
		if (lineIn != null) {
			return lineIn.accept(lineEnd);
		} else
			return false;

	}

	// public Vector<PropertyWrapper> getProperties() {
	// return properties;
	// }

	@Override
	public WorldObject getControls() {
		// TODO Auto-generated method stub
		return new NodeControls(this);
	}

	public void configurationCancelled() {
		this.removeFromParent();

	}

	public void configurationComplete() {
		proxy = createProxy();

		updateSymbol();

		saveProperties();
	}

	public String getName() {
		// TODO Auto-generated method stub
		return getIcon().getName();
	}

}

class NodeControls extends WorldObject {
	ProxyGeneric proxyNode;

	public NodeControls(ProxyGeneric proxyNode) {
		super();

		this.proxyNode = proxyNode;

		init();

		// addChild(tag );

		// this.setBounds(getFullBounds());
	}

	public void init() {
		PText tag = new PText(proxyNode.getIcon().getName() + " Proxy");
		tag.setTextPaint(GDefaults.FOREGROUND_COLOR);
		tag.setFont(GDefaults.LARGE_FONT);

		this.setDraggable(false);
		addToLayout(tag);
		this.setChildrenPickable(false);

		PropertySchema[] propertyTypes = proxyNode.getMetaProperties();
		if (propertyTypes != null) {

			StringBuilder strBd = new StringBuilder(200);

			for (int i = 0; i < propertyTypes.length; i++) {
				PropertySchema type = propertyTypes[i];
				strBd.append(type.getName() + ": "
						+ proxyNode.getProperty(type.getName()).toString()
						+ "\n");

			}
			GText propertyText = new GText(strBd.toString());
			addToLayout(propertyText);

		} else {
			GText propertyText = new GText("No properties");
			addToLayout(propertyText);
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
