package ca.neo.ui.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.text.SimpleAttributeSet;

import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.ui.NeoWorld;
import ca.neo.ui.style.Style;
import ca.neo.ui.views.objects.properties.IConfigurable;
import ca.neo.ui.views.objects.properties.PropertiesDialog;
import ca.neo.ui.views.objects.properties.PropertySchema;
import ca.shu.ui.lib.objects.GText;
import ca.shu.ui.lib.objects.lines.LineEnd;
import ca.shu.ui.lib.objects.lines.LineEndWell;
import ca.shu.ui.lib.objects.lines.LineIn;
import ca.shu.ui.lib.world.impl.Frame;
import ca.shu.ui.lib.world.impl.WorldObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

public abstract class PModelConfigurable extends PModel implements
		IConfigurable {
	private JDialog dialog;
	SimpleAttributeSet properties = new SimpleAttributeSet();

	public PModelConfigurable() {
		super();
		// addPropertyChangeListener(PNode.PROPERTY_FULL_BOUNDS,
		// new PropertyChangeListener() {
		// public void propertyChange(PropertyChangeEvent arg0) {
		// if (lineEndWell != null)
		// lineEndWell.layoutEdges();
		//
		// if (lineIn != null) {
		// lineIn.layoutEdges();
		// }
		// }
		// });

		configureModel();

		/*
		 * Wait until a change has been made to the model
		 */
		if (model == null && !configCancelled) {
			synchronized (modelConfigurationLock) {
				try {
					modelConfigurationLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	protected Object modelConfigurationLock = new Object();

	boolean configCancelled = false;
	public void cancelConfiguration() {
		removeFromParent();
		configCancelled = true;
		synchronized (modelConfigurationLock) {
			modelConfigurationLock.notifyAll();
		}
	}

	public void completeConfiguration() {
		initModel();
		synchronized (modelConfigurationLock) {
			modelConfigurationLock.notifyAll();
		}
	}

	public void configureModel() {

		if (getPropertiesSchema() == null || getPropertiesSchema().length == 0) {
			completeConfiguration();
			return;
		}

		if (dialog == null) {

			Frame frame = NeoWorld.getInstance();
			dialog = new PropertiesDialog(frame, PModelConfigurable.this);

		}
	}

	// LineIn lineIn;
	//
	// LineEndWell lineEndWell;
	public abstract PropertySchema[] getPropertiesSchema();

	public Object getProperty(String name) {
		return properties.getAttribute(name);
	}

	@Override
	public WorldObject getTooltipObject() {
		// TODO Auto-generated method stub
		return new NodeTooltip(this);
	}

	public void loadPropertiesFromFile(String fileName) {
		SimpleAttributeSet loadedProperties = (SimpleAttributeSet) loadStatic(fileName);

		if (loadedProperties != null) {
			properties = loadedProperties;
		}
	}

	public void savePropertiesToFile(String fileName) {
		saveStatic(properties, "defaultProperties");
	}

	public void setProperty(String name, Object value) {
		properties.addAttribute(name, value);
	}

	protected abstract Object createModel();

	// public boolean connect(LineEnd lineEnd) {
	// if (lineIn != null) {
	// return lineIn.connect(lineEnd);
	// } else
	// return false;
	//
	// }
	//
	// public void disconnect() {
	// if (lineIn != null) {
	// lineIn.disconnect();
	// }
	//
	// }

	// @Override
	// protected void updateSymbol() {
	// super.updateSymbol();
	//
	// // if (lineEndWell == null) {
	// // lineEndWell = new LineEndWell();
	// //
	// // lineEndWell.setOffset(icon.getBounds().getMaxY() + 5, 0);
	// // addChild(lineEndWell);
	// //
	// // }
	// //
	// // if (lineIn == null) {
	// // lineIn = new LineIn();
	// //
	// // lineIn.setOffset(icon.getBounds().getMinY() - 15
	// // - lineIn.getWidth(), 0);
	// // addChild(lineIn);
	// // }
	// //
	// // setBounds(parentToLocal(getFullBounds()));
	//
	// }

	// @Override
	// public PropertySchema[] getPropertiesSchema() {
	// // TODO Auto-generated method stub
	// return null;
	// }

	// public Origin getOrigin(String name) {
	// return null;
	// // return getModelNode().getOrigins();
	// }

	protected void initModel() {
		setModel(createModel());

		updateSymbol();

	}

}

class NodeTooltip extends WorldObject {
	private static final long serialVersionUID = 1L;

	PModelConfigurable proxyNode;

	public NodeTooltip(PModelConfigurable proxyNode) {
		super();

		this.proxyNode = proxyNode;

		init();

		// addChild(tag );

		// this.setBounds(getFullBounds());
	}

	public void init() {
		PText tag = new PText(proxyNode.getIcon().getName());
		tag.setTextPaint(Style.FOREGROUND_COLOR);
		tag.setFont(Style.FONT_LARGE);

		// this.setDraggable(false);
		addToLayout(tag);
		// this.setChildrenPickable(false);

		if (proxyNode.isModelCreated()) {

			PropertySchema[] propertyTypes = proxyNode.getPropertiesSchema();
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
	}

}
