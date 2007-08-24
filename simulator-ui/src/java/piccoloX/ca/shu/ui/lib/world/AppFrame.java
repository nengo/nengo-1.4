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
import javax.swing.JOptionPane;
import javax.swing.border.EtchedBorder;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableActionManager;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.Grid;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.UIEnvironment;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PUtil;

/**
 * This class is based on PFrame by Jesse Grosjean
 * 
 * @author Shu Wu
 */
public abstract class AppFrame extends JFrame {
	public static final String TIPS = "<B>Keyboard Shortcuts</B><BR>"
			+ "Hold down Ctrl key to view tooltips at any time"
			+ "<BR><BR><B>Mouse Shortcuts</B><BR>"
			+ "Zooming: scroll the Mouse Wheel or Right Click and Drag";

	/**
	 * 
	 */
	private static final long serialVersionUID = 2769082313231407201L;

	private EventListener escapeFullScreenModeListener;

	private final GraphicsDevice graphicsDevice;

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

	public AppFrame(String title) {
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
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		canvas = new GCanvas(this);
		canvas.createWorld();
		canvas.setFocusable(true);

		getContentPane().add(canvas);
		canvas.requestFocus();
		validate();
		setFullScreenMode(false);

	}

	/**
	 * This method adds a key listener that will take this PFrame out of full
	 * screen mode when the escape key is pressed. This is called for you
	 * automatically when the frame enters full screen mode.
	 */
	public void addEscapeFullScreenModeListener() {
		removeEscapeFullScreenModeListener();
		escapeFullScreenModeListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent aEvent) {
				if (aEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
					setFullScreenMode(false);
				}
			}
		};
		canvas.addKeyListener((KeyListener) escapeFullScreenModeListener);
	}

	public abstract String getAboutString();

	public ReversableActionManager getActionManager() {
		return actionManager;
	}

	public abstract String getAppName();

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

		initApplicationMenu(menuBar);

		// menu.setMnemonic(KeyEvent.VK_V);

		editMenu = new MenuBuilder("Edit");
		menuBar.add(editMenu.getJMenu());

		worldMenu = new MenuBuilder("World");
		menuBar.add(worldMenu.getJMenu());

		menuBar.setVisible(true);
		updateWorldMenu();
		updateEditMenu();

		MenuBuilder helpMenu = new MenuBuilder("Help");
		menuBar.add(helpMenu.getJMenu());
		helpMenu.addAction(new TipsAction("Tips"));
		helpMenu.addAction(new AboutAction("About"));

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

	protected PCamera createDefaultCamera() {
		return PUtil.createBasicScenegraph();
	}

	@SuppressWarnings("unchecked")
	protected DisplayMode getBestDisplayMode(GraphicsDevice device) {
		Iterator itr = getPreferredDisplayModes(device).iterator();
		while (itr.hasNext()) {
			DisplayMode each = (DisplayMode) itr.next();
			DisplayMode[] modes = device.getDisplayModes();
			for (DisplayMode element : modes) {
				if (element.getWidth() == each.getWidth()
						&& element.getHeight() == each.getHeight()
						&& element.getBitDepth() == each.getBitDepth()) {
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

	/**
	 * Use this function to add menu items to the frame menu bar
	 * 
	 * @param menuBar
	 *            is attached to the frame
	 */
	protected void initApplicationMenu(JMenuBar menuBar) {

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
		worldMenu.addAction(new MinimizeAllWindows());

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

		MenuBuilder debugMenu = worldMenu.createSubMenu("Debug");

		if (!PDebug.debugPrintUsedMemory) {
			debugMenu.addAction(new ShowDebugMemory());
		} else {
			debugMenu.addAction(new HideDebugMemory());
		}
	}

	protected void windowClosing() {
		savePreferences();
		System.exit(0);
	}

	class AboutAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public AboutAction(String actionName) {
			super("About", actionName);
		}

		@Override
		protected void action() throws ActionException {
			JLabel editor = new JLabel("<html>" + getAboutString() + "</html>");
			JOptionPane.showMessageDialog(UIEnvironment.getInstance(), editor,
					"About " + getAppName(), JOptionPane.PLAIN_MESSAGE);
		}

	}

	/**
	 * 
	 * @param object
	 *            Object to be added to NeoGraphics
	 * @return the object being added
	 */
	public WorldObject addWorldObject(WorldObject object) {
		getWorld().getGround().catchObject(object);
		return object;
	}

	class HideDebugMemory extends StandardAction {

		private static final long serialVersionUID = 1L;

		public HideDebugMemory() {
			super("Stop printing Memory Used to console");
		}

		@Override
		protected void action() throws ActionException {
			PDebug.debugPrintUsedMemory = false;
			updateWorldMenu();
		}

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

	class MinimizeAllWindows extends StandardAction {

		private static final long serialVersionUID = 1L;

		public MinimizeAllWindows() {
			super("Minimize all windows");
		}

		@Override
		protected void action() throws ActionException {
			getWorld().minimizeAllWindows();

		}

	}

	class MyWindowListener implements WindowListener {

		public void windowActivated(WindowEvent arg0) {
		}

		public void windowClosed(WindowEvent arg0) {

		}

		public void windowClosing(WindowEvent arg0) {
			AppFrame.this.windowClosing();
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

	class ShowDebugMemory extends StandardAction {

		private static final long serialVersionUID = 1L;

		public ShowDebugMemory() {
			super("Print Memory Used to console");
		}

		@Override
		protected void action() throws ActionException {
			PDebug.debugPrintUsedMemory = true;
			updateWorldMenu();
		}

	}

	class TipsAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public TipsAction(String actionName) {
			super("Show UI tips", actionName);
		}

		@Override
		protected void action() throws ActionException {
			JLabel editor = new JLabel("<html>" + TIPS + "</html>");
			JOptionPane.showMessageDialog(UIEnvironment.getInstance(), editor,
					"NeoGraphics Tips", JOptionPane.PLAIN_MESSAGE);
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
	public void apply(AppFrame parent) {
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