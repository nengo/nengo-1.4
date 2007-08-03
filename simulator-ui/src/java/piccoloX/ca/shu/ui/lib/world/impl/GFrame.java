package ca.shu.ui.lib.world.impl;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.border.EtchedBorder;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ActionManager;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.Window;
import ca.shu.ui.lib.util.Grid;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.world.World;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PUtil;

/**
 * This class is based on PFrame by Jesse Grosjean
 * 
 * @author Shu Wu
 */
public class GFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2769082313231407201L;

	private EventListener escapeFullScreenModeListener;

	private GraphicsDevice graphicsDevice;

	private boolean isFullScreenMode;

	private JLabel statusBar;

	PCamera camera;

	GCanvas canvas;

	String statusStr = "";

	Vector<String> taskStatusStrings = new Vector<String>();

	PLayer topLayer;

	ActionManager actionManager;

	public GFrame(String title) {
		super(title, GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration());

		actionManager = new ActionManager(this);
		getContentPane().setLayout(new BorderLayout());
		initMenu();
		initStatusBar();

		// System.out.println("constructing WorldFrame");
		graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();

		setBounds(new Rectangle(100, 100, 800, 600));
		setBackground(null);

		try {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} catch (SecurityException e) {
		} // expected from applets

		canvas = new GCanvas(this);

		getContentPane().add(canvas);
		canvas.requestFocus();
		validate();
		setFullScreenMode(false);

	}

	public void addChild(Window wo) {

		canvas.addWindow(wo);
	}

	/**
	 * This method adds a key listener that will take this PFrame out of full
	 * screen mode when the escape key is pressed. This is called for you
	 * automatically when the frame enters full screen mode.
	 */
	public void addEscapeFullScreenModeListener() {
		removeEscapeFullScreenModeListener();
		escapeFullScreenModeListener = new KeyAdapter() {
			public void keyPressed(KeyEvent aEvent) {
				if (aEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
					setFullScreenMode(false);
				}
			}
		};
		canvas.addKeyListener((KeyListener) escapeFullScreenModeListener);
	}

	/**
	 * Use this function to add menu items to the frame menu bar
	 * 
	 * @param menuBar
	 *            is attached to the frame
	 */
	public void constructMenuBar(JMenuBar menuBar) {

	}

	public GCanvas getCanvas() {
		return canvas;
	}

	/**
	 * @return the top-most World associated with this frame
	 */
	public World getWorld() {
		return canvas.getWorld();
	}

	public void popTaskStatusStr(String str) {
		taskStatusStrings.remove(str);
		updateStatusBar();
	}

	public String pushTaskStatusStr(String str) {
		taskStatusStrings.add(str);
		updateStatusBar();
		return str;
	}

	/**
	 * This method removes the escape full screen mode key listener. It will be
	 * called for you automatically when full screen mode exits, but the method
	 * has been made public for applications that wish to use other methods for
	 * exiting full screen mode.
	 */
	public void removeEscapeFullScreenModeListener() {
		if (escapeFullScreenModeListener != null) {
			canvas
					.removeKeyListener((KeyListener) escapeFullScreenModeListener);
			escapeFullScreenModeListener = null;
		}
	}

	/*
	 * @param fullScreenMode sets the screen to fullscreen
	 */
	public void setFullScreenMode(boolean fullScreenMode) {
		this.isFullScreenMode = fullScreenMode;
		if (fullScreenMode) {
			addEscapeFullScreenModeListener();

			if (isDisplayable()) {
				dispose();
			}

			setUndecorated(true);
			setResizable(false);
			graphicsDevice.setFullScreenWindow(this);

			if (graphicsDevice.isDisplayChangeSupported()) {
				chooseBestDisplayMode(graphicsDevice);
			}
			validate();
		} else {
			removeEscapeFullScreenModeListener();

			if (isDisplayable()) {
				dispose();
			}

			setUndecorated(false);
			setResizable(true);
			graphicsDevice.setFullScreenWindow(null);
			validate();
			setVisible(true);
		}
	}

	/**
	 * @param text
	 *            Sets the text of the status bar in the UI
	 */
	public void setStatusStr(String text) {
		statusStr = text;
		updateStatusBar();
	}

	MenuBuilder worldMenu;
	MenuBuilder editMenu;

	/**
	 * Initializes the menu
	 */
	private void initMenu() {

		JMenuBar menuBar = new JMenuBar();
		Style.applyStyleToComponent(menuBar);

		constructMenuBar(menuBar);

		// menu.setMnemonic(KeyEvent.VK_V);

		editMenu = new MenuBuilder("Edit");
		menuBar.add(editMenu.getJMenu());

		worldMenu = new MenuBuilder("World");
		menuBar.add(worldMenu.getJMenu());

		menuBar.setVisible(true);
		updateWorldMenu();
		updateEditMenu();
		this.setJMenuBar(menuBar);
		this.repaint();

	}

	public void reversableActionsUpdated() {
		updateEditMenu();
	}

	protected void updateEditMenu() {
		editMenu.reset();

		editMenu.addAction(new UndoAction());
		editMenu.addAction(new RedoAction());

	}

	class UndoAction extends StandardAction {

		public UndoAction() {
			super("Undo: " + actionManager.getLastActionDescription());
			if (!actionManager.hasReversableAction()) {
				setEnabled(false);
			}
		}

		private static final long serialVersionUID = 1L;

		@Override
		protected void action() throws ActionException {
			actionManager.undoLastAction();
		}
	}

	class RedoAction extends StandardAction {

		public RedoAction() {
			super("Redo: " + actionManager.getLastActionDescription());
			if (!actionManager.hasReversableAction()) {
				setEnabled(false);
			}
		}

		private static final long serialVersionUID = 1L;

		@Override
		protected void action() throws ActionException {
			actionManager.redoLastAction();

		}

	}

	protected void updateWorldMenu() {
		worldMenu.reset();

		worldMenu.addAction(new ZoomOutAction());
		if (!isFullScreenMode) {
			worldMenu.addAction(new TurnOnFullScreen());
		} else {
			worldMenu.addAction(new TurnOffFullScreen());
		}

		if (!WorldImpl.isContexualTipsVisible()) {
			worldMenu.addAction(new TurnOnTooltips());
		} else {
			worldMenu.addAction(new TurnOffTooltips());
		}

		if (!Grid.isGridVisible()) {
			worldMenu.addAction(new TurnOnGrid());
		} else {
			worldMenu.addAction(new TurnOffGrid());
		}

		MenuBuilder qualityMenu = worldMenu.createSubMenu("Rendering Quality");
		qualityMenu.addAction(new LowQualityAction());
		qualityMenu.addAction(new MediumQualityAction());
		qualityMenu.addAction(new HighQualityAction());
	}

	// protected JMenu addMenu(JMenuBar menuBar, String name) {
	// JMenu menu = new JMenu(name);
	// Style.styleComponent(menu);
	// menuBar.add(menu);
	// return menu;
	// }

	/*
	 * Initializes the status bar
	 */
	private void initStatusBar() {

		statusBar = new JLabel("welcome to NeoWorld");
		statusBar.setOpaque(true);
		statusBar.setBackground(Style.COLOR_BACKGROUND);
		statusBar.setForeground(Style.COLOR_FOREGROUND);
		statusBar.setBorder(new EtchedBorder());

		Container c = super.getContentPane();

		c.add(statusBar, BorderLayout.SOUTH);

	}

	protected void chooseBestDisplayMode(GraphicsDevice device) {
		DisplayMode best = getBestDisplayMode(device);
		if (best != null) {
			device.setDisplayMode(best);
		}
	}

	protected PCamera createDefaultCamera() {
		return PUtil.createBasicScenegraph();
	}

	protected DisplayMode getBestDisplayMode(GraphicsDevice device) {
		Iterator itr = getPreferredDisplayModes(device).iterator();
		while (itr.hasNext()) {
			DisplayMode each = (DisplayMode) itr.next();
			DisplayMode[] modes = device.getDisplayModes();
			for (int i = 0; i < modes.length; i++) {
				if (modes[i].getWidth() == each.getWidth()
						&& modes[i].getHeight() == each.getHeight()
						&& modes[i].getBitDepth() == each.getBitDepth()) {
					return each;
				}
			}
		}

		return null;
	}

	/**
	 * By default return the current display mode. Subclasses may override this
	 * method to return other modes in the collection.
	 */
	protected Collection getPreferredDisplayModes(GraphicsDevice device) {
		ArrayList<DisplayMode> result = new ArrayList<DisplayMode>();

		result.add(device.getDisplayMode());
		/*
		 * result.add(new DisplayMode(640, 480, 32, 0)); result.add(new
		 * DisplayMode(640, 480, 16, 0)); result.add(new DisplayMode(640, 480,
		 * 8, 0));
		 */

		return result;
	}

	protected void updateStatusBar() {
		StringBuilder strBuff = new StringBuilder("<HTML>");
		if (taskStatusStrings.size() > 0) {
			strBuff.append("*** Tasks ***<BR>");

			Iterator<String> taskIt = taskStatusStrings.iterator();
			while (taskIt.hasNext()) {
				strBuff.append(taskIt.next() + "<BR>");
			}
			strBuff.append("<BR>");

		}
		strBuff.append(statusStr);
		strBuff.append("</HTML>");

		statusBar.setText(strBuff.toString());
	}

	class HighQualityAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public HighQualityAction() {
			super("High Quality");
		}

		@Override
		protected void action() throws ActionException {
			getCanvas().setDefaultRenderQuality(
					PPaintContext.HIGH_QUALITY_RENDERING);
			getCanvas().setAnimatingRenderQuality(
					PPaintContext.HIGH_QUALITY_RENDERING);
			getCanvas().setInteractingRenderQuality(
					PPaintContext.HIGH_QUALITY_RENDERING);
			updateWorldMenu();
		}

	}

	class LowQualityAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public LowQualityAction() {
			super("Low Quality");
		}

		@Override
		protected void action() throws ActionException {
			getCanvas().setDefaultRenderQuality(
					PPaintContext.LOW_QUALITY_RENDERING);
			getCanvas().setAnimatingRenderQuality(
					PPaintContext.LOW_QUALITY_RENDERING);
			getCanvas().setInteractingRenderQuality(
					PPaintContext.LOW_QUALITY_RENDERING);
			updateWorldMenu();
		}

	}

	class MediumQualityAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public MediumQualityAction() {
			super("Medium Quality");
		}

		@Override
		protected void action() throws ActionException {
			getCanvas().setDefaultRenderQuality(
					PPaintContext.HIGH_QUALITY_RENDERING);
			getCanvas().setAnimatingRenderQuality(
					PPaintContext.LOW_QUALITY_RENDERING);
			getCanvas().setInteractingRenderQuality(
					PPaintContext.LOW_QUALITY_RENDERING);
			updateWorldMenu();
		}

	}

	class TurnOffFullScreen extends StandardAction {

		private static final long serialVersionUID = 1L;

		public TurnOffFullScreen() {
			super("Full screen off");
		}

		@Override
		protected void action() throws ActionException {
			setFullScreenMode(false);
			updateWorldMenu();
		}

	}

	class TurnOffGrid extends StandardAction {

		private static final long serialVersionUID = 1L;

		public TurnOffGrid() {
			super("Grid off");

		}

		@Override
		protected void action() throws ActionException {
			Grid.setGridVisible(false);
			updateWorldMenu();
		}

	}

	class TurnOffTooltips extends StandardAction {

		private static final long serialVersionUID = 1L;

		public TurnOffTooltips() {
			super("Tooltips off");
		}

		@Override
		protected void action() throws ActionException {
			WorldImpl.setContexualTipsVisible(false);
			updateWorldMenu();
		}

	}

	class TurnOnFullScreen extends StandardAction {

		private static final long serialVersionUID = 1L;

		public TurnOnFullScreen() {
			super("Full screen on");
		}

		@Override
		protected void action() throws ActionException {
			setFullScreenMode(true);
			updateWorldMenu();
		}

	}

	class TurnOnGrid extends StandardAction {

		private static final long serialVersionUID = 1L;

		public TurnOnGrid() {
			super("Grid on");
		}

		@Override
		protected void action() throws ActionException {
			Grid.setGridVisible(true);
			updateWorldMenu();
		}

	}

	class TurnOnTooltips extends StandardAction {

		private static final long serialVersionUID = 1L;

		public TurnOnTooltips() {
			super("Tooltips on");
		}

		@Override
		protected void action() throws ActionException {
			WorldImpl.setContexualTipsVisible(true);
			updateWorldMenu();
		}

	}

	class ZoomOutAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public ZoomOutAction() {
			super("Fit on screen");
		}

		@Override
		protected void action() throws ActionException {
			getWorld().fitOnScreen();
			updateWorldMenu();
		}

	}

	public ActionManager getActionManager() {
		return actionManager;
	}
}
