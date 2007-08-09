package ca.shu.ui.lib.world;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
import ca.shu.ui.lib.actions.ReversableActionManager;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.Window;
import ca.shu.ui.lib.util.Grid;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.UIEnvironment;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PRoot;
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

	private UserPreferences preferences;

	private JLabel statusBar;

	ReversableActionManager actionManager;

	PCamera camera;

	GCanvas canvas;

	MenuBuilder editMenu;

	String statusStr = "";

	Vector<String> taskStatusStrings = new Vector<String>();

	PLayer topLayer;

	MenuBuilder worldMenu;

	public GFrame(String title) {
		super(title, GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration());
		loadPreferences();
		UIEnvironment.setInstance(this);

		actionManager = new ReversableActionManager(this);
		getContentPane().setLayout(new BorderLayout());
		initMenu();
		initStatusBar();

		// System.out.println("constructing WorldFrame");
		graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();

		setBounds(new Rectangle(100, 100, 800, 600));
		setBackground(null);
		addWindowListener(new MyWindowListener());

		try {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} catch (SecurityException e) {
		} // expected from applets

		canvas = new GCanvas(this);
		canvas.createWorld();

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

	public ReversableActionManager getActionManager() {
		return actionManager;
	}

	public GCanvas getCanvas() {
		return canvas;
	}

	public PRoot getRoot() {
		return canvas.getRoot();
	}

	public String getUserFileDirectory() {
		return "UI";
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

	public void reversableActionsUpdated() {
		updateEditMenu();
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

	/**
	 * Initializes the menu
	 */
	private void initMenu() {

		JMenuBar menuBar = new JMenuBar();
		Style.applyStyleToComponent(menuBar);

		constructMenu(menuBar);

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

	/**
	 * Use this function to add menu items to the frame menu bar
	 * 
	 * @param menuBar
	 *            is attached to the frame
	 */
	protected void constructMenu(JMenuBar menuBar) {

	}

	protected PCamera createDefaultCamera() {
		return PUtil.createBasicScenegraph();
	}

	@SuppressWarnings("unchecked")
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
	@SuppressWarnings("unchecked")
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

	// protected JMenu addMenu(JMenuBar menuBar, String name) {
	// JMenu menu = new JMenu(name);
	// Style.styleComponent(menu);
	// menuBar.add(menu);
	// return menu;
	// }

	protected void loadPreferences() {
		File preferencesFile = new File(getUserFileDirectory(), "userSettings");

		if (preferencesFile.exists()) {
			FileInputStream fis;
			try {
				fis = new FileInputStream(preferencesFile);

				ObjectInputStream ois = new ObjectInputStream(fis);
				try {
					preferences = (UserPreferences) ois.readObject();
				} catch (ClassNotFoundException e) {
					System.out.println("Could not load preferences");
				}
			} catch (IOException e1) {
				System.out.println("Could not read preferences file");
			}
		}

		if (preferences == null) {
			preferences = new UserPreferences();

		}
		preferences.apply(this);
	}

	protected void savePreferences() {
		File file = new File(getUserFileDirectory());
		if (!file.exists())
			file.mkdir();

		File preferencesFile = new File(getUserFileDirectory(), "userSettings");

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);

			oos.writeObject(preferences);

			FileOutputStream fos = new FileOutputStream(preferencesFile);
			fos.write(bos.toByteArray());
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void updateEditMenu() {
		editMenu.reset();

		editMenu.addAction(new UndoAction());
		editMenu.addAction(new RedoAction());

	}

	protected void updateStatusBar() {
		StringBuilder strBuff = new StringBuilder("<HTML>");
		if (taskStatusStrings.size() > 0) {
			strBuff.append("*** Messages ***<BR>");

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

	protected void updateWorldMenu() {
		worldMenu.reset();

		if (!isFullScreenMode) {
			worldMenu.addAction(new TurnOnFullScreen());
		} else {
			worldMenu.addAction(new TurnOffFullScreen());
		}

		if (!preferences.isEnableTooltips()) {
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

	class MyWindowListener implements WindowListener {

		public void windowActivated(WindowEvent arg0) {
		}

		public void windowClosed(WindowEvent arg0) {

		}

		public void windowClosing(WindowEvent arg0) {
			savePreferences();
		}

		public void windowDeactivated(WindowEvent arg0) {
		}

		public void windowDeiconified(WindowEvent arg0) {
		}

		public void windowIconified(WindowEvent arg0) {
		}

		public void windowOpened(WindowEvent arg0) {
		}

	}

	class RedoAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public RedoAction() {
			super("Redo: " + actionManager.getRedoActionDescription());
			if (!actionManager.canRedo()) {
				setEnabled(false);
			}
		}

		@Override
		protected void action() throws ActionException {
			actionManager.redoAction();

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
			preferences.setGridVisible(false);
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
			preferences.setEnableTooltips(false);
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
			preferences.setGridVisible(true);

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
			preferences.setEnableTooltips(true);
			updateWorldMenu();
		}

	}

	class UndoAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public UndoAction() {
			super("Undo: " + actionManager.getUndoActionDescription());
			if (!actionManager.canUndo()) {
				setEnabled(false);
			}
		}

		@Override
		protected void action() throws ActionException {
			actionManager.undoAction();
		}
	}

}

class UserPreferences implements Serializable {

	private static final long serialVersionUID = 1L;
	private boolean enableTooltips = true;
	private boolean gridVisible = true;

	/*
	 * Apply preferences
	 */
	public void apply(GFrame parent) {
		setEnableTooltips(enableTooltips);
		setGridVisible(gridVisible);
	}

	public boolean isEnableTooltips() {
		return enableTooltips;
	}

	public boolean isGridVisible() {
		return gridVisible;
	}

	public void setEnableTooltips(boolean enableTooltips) {
		this.enableTooltips = enableTooltips;
		World.setTooltipsVisible(this.enableTooltips);
	}

	public void setGridVisible(boolean gridVisible) {
		this.gridVisible = gridVisible;
		Grid.setGridVisible(gridVisible);
	}

}