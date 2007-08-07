package ca.shu.ui.lib.objects.lines;

import ca.shu.ui.lib.objects.GEdge;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PPickPath;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

public class LineEndWell extends WorldObjectImpl  {

	public LineEndWell() {
		super();

		// PNode icon = PPath.createEllipse(0, 0, 30, 30);
		// icon.setPaint(GDefaults.FOREGROUND_COLOR);
		// addChild(icon);

		WorldObjectImpl icon = new LineEndWellIcon();

		addChild(icon);
		setBounds(getFullBounds());
		// setChildrenPickable(false);

		setDraggable(false);

		icon.setDraggable(false);
		icon.addInputEventListener(new MouseHandler(this));

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

	/**
	 * 
	 * @return The new LineEnd which has been created and added to the
	 *         LineEndWell
	 */
	public LineEnd createAndAddLineEnd() {
		LineEnd newLineEnd = constructLineEnd();
		addLineEnd(newLineEnd);
		return newLineEnd;
	}

	protected void addLineEnd(LineEnd lineEnd) {
		getParent().addChild(lineEnd);

		Edge edge = new Edge(this, lineEnd);
		getWorldLayer().addChild(edge);

		// PBoundsHandle.addBoundsHandlesTo(this);

	}

	/**
	 * 
	 * @return new LineEnd
	 */
	protected LineEnd constructLineEnd() {
		return new LineEnd(this);
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
	public void mousePressed(PInputEvent event) {
		// TODO Auto-generated method stub
		super.mousePressed(event);

		// if (event.getPickedNode() == )

		newLineEnd = lineEndWell.createAndAddLineEnd();

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

/**
 * This edge is conditionally visible
 * 
 * @author Shu Wu
 * 
 */
class Edge extends GEdge {

	public Edge(LineEndWell startNode, LineEnd endNode) {
		super(startNode, endNode);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void paint(PPaintContext paintContext) {
		/*
		 * Only paint this edge, if the LineEndWell is visible, or the LineEnd
		 * is connected
		 */
		if (getStartNode().getVisible()
				|| ((LineEnd) getEndNode()).isConnected())
			super.paint(paintContext);
	}

}