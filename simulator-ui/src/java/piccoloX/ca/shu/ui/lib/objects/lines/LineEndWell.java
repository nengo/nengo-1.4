package ca.shu.ui.lib.objects.lines;

import java.awt.event.MouseEvent;

import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PPickPath;

public class LineEndWell extends WorldObject {

	private static final long serialVersionUID = 1L;

	public LineEndWell() {
		super();

		WorldObject icon = new LineEndWellIcon();

		addChild(icon);
		setBounds(getFullBounds());

		setSelectable(false);

		icon.setSelectable(false);
		icon.addInputEventListener(new CreateLineEndHandler(this));

	}

	/**
	 * @return new LineEnd
	 */
	protected LineEnd constructLineEnd() {
		return new LineEnd(this);
	}

	/**
	 * @return The new LineEnd which has been created and added to the
	 *         LineEndWell
	 */
	protected LineEnd createAndAddLineEnd() {
		LineEnd newLineEnd = constructLineEnd();
		addChild(newLineEnd);
		return newLineEnd;
	}

}

/**
 * This handler listens for mouse events on the line end well and creates new
 * line ends when needed.
 * 
 * @author Shu Wu
 */
class CreateLineEndHandler extends PBasicInputEventHandler {

	private LineEndWell lineEndWell;

	private LineEnd newLineEnd;

	public CreateLineEndHandler(LineEndWell lineEndWell) {
		super();
		this.lineEndWell = lineEndWell;

	}

	@Override
	public void mousePressed(PInputEvent event) {

		super.mousePressed(event);

		if (event.getButton() != MouseEvent.BUTTON1)
			return;

		newLineEnd = lineEndWell.createAndAddLineEnd();

		PPickPath path = event.getPath();

		path.pushNode(newLineEnd);
		path.pushTransform(newLineEnd.getTransform());

	}

}
