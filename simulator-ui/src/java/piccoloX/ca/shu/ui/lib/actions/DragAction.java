package ca.shu.ui.lib.actions;

import java.awt.geom.Point2D;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import ca.shu.ui.lib.world.IDroppable;
import ca.shu.ui.lib.world.IWorldObject;
import ca.shu.ui.lib.world.piccolo.WorldObjectImpl;
import ca.shu.ui.lib.world.piccolo.objects.Window;

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
	private Collection<WeakReference<WorldObjectImpl>> selectedObjectsRef;

	private HashMap<WeakReference<WorldObjectImpl>, ObjectState> objectStates;

	/**
	 * @param selectedObjects
	 *            Nodes before they are dragged. Their offset positions will be
	 *            used as initial positions.
	 */
	public DragAction(Collection<WorldObjectImpl> selectedObjects) {
		super("Drag operation");

		selectedObjectsRef = new ArrayList<WeakReference<WorldObjectImpl>>(
				selectedObjects.size());

		objectStates = new HashMap<WeakReference<WorldObjectImpl>, ObjectState>(
				selectedObjects.size());

		for (WorldObjectImpl wo : selectedObjects) {

			WeakReference<WorldObjectImpl> woRef = new WeakReference<WorldObjectImpl>(
					wo);
			selectedObjectsRef.add(woRef);

			ObjectState state = new ObjectState(wo.getParent(), wo.getOffset());
			objectStates.put(woRef, state);

		}

	}

	/**
	 * @param obj
	 *            Object whose drag is being undone
	 * @return True, if Object's drag can be undone
	 */
	public static boolean isObjectDragUndoable(IWorldObject obj) {
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
		for (WeakReference<WorldObjectImpl> object : selectedObjectsRef) {
			IWorldObject node = object.get();

			if (node != null) {
				ObjectState state = objectStates.get(object);
				if (state != null) {
					state.setFinalState(node.getParent(), node.getOffset());
				}
			}
		}
	}

	@Override
	protected void action() throws ActionException {

		for (WeakReference<WorldObjectImpl> object : selectedObjectsRef) {
			WorldObjectImpl node = object.get();

			if (node != null) {
				ObjectState state = objectStates.get(object);
				IWorldObject fParent = state.getFinalParentRef().get();
				if (fParent != null) {

					fParent.addChild(node);
					node.setOffset(state.getFinalOffset());

					dropNode(node);

				}
			}
		}
	}

	public static void dropNode(IWorldObject node) {
		if (node instanceof IDroppable) {
			IDroppable droppable = (IDroppable) node;

			Collection<IWorldObject> results = node
					.getWorldLayer()
					.findIntersectingNodes(node.localToGlobal(node.getBounds()));

			ArrayList<IWorldObject> targets = new ArrayList<IWorldObject>(
					results.size());

			for (IWorldObject wo : results) {

				targets.add(wo);

			}

			droppable.justDropped(targets);

		}
	}

	@Override
	protected void undo() throws ActionException {
		for (WeakReference<WorldObjectImpl> woRef : selectedObjectsRef) {
			WorldObjectImpl wo = woRef.get();
			if (wo != null) {
				if (isObjectDragUndoable(wo)) {
					ObjectState state = objectStates.get(woRef);

					IWorldObject iParent = state.getInitialParentRef().get();

					if (iParent != null) {

						iParent.addChild(wo);
						wo.setOffset(state.getInitialOffset());

						dropNode(wo);
					}
				}
			}
		}
	}

	@Override
	protected boolean isReversable() {
		int numOfDraggableObjects = 0;
		for (WeakReference<WorldObjectImpl> woRef : selectedObjectsRef) {
			if (woRef.get() != null && isObjectDragUndoable(woRef.get())) {
				numOfDraggableObjects++;
			}
		}

		if (numOfDraggableObjects >= 1) {
			return true;
		} else {
			return false;
		}
	}
}

/**
 * Stores UI state variables required to do and undo drag operations.
 * 
 * @author Shu Wu
 */
class ObjectState {
	private WeakReference<IWorldObject> iParent;
	private Point2D iOffset;
	private WeakReference<IWorldObject> fParent;
	private Point2D fOffset;

	protected ObjectState(IWorldObject initialParent, Point2D initialOffset) {
		super();
		this.iParent = new WeakReference<IWorldObject>(initialParent);
		this.iOffset = initialOffset;
	}

	protected void setFinalState(IWorldObject finalParent, Point2D finalOffset) {
		this.fParent = new WeakReference<IWorldObject>(finalParent);
		this.fOffset = finalOffset;
	}

	protected WeakReference<IWorldObject> getInitialParentRef() {
		return iParent;
	}

	protected Point2D getInitialOffset() {
		return iOffset;
	}

	protected WeakReference<IWorldObject> getFinalParentRef() {
		return fParent;
	}

	protected Point2D getFinalOffset() {
		return fOffset;
	}

}
