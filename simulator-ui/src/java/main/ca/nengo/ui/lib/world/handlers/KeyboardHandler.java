package ca.nengo.ui.lib.world.handlers;

import java.awt.event.KeyEvent;

import ca.nengo.ui.NengoGraphics;
import ca.nengo.ui.lib.util.UIEnvironment;
import ca.nengo.ui.lib.world.Destroyable;
import ca.nengo.ui.lib.world.piccolo.WorldImpl;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Handles key inputs
 * 
 * @author Shu Wu
 */
public class KeyboardHandler extends PBasicInputEventHandler implements Destroyable {

	public KeyboardHandler(WorldImpl world) {
		super();
	}

	@Override
	public void keyPressed(PInputEvent event) {
		if (NengoGraphics.getInstance().isScriptConsoleVisible()
				&& !event.isControlDown() && !event.isMetaDown()
				&& event.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
			// letter key press, package as KeyEvent and pass to ScriptConsole
			KeyEvent e = new KeyEvent(NengoGraphics.getInstance(), 0, System.currentTimeMillis(), 
									  event.getModifiers(), event.getKeyCode(), event.getKeyChar() );
			NengoGraphics.getInstance().getScriptConsole().passKeyEvent( e );
		} else if (event.isShiftDown()) {
			// shift down
			UIEnvironment.getInstance().getUniverse().setSelectionMode(true);
		}
	}

	@Override
	public void keyReleased(PInputEvent event) {
		if (!event.isShiftDown()) {
			UIEnvironment.getInstance().getUniverse().setSelectionMode(false);
		}
		super.keyReleased(event);
	}

	@Override
	public void mousePressed(PInputEvent event) {
		super.mousePressed(event);
	}

	public void destroy() {
		//do nothing
	}
}
