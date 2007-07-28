package ca.shu.ui.lib.objects.lines;

import ca.shu.ui.lib.objects.GEdge;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PPickPath;

public class LineEndWell extends WorldObjectImpl {

	public LineEndWell() {
		super();

		// PNode icon = PPath.createEllipse(0, 0, 30, 30);
		// icon.setPaint(GDefaults.FOREGROUND_COLOR);
		// addChild(icon);

		addChild(new LineEndIcon());
		setBounds(getFullBounds());
		setChildrenPickable(false);

		setDraggable(false);

		addInputEventListener(new MouseHandler(this));

	}

	@Override
	public void signalBoundsChanged() {
		// TODO Auto-generated method stub
		super.signalBoundsChanged();
	}

	@Override
	protected void parentBoundsChanged() {
		// TODO Auto-generated method stub
		super.parentBoundsChanged();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LineEnd createEnd() {
		LineEnd newLineEnd = new LineEnd();

		getParent().addChild(newLineEnd);

		GEdge edge = new GEdge(this, newLineEnd);
		getParent().addChild(edge);

		return newLineEnd;
	}

	@Override
	protected boolean validateFullBounds() {
		// TODO Auto-generated method stub
		return super.validateFullBounds();
	}

}

class MouseHandler extends PBasicInputEventHandler {

	LineEndWell lineEndWell;

	LineEnd newLineEnd;

	public MouseHandler(LineEndWell lineEndWell) {
		super();
		this.lineEndWell = lineEndWell;
	}

	@Override
	public void mouseDragged(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mouseDragged(event);

		if (newLineEnd != null) {

		}
	}

	@Override
	public void mousePressed(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mousePressed(event);

		newLineEnd = lineEndWell.createEnd();

		PPickPath path = event.getPath();

		path.pushNode(newLineEnd);
		path.pushTransform(newLineEnd.getTransform());

	}

	@Override
	public void mouseReleased(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mouseReleased(event);
	}

}