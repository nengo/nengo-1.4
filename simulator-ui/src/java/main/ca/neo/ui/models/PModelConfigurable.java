package ca.neo.ui.models;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import ca.neo.ui.style.Style;
import ca.neo.ui.views.objects.configurable.IConfigurable;
import ca.neo.ui.views.objects.configurable.managers.PropertySet;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;
import ca.shu.ui.lib.objects.widgets.TrackedActivity;
import ca.shu.ui.lib.objects.widgets.TrackedStatusMsg;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This abstract class implements IConfigurable, which allows it to be
 * configured by any Configuration manager
 * 
 * @author Shu Wu
 * 
 */
public abstract class PModelConfigurable extends PModel implements
		IConfigurable {

	/**
	 * Default constructor
	 */
	public PModelConfigurable() {
		super();
		init();
	}

	public PModelConfigurable(Object model) {
		super(model);
		init();
	}

	public void cancelConfiguration() {
		removeFromParent();

	}

	/*
	 * (non-Javadoc) This function can be safely called from any thread
	 * 
	 * @see ca.neo.ui.views.objects.configurable.IConfigurable#completeConfiguration(ca.neo.ui.views.objects.configurable.managers.PropertySet)
	 */
	public void completeConfiguration(PropertySet properties) {

		Object model = configureModel(properties);

		try {
			SwingUtilities.invokeAndWait(new CompleteConfiguration(model));
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	class CompleteConfiguration implements Runnable {
		Object model;

		public CompleteConfiguration(Object model) {
			super();
			this.model = model;
		}

		public void run() {
			setModel(model);
			afterModelCreated();

		}

	}

	public abstract PropDescriptor[] getConfigSchema();

	@Override
	public WorldObjectImpl getTooltipObject() {
		// TODO Auto-generated method stub
		return new NodeTooltip(this);
	}

	public boolean isConfigured() {
		if (getModel() != null) {
			return true;
		} else {
			return false;
		}
	}

	private void init() {
		// nothing to initialize yet
	}

	/**
	 * This function is called after the model has been created Some parts of
	 * the UI may be constructed here
	 */
	protected void afterModelCreated() {

	}

	/**
	 * WARNING: This function may be called from any Thread, so it is not safe
	 * to put UI stuff here If there's UI Stuff to be done, put it in
	 * afterModelCreated
	 * 
	 * Creates a model for the configuration process, called if a ConfigManager
	 * is used
	 * 
	 * @param configuredProperties
	 *            the configured properties
	 */
	protected Object configureModel(PropertySet configuredProperties) {
		return null;
	}

}

/*
 * TODO: print information about this node
 */
class NodeTooltip extends WorldObjectImpl {
	private static final long serialVersionUID = 1L;

	PModelConfigurable proxyNode;

	public NodeTooltip(PModelConfigurable proxyNode) {
		super();

		this.proxyNode = proxyNode;

		init();

	}

	public void init() {
		PText tag = new PText(proxyNode.getIcon().getName());
		tag.setTextPaint(Style.COLOR_FOREGROUND);
		tag.setFont(Style.FONT_LARGE);

		// this.setDraggable(false);
		addToLayout(tag);
		// this.setChildrenPickable(false);

		// if (proxyNode.isModelCreated()) {
		//
		// ConfigStruct[] propertyTypes = proxyNode.getConfigSchema();
		// if (propertyTypes != null) {
		//
		// StringBuilder strBd = new StringBuilder(200);
		//
		// for (int i = 0; i < propertyTypes.length; i++) {
		// ConfigStruct type = propertyTypes[i];
		// strBd.append(type.getName() + ": "
		// + proxyNode.getProperty(type.getName()).toString()
		// + "\n");
		//
		// }
		// GText propertyText = new GText(strBd.toString());
		// addToLayout(propertyText);
		//
		// } else {
		// GText propertyText = new GText("No properties");
		// addToLayout(propertyText);
		// }
		// }
	}

}
