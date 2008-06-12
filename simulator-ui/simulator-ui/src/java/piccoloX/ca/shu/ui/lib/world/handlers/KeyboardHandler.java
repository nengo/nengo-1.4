package ca.shu.ui.lib.world.handlers;

import java.awt.event.KeyEvent;

import ca.shu.ui.lib.AppFrame;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.world.Destroyable;
import ca.shu.ui.lib.world.Search.SearchInputHandler;
import ca.shu.ui.lib.world.piccolo.WorldImpl;
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
		super();
		this.searchHandler = new SearchInputHandler(world);
	}

	@Override
	public void keyPressed(PInputEvent event) {
		// control down
		if ((event.getModifiers() & AppFrame.MENU_SHORTCUT_KEY_MASK) != 0) {
			if (event.getKeyCode() == KeyEvent.VK_F) {
				searchEnabled = !isSearchEnabled();

				if (searchEnabled) {
					searchHandler.beginSearch();
				} else {
					searchHandler.finishSearch();
				}
			}

		}
		// shift down
		else if (event.isShiftDown()) {
			UIEnvironment.getInstance().getUniverse().setSelectionMode(true);

		} else if (searchEnabled && searchHandler.isSearching()) {
			searchHandler.keyPressed(event.getKeyCode());

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
