package ca.shu.ui.lib.world.handlers;

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

	private WorldImpl world;

	public KeyboardHandler(WorldImpl world) {
		super();
		this.world = world;
		this.searchHandler = new SearchInputHandler(world);
	}

	@Override
	public void keyPressed(PInputEvent event) {
		if (searchEnabled && searchHandler.isSearching()) {
			searchHandler.keyPressed(event.getKeyCode());

		}
	}

	@Override
	public void keyTyped(PInputEvent event) {
		if (!searchEnabled || !searchHandler.isSearching()) {
			searchEnabled = false;

			if (event.getKeyChar() == 's' || event.getKeyChar() == 'S') {
				UIEnvironment.getInstance().getUniverse().setSelectionMode(!world.isSelectionMode());
			} else if (event.getKeyChar() == 'f' || event.getKeyChar() == 'F') {
				searchEnabled = !searchEnabled;

				if (searchEnabled) {
					searchHandler.beginSearch();
				} else {
					searchHandler.finishSearch();
				}
			}
		} else {

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
