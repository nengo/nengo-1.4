package ca.shu.ui.lib.util;

import ca.shu.ui.lib.actions.ReversableActionManager;
import ca.shu.ui.lib.world.GFrame;

public class UIEnvironment {
	public static final double SEMANTIC_ZOOM_LEVEL = 0.2;

	static GFrame uiInstance;
	
	public static ReversableActionManager getActionManager() {
		return getInstance().getActionManager();
	}

	/**
	 * 
	 * @return UI Instance
	 */
	public static GFrame getInstance() {
		return uiInstance;
	}

	/**
	 * 
	 * 
	 * @param instance
	 *            UI Instance
	 */
	public static void setInstance(GFrame instance) {

		/*
		 * Only one instance of NeoWorld may be running at once
		 */
		if (uiInstance != null) {
			throw new RuntimeException(
					"Only one instance of GFrame may be running in one environment.");
		}

		uiInstance = instance;
	}

}
