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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableActionManager;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.menus.MenuBuilder;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PUtil;

/**
 * This class is based on PFrame by Jesse Grosjean
 * 
 * @author Shu Wu
 */
public abstract class AppFrame extends JFrame {
	private static final long serialVersionUID = 2769082313231407201L;

	/**
	 * A String which briefly describes some commands used in this application
	 */
	public static final String TIPS = "<B>*** Keyboard Shortcuts ***</B><BR>"
			+ "Press 's' to switch between Interaction modes<BR>"
			+ "Mouse over a node and hold down 'ctrl' to view tooltips<BR>"
			+ "<BR><B>*** Mouse Shortcuts ***</B><BR>"
			+ "Zooming: scroll the Mouse Wheel or Right Click and Drag";

	/**
	 * Name of the directory where UI Files are stored
	 */
	public static final String USER_FILE_DIR = "UIFiles";

	private ReversableActionManager actionManager;

	private Canvas canvas;

	private MenuBuilder editMenu;

	private EventListener escapeFullScreenModeListener;

	private final GraphicsDevice graphicsDevice;

	private JLabel interactionModeLabel;

	private boolean isFullScreenMode;

	private UserPreferences preferences;

	private boolean selectionModeEnabled;

	private JLabel statusMessageLabel;

	private JPanel statusPanel;

	private String statusStr = "";

	private JLabel taskMessagesLabel;

	private Vector<String> taskStatusStrings = new Vector<String>();

	private MenuBuilder worldMenu;

	/**
	 * @param title
	 *            Title of application
	 */
	public AppFrame(String title) {
		super(title, GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration());
		loadPreferences();
		UIEnvironment.setInstance(this);

		actionManager = new ReversableActionManager(this);
		getContentPane().setLayout(new BorderLayout());
		initMenu();
		initStatusPanel();

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

		canvas = new Canvas();
		canvas.createWorld();
		canvas.setFocusable(true);

		setSelectionMode(false);

		getContentPane().add(canvas);
		canvas.requestFocus();
		validate();
		setFullScreenMode(false);

	}

	/**
	 * Initializes the menu
	 */
	private void initMenu() {

		JMenuBar menuBar = new JMenuBar();

		Style.applyStyleToComponent(menuBar);
		initApplicationMenu(menuBar);

		editMenu = new MenuBuilder("Edit");
		editMenu.getJMenu().setMnemonic(KeyEvent.VK_E);
		menuBar.add(editMenu.getJMenu());

		worldMenu = new MenuBuilder("World");
		worldMenu.getJMenu().setMnemonic(KeyEvent.VK_W);
		menuBar.add(worldMenu.getJMenu());

		menuBar.setVisible(true);
		updateWorldMenu();
		updateEditMenu();

		MenuBuilder helpMenu = new MenuBuilder("Help");
		helpMenu.getJMenu().setMnemonic(KeyEvent.VK_H);
		menuBar.add(helpMenu.getJMenu());
		helpMenu.addAction(new TipsAction("Tips and Commands"), KeyEvent.VK_T);
		helpMenu.addAction(new AboutAction("About"), KeyEvent.VK_A);

		this.setJMenuBar(menuBar);
		this.repaint();

	}

	/**
	 * Initializes the status bar
	 */
	private void initStatusPanel() {

		statusMessageLabel = new JLabel("welcome to " + getAppName());
		statusMessageLabel.setForeground(Style.COLOR_FOREGROUND);

		taskMessagesLabel = new JLabel();
		taskMessagesLabel.setForeground(Style.COLOR_FOREGROUND);

		interactionModeLabel = new JLabel();
		interactionModeLabel.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				setSelectionMode(!isSelectionMode());
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}

		});
		interactionModeLabel.setForeground(Style.COLOR_FOREGROUND);

		statusPanel = new JPanel();
		statusPanel.setLayout(new BorderLayout());

		statusPanel.setBackground(Style.COLOR_BACKGROUND);
		statusPanel.setBorder(new EtchedBorder());

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.setBackground(null);
		bottomPanel.add(statusMessageLabel, BorderLayout.WEST);
		bottomPanel.add(interactionModeLabel, BorderLayout.EAST);

		statusPanel.add(taskMessagesLabel, BorderLayout.NORTH);
		statusPanel.add(bottomPanel, BorderLayout.SOUTH);

		Container c = super.getContentPane();
		taskMessagesLabel.setVisible(false);

		c.add(statusPanel, BorderLayout.SOUTH);

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

	/**
	 * Loads saved preferences related to the application
	 */
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

	/**
	 * Save preferences to file
	 */
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

	/**
	 * Updates the menu 'edit'
	 */
	protected void updateEditMenu() {
		editMenu.reset();

		editMenu.addAction(new UndoAction(), KeyEvent.VK_U);

		editMenu.addAction(new RedoAction(), KeyEvent.VK_R);

	}

	/**
	 * Updates task-related messages in the status bar
	 */
	protected void updateTaskMessages() {
		StringBuilder strBuff = new StringBuilder("<HTML>");
		if (taskStatusStrings.size() > 0) {
			strBuff.append("- MESSAGES -<BR>");

			for (int i = taskStatusStrings.size() - 1; i >= 0; i--) {
				strBuff.append(taskStatusStrings.get(i) + "<BR>");
			}

			strBuff.append("<BR>");

			taskMessagesLabel.setVisible(true);
		} else {
			taskMessagesLabel.setVisible(false);
		}

		strBuff.append("</HTML>");

		taskMessagesLabel.setText(strBuff.toString());
	}

	/**
	 * Updates the menu 'world'
	 */
	protected void updateWorldMenu() {
		worldMenu.reset();
		worldMenu.addAction(new MinimizeAllWindows(), KeyEvent.VK_M);

		if (!isFullScreenMode) {
			worldMenu.addAction(new TurnOnFullScreen(), KeyEvent.VK_F);
		} else {
			worldMenu.addAction(new TurnOffFullScreen(), KeyEvent.VK_F);
		}

		if (!preferences.isEnableTooltips()) {
			worldMenu.addAction(new TurnOnTooltips(), KeyEvent.VK_T);
		} else {
			worldMenu.addAction(new TurnOffTooltips(), KeyEvent.VK_T);
		}

		if (!Grid.isGridVisible()) {
			worldMenu.addAction(new TurnOnGrid(), KeyEvent.VK_G);
		} else {
			worldMenu.addAction(new TurnOffGrid(), KeyEvent.VK_G);
		}

		if (!isSelectionMode()) {
			worldMenu.addAction(new SwitchToSelectionMode(), KeyEvent.VK_S);
		} else {
			worldMenu.addAction(new SwitchToNavigationMode(), KeyEvent.VK_S);
		}

		MenuBuilder qualityMenu = worldMenu.createSubMenu("Rendering Quality");
		qualityMenu.getJMenu().setMnemonic(KeyEvent.VK_Q);

		qualityMenu.addAction(new LowQualityAction(), KeyEvent.VK_L);
		qualityMenu.addAction(new MediumQualityAction(), KeyEvent.VK_M);
		qualityMenu.addAction(new HighQualityAction(), KeyEvent.VK_H);

		MenuBuilder debugMenu = worldMenu.createSubMenu("Debug");
		debugMenu.getJMenu().setMnemonic(KeyEvent.VK_E);

		if (!PDebug.debugPrintUsedMemory) {
			debugMenu.addAction(new ShowDebugMemory(), KeyEvent.VK_S);
		} else {
			debugMenu.addAction(new HideDebugMemory(), KeyEvent.VK_H);
		}
	}

	/**
	 * Called when the user closes the Application window
	 */
	protected void windowClosing() {
		savePreferences();
		System.exit(0);
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

	/**
	 * @param message
	 *            Task related status message to remove from the status bar
	 * @return status message
	 */
	public String addTaskStatusMsg(String message) {
		taskStatusStrings.add(message);
		updateTaskMessages();
		return message;
	}

	/**
	 * @return String which describes what the application is about
	 */
	public abstract String getAboutString();

	/**
	 * @return Action manager responsible for managing actions. Enables undo,
	 *         redo functionality.
	 */
	public ReversableActionManager getActionManager() {
		return actionManager;
	}

	/**
	 * @return Name of the application
	 */
	public abstract String getAppName();

	/**
	 * @return Canvas which hold the zoomable UI
	 */
	public Canvas getCanvas() {
		return canvas;
	}

	/**
	 * @return Name of the directory to store user files
	 */
	public String getUserFileDirectory() {
		return USER_FILE_DIR;
	}

	/**
	 * @return the top-most World associated with this frame
	 */
	public World getWorld() {
		return canvas.getWorld();
	}

	/**
	 * Checks whether the UI is in selection or navigation mode.
	 * 
	 * @return If true, selection mode is enabled. If false, navigation mode is
	 *         enabled.
	 */
	public boolean isSelectionMode() {
		return selectionModeEnabled;
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

	/**
	 * @param message
	 *            Task-related status message to add to the status bar
	 */
	public void removeTaskStatusMsg(String message) {
		taskStatusStrings.remove(message);
		updateTaskMessages();
	}

	/**
	 * Called when reversable actions have changed. Updates the edit menu.
	 */
	public void reversableActionsUpdated() {
		updateEditMenu();
	}

	/**
	 * @param fullScreenMode
	 *            sets the screen to fullscreen
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
	 * @param enabled
	 *            True if selection mode is enabled, False if navigation
	 */
	public void setSelectionMode(boolean enabled) {

		selectionModeEnabled = enabled;
		if (selectionModeEnabled) {
			interactionModeLabel.setText("Selection Mode");
		} else {
			interactionModeLabel.setText("Navigation Mode");
		}

		for (World world : getCanvas().getWorlds()) {
			world.setSelectionMode(selectionModeEnabled);
		}
	}

	/**
	 * @param message
	 *            Sets the text of the status bar in the UI
	 */
	public void setStatusMessage(String message) {
		statusStr = message;
		statusMessageLabel.setText(statusStr);

	}

	/**
	 * Action to show the 'about' dialog
	 * 
	 * @author Shu Wu
	 */
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
	 * Action to hide debug memory messages printed to the console.
	 * 
	 * @author Shu Wu
	 */
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

	/**
	 * Action to set rendering mode to high quality.
	 * 
	 * @author Shu Wu
	 */
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

	/**
	 * Action to set rendering mode to low quality.
	 * 
	 * @author Shu Wu
	 */
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

	/**
	 * Action to set rendering mode to medium quality.
	 * 
	 * @author Shu Wu
	 */
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

	/**
	 * Minimizes all windows in the top-level world
	 * 
	 * @author Shu Wu
	 */
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

	/**
	 * Listener which listens for Application window close events
	 * 
	 * @author Shu Wu
	 */
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

	/**
	 * Action to redo the last reversable action
	 * 
	 * @author Shu Wu
	 */
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

	/**
	 * Action to enable the printing of memory usage messages to the console
	 * 
	 * @author Shu Wu
	 */
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

	/**
	 * Action which shows the tips dialog
	 * 
	 * @author Shu Wu
	 */
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

	/**
	 * Action to turn off full screen mode
	 * 
	 * @author Shu Wu
	 */
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

	/**
	 * Action to turn off the grid
	 * 
	 * @author Shu Wu
	 */
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

	/**
	 * Action to turn off tooltips
	 * 
	 * @author Shu Wu
	 */
	class TurnOffTooltips extends StandardAction {

		private static final long serialVersionUID = 1L;

		public TurnOffTooltips() {
			super("Autoshow Tooltips off");
		}

		@Override
		protected void action() throws ActionException {
			preferences.setEnableTooltips(false);
			updateWorldMenu();
		}

	}

	/**
	 * Action to turn on full screen mode
	 * 
	 * @author Shu Wu
	 */
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

	/**
	 * Action to turn on the grid
	 * 
	 * @author Shu Wu
	 */
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

	/**
	 * Action to turn on tooltips
	 * 
	 * @author Shu Wu
	 */
	class TurnOnTooltips extends StandardAction {

		private static final long serialVersionUID = 1L;

		public TurnOnTooltips() {
			super("Autoshow Tooltips on");
		}

		@Override
		protected void action() throws ActionException {
			preferences.setEnableTooltips(true);
			updateWorldMenu();
		}

	}

	/**
	 * Action to switch to selection mode
	 * 
	 * @author Shu Wu
	 */
	class SwitchToSelectionMode extends StandardAction {

		private static final long serialVersionUID = 1L;

		public SwitchToSelectionMode() {
			super("Switch to Selection Mode");
		}

		@Override
		protected void action() throws ActionException {
			setSelectionMode(true);
			updateWorldMenu();
		}

	}

	/**
	 * Action to switch to navigation mode
	 * 
	 * @author Shu Wu
	 */
	class SwitchToNavigationMode extends StandardAction {

		private static final long serialVersionUID = 1L;

		public SwitchToNavigationMode() {
			super("Switch to Navigation Mode");
		}

		@Override
		protected void action() throws ActionException {
			setSelectionMode(false);
			updateWorldMenu();
		}

	}

	/**
	 * Action which undos the last reversable action
	 * 
	 * @author Shu Wu
	 */
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

/**
 * Serializable object which contains UI preferences of the application
 * 
 * @author Shu Wu
 */
/**
 * @author Shu
 */
class UserPreferences implements Serializable {

	private static final long serialVersionUID = 1L;
	private boolean enableTooltips = true;
	private boolean gridVisible = true;

	/**
	 * Applies preferences
	 * 
	 * @param applyTo
	 *            The application in which to apply the preferences to
	 */
	public void apply(AppFrame applyTo) {
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