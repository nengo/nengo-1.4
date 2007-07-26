package ca.shu.ui.lib.objects.lines;

import ca.shu.ui.lib.objects.GEdge;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PPickPath;

public class LineEndWell extends LineHolder {


	public LineEndWell() {
		super();

//		PNode icon = PPath.createEllipse(0, 0, 30, 30);
//		icon.setPaint(GDefaults.FOREGROUND_COLOR);
//		addChild(icon);

		this.addChild(new LineEndIcon());
		this.setBounds(getFullBounds());
		this.setChildrenPickable(false);
		
		this.setDraggable(false);

		this.addInputEventListener(new LineEndMouseHandler(this));

	
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

	public LineEnd addNewLineEnd() {
		LineEnd newLineEnd = new LineEnd();

		this.addChild(newLineEnd);

		GEdge edge = new GEdge(this, newLineEnd);
		this.addChild(edge);

		return newLineEnd;
	}

	@Override
	protected boolean validateFullBounds() {
		// TODO Auto-generated method stub
		return super.validateFullBounds();
	}

}

class LineEndMouseHandler extends PBasicInputEventHandler {

	LineEndWell lineEndWell;

	LineEnd newLineEnd;

	public LineEndMouseHandler(LineEndWell lineEndWell) {
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

		newLineEnd = lineEndWell.addNewLineEnd();

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