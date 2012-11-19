package ca.nengo.ui.lib.world.handlers;

import java.awt.event.KeyEvent;

import ca.nengo.ui.NengoGraphics;
import ca.nengo.ui.lib.AppFrame;
import ca.nengo.ui.lib.util.UIEnvironment;
import ca.nengo.ui.lib.world.Destroyable;
import ca.nengo.ui.lib.world.Search.SearchInputHandler;
import ca.nengo.ui.lib.world.piccolo.WorldImpl;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Handles key inputs
 * 
 * @author Shu Wu
 */
public class KeyboardHandler extends PBasicInputEventHandler implements Destroyable {
	private boolean searchEnabled = false;

	private final SearchInputHandler searchHandler;

	public KeyboardHandler(WorldImpl world) {
		this.searchHandler = new SearchInputHandler(world);
	}

	@Override
	public void keyPressed(PInputEvent event) {
		if ((event.getModifiers() & AppFrame.MENU_SHORTCUT_KEY_MASK) != 0) {
			// control down
			if (event.getKeyCode() == KeyEvent.VK_F) {
				searchEnabled = !isSearchEnabled();

				if (searchEnabled) {
					searchHandler.beginSearch();
				} else {
					searchHandler.finishSearch();
				}
			}
		} else if (searchEnabled && searchHandler.isSearching()) {
			// pass event to search handler
			searchHandler.keyPressed(event.getKeyCode());
		} else if (NengoGraphics.getInstance().getScriptConsolePane().isAuxVisible() && 
				   event.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
			// letter key press, package as KeyEvent and pass to ScriptConsole
			KeyEvent e = new KeyEvent(NengoGraphics.getInstance(), 0, System.currentTimeMillis(), 
									  event.getModifiers(), event.getKeyCode(), event.getKeyChar() );
			NengoGraphics.getInstance().getScriptConsole().passKeyEvent( e );
		} else if (event.isShiftDown()) {
			// shift down
			UIEnvironment.getInstance().getUniverse().setSelectionMode(true);
		}
	}

	private boolean isSearchEnabled() {
		return searchEnabled && searchHandler.isSearching();
	}

	@Override
	public void keyReleased(PInputEvent event) {
		if (!event.isShiftDown()) {
			UIEnvironment.getInstance().getUniverse().setSelectionMode(false);
		}
		super.keyReleased(event);
	}

	@Override
	public void keyTyped(PInputEvent event) {
		if (searchEnabled) {
			searchHandler.keyTyped(event.getKeyChar());
		}
	}

	public void destroy() {
		searchHandler.destroy();
	}

	@Override
	public void mousePressed(PInputEvent event) {
		super.mousePressed(event);
		/*
		 * Stop the search if the user clicks to do something else
		 */
		searchHandler.finishSearch();
	}
}
