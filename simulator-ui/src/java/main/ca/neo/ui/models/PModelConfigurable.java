package ca.neo.ui.models;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.text.SimpleAttributeSet;

import ca.neo.ui.NeoGraphics;
import ca.neo.ui.style.Style;
import ca.neo.ui.views.objects.configurable.IConfigurable;
import ca.neo.ui.views.objects.configurable.UIConfigManager;
import ca.neo.ui.views.objects.configurable.managers.IConfigurationManager;
import ca.neo.ui.views.objects.configurable.struct.PropertyStructure;
import ca.shu.ui.lib.objects.GText;
import ca.shu.ui.lib.objects.widgets.TrackedTask;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolo.nodes.PText;

public abstract class PModelConfigurable extends PModel implements
		IConfigurable {
	public PModelConfigurable() {
		super();
		// TODO Auto-generated constructor stub
	}

	private JDialog dialog;
	SimpleAttributeSet properties = new SimpleAttributeSet();

	/**
	 * @param useDefaultConfigManager
	 *            uses the default configuration manager
	 */
	public PModelConfigurable(boolean useDefaultConfigManager) {
		if (useDefaultConfigManager) {
			startConfigManager(getDefaultConfigManager());
		}

	}

	/**
	 * @return The default configuration manager
	 */
	protected IConfigurationManager getDefaultConfigManager() {
		return new UIConfigManager(NeoGraphics.getInstance());
	}

	/**
	 * Create a model using any IConfigurationManager
	 * 
	 * @param configManager
	 */
	public PModelConfigurable(IConfigurationManager configManager) {
		super();

		startConfigManager(configManager);
	}

	/**
	 * Starts the configuration manager
	 * 
	 * @param configManager
	 *            The configuration manager used to configure this model
	 */
	protected void startConfigManager(IConfigurationManager configManager) {
		if (getPropertiesSchema() == null || getPropertiesSchema().length == 0) {
			Util.Error("No properties to configure");
		}
		configManager.configure(this);

		// configureModel();

		/*
		 * Wait to see if the model has been created
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

		// if (model == null) {
		// Util.Error("Error creating model");
		// }

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

	// public void configureModel() {
	//
	// if (dialog == null) {
	//
	// Frame frame = NeoWorld.getInstance();
	// dialog = new UserConfigManager(frame, PModelConfigurable.this);
	//
	// }
	// }

	public abstract PropertyStructure[] getPropertiesSchema();

	public Object getProperty(String name) {
		return properties.getAttribute(name);
	}

	public Object getProperty(PropertyStructure prop) {
		return properties.getAttribute(prop.getName());
	}

	@Override
	public WorldObjectImpl getTooltipObject() {
		// TODO Auto-generated method stub
		return new NodeTooltip(this);
	}

	public void loadPropertiesFromFile(String fileName) {
		SimpleAttributeSet loadedProperties = (SimpleAttributeSet) Util
				.loadProperty(this, fileName);

		if (loadedProperties != null) {
			properties = loadedProperties;
		} else {
			Util.Error("Could not load file: " + fileName);
		}
	}

	// public String[] getPropertyFiles() {
	//		
	// }

	public void savePropertiesToFile(String fileName) {
		Util.saveProperty(this, properties, fileName);

		// saveStatic(properties, fileName);
	}

	public void deletePropretiesFile(String fileName) {
		Util.deleteProperty(this, fileName);
	}

	public void setProperty(String name, Object value) {
		properties.addAttribute(name, value);
	}

	public void setProperty(PropertyStructure propertySchema, Object value) {
		setProperty(propertySchema.getName(), value);
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
		TrackedTask task = new TrackedTask("Creating " + getName() + " ("
				+ getTypeName() + ")");

		setModel(createModel());
		task.finished();
	}

	// @Override
	// public PopupMenuBuilder constructMenu() {
	//
	// PopupMenuBuilder menu = super.constructMenu();
	//
	// menu.addSection("Configuration");
	// menu.addAction(new SaveAction());
	//
	// return menu;
	// }

	class SaveAction extends AbstractAction {

		public SaveAction() {
			super("Save");
			// TODO Auto-generated constructor stub
		}

		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent arg0) {
			String name = JOptionPane.showInputDialog("Name:");

			savePropertiesToFile(name);
		}

	}

	public String[] getPropertyFiles() {
		// TODO Auto-generated method stub
		return Util.getPropertyFiles(this);
	}

}

class NodeTooltip extends WorldObjectImpl {
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

			PropertyStructure[] propertyTypes = proxyNode.getPropertiesSchema();
			if (propertyTypes != null) {

				StringBuilder strBd = new StringBuilder(200);

				for (int i = 0; i < propertyTypes.length; i++) {
					PropertyStructure type = propertyTypes[i];
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
