package ca.shu.ui.lib.actions;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;

import ca.shu.ui.lib.objects.Window;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.PNode;

/**
 * Action which allows the dragging of objects by the selection handler to be
 * done and undone. NOTE: Special care is taken of Window objects. These objects
 * maintain their own Window state, and are thus immune to this action handler's
 * undo action.
 * 
 * @author Shu Wu
 */
public class DragAction extends ReversableAction {

	private static final long serialVersionUID = 1L;
	private Collection<WorldObject> selectedObjects;

	private HashMap<WorldObject, ObjectState> objectStates;

	/**
	 * @param selectedObjects
	 *            Nodes before they are dragged. Their offset positions will be
	 *            used as initial positions.
	 */
	public DragAction(Collection<WorldObject> selectedObjects) {
		super(getActionDescription(selectedObjects));
		this.selectedObjects = selectedObjects;

		objectStates = new HashMap<WorldObject, ObjectState>(selectedObjects
				.size());

		for (WorldObject object : selectedObjects) {
			ObjectState state = new ObjectState(object.getParent(), object
					.getOffset());
			objectStates.put(object, state);
		}

	}

	static int i = 1;

	private static String getActionDescription(
			Collection<WorldObject> selectedObjects) {

		if (selectedObjects.size() > 1) {
			return i++ + "Drag objects";
		} else if (selectedObjects.size() == 1) {
			return i++ + "Drag " + selectedObjects.iterator().next().getName();
		} else {
			return "No Objects Dragged";
		}

	}

	/**
	 * @param obj
	 *            Object whose drag is being undone
	 * @return True, if Object's drag can be undone
	 */
	public static boolean isObjectDragUndoable(WorldObject obj) {
		if (obj instanceof Window) {
			/*
			 * Window drag actions are immune to being undone
			 */
			return false;
		} else
			return true;
	}

	/**
	 * Stores the final positions based on the node offsets... called from
	 * selection handler after dragging has ended
	 */
	public void setFinalPositions() {
		for (WorldObject object : selectedObjects) {
			ObjectState state = objectStates.get(object);
			state.setFinalState(object.getParent(), object.getOffset());
		}
	}

	@Override
	protected void action() throws ActionException {

		for (WorldObject object : selectedObjects) {
			ObjectState state = objectStates.get(object);
			state.getFinalParent().addChild(object);
			object.setOffset(state.getFinalOffset());

			object.justDropped();
		}
	}

	@Override
	protected void undo() throws ActionException {
		for (WorldObject object : selectedObjects) {
			if (isObjectDragUndoable(object)) {
				ObjectState state = objectStates.get(object);
				state.getInitialParent().addChild(object);
				object.setOffset(state.getInitialOffset());

				object.justDropped();
			}
		}
	}

	@Override
	protected boolean isReversable() {
		if ((selectedObjects.size() == 0))
			return false;

		return ((!(selectedObjects.size() == 1 && !isObjectDragUndoable(selectedObjects
				.iterator().next()))));
	}
}

/**
 * Stores UI state variables required to do and undo drag operations.
 * 
 * @author Shu Wu
 */
class ObjectState {
	private PNode iParent;
	private Point2D iOffset;
	private PNode fParent;
	private Point2D fOffset;

	protected ObjectState(PNode initialParent, Point2D initialOffset) {
		super();
		this.iParent = initialParent;
		this.iOffset = initialOffset;
	}

	protected void setFinalState(PNode finalParent, Point2D finalOffset) {
		this.fParent = finalParent;
		this.fOffset = finalOffset;
	}

	protected PNode getInitialParent() {
		return iParent;
	}

	protected Point2D getInitialOffset() {
		return iOffset;
	}

	protected PNode getFinalParent() {
		return fParent;
	}

	protected Point2D getFinalOffset() {
		return fOffset;
	}

}
