/*
 * Copyright (c) 2002-@year@, University of Maryland
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of the University of Maryland nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Piccolo was written at the Human-Computer Interaction Laboratory www.cs.umd.edu/hcil by Jesse Grosjean
 * under the supervision of Ben Bederson. The Piccolo website is www.cs.umd.edu/hcil/piccolo.
 */
package ca.nengo.ui.lib.world.handlers;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import ca.nengo.ui.lib.actions.DragAction;
import ca.nengo.ui.lib.world.WorldObject;
import ca.nengo.ui.lib.world.piccolo.WorldGroundImpl;
import ca.nengo.ui.lib.world.piccolo.WorldImpl;
import ca.nengo.ui.lib.world.piccolo.WorldObjectImpl;
import ca.nengo.ui.lib.world.piccolo.WorldSkyImpl;
import ca.nengo.ui.lib.world.piccolo.objects.SelectionBorder;
import ca.nengo.ui.lib.world.piccolo.objects.Window;
import ca.nengo.ui.lib.world.piccolo.primitives.PiccoloNodeInWorld;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolo.util.PNodeFilter;
import edu.umd.cs.piccolox.event.PNotificationCenter;

/**
 * <code>PSelectionEventHandler</code> provides standard interaction for
 * selection. Clicking selects the object under the cursor. Shift-clicking
 * allows multiple objects to be selected. Dragging offers marquee selection.
 * Pressing the delete key deletes the selection by default.
 * 
 * @version 1.0
 * @author Ben Bederson, modified by Shu Wu
 */
public class SelectionHandler extends PDragSequenceEventHandler {

	private static HashSet<SelectionListener> selectionListeners = new HashSet<SelectionListener>();

	public static final String SELECTION_CHANGED_NOTIFICATION = "SELECTION_CHANGED_NOTIFICATION";
	public static final String SELECTION_HANDLER_FRAME_ATTR = "SelHandlerFrame";

	final static int DASH_WIDTH = 5;
	final static int NUM_STROKES = 10;

	public static void addSelectionListener(SelectionListener listener) {
		selectionListeners.add(listener);
	}

	public static void removeSelectionListener(SelectionListener listener) {
		selectionListeners.remove(listener);
	}

	public static void singleObjectSelected(WorldObject obj) {
		for (SelectionListener listener : selectionListeners) {
			listener.objectFocused(obj);
		}
	}

	private HashMap<WorldObjectImpl, Boolean> allItems = null; // Used within
	// drag
	private Point2D canvasPressPt = null;
	private boolean deleteKeyActive = false; // True if DELETE key should
	// delete
	private DragAction dragAction;
	// children can be selected
	private PPath marquee = null;
	// temporarily
	private HashMap<WorldObjectImpl, Boolean> marqueeMap = null;
	// selection
	private Paint marqueePaint;
	private float marqueePaintTransparency = 1.0f;
	private WorldSkyImpl marqueeParent = null; // Node that marquee is added to
	// as
	private Paint marqueeStrokePaint;
	private WorldObjectImpl pressNode = null; // Node pressed on (or null if

	// none)
	// a
	// child
	private Point2D presspt = null;

	// selection
	private WorldGroundImpl selectableParent = null; // List of nodes whose

	private HashMap<WorldObjectImpl, Boolean> selection = null; // The current

	// /////////////////////////////////////////////////////
	// Public static methods for manipulating the selection
	// /////////////////////////////////////////////////////

	private float strokeNum = 0;

	private Stroke[] strokes = null;

	// handler temporarily
	private ArrayList<WorldObjectImpl> unselectList = null; // Used within drag
	// handler

	private WorldImpl world;

	private PanEventHandler panHandler;

	/**
	 * Creates a selection event handler.
	 * 
	 * @param marqueeParent
	 *            The node to which the event handler dynamically adds a marquee
	 *            (temporarily) to represent the area being selected.
	 * @param selectableParent
	 *            The node whose children will be selected by this event
	 *            handler.
	 */
	public SelectionHandler(WorldImpl world, PanEventHandler panHandler) {
		this.world = world;
		this.marqueeParent = world.getSky();
		this.selectableParent = world.getGround();
		this.panHandler = panHandler;
		panHandler.setSelectionHandler(this);
		setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
		init();
	}

	private boolean internalSelect(WorldObjectImpl node) {
		if (isSelected(node)) {
			return false;
		}

		selection.put(node, Boolean.TRUE);
		decorateSelectedNode(node);
		return true;
	}

	private boolean internalUnselect(WorldObjectImpl node) {
		if (!isSelected(node)) {
			return false;
		}

		undecorateSelectedNode(node);
		selection.remove(node);
		return true;
	}

	private void postSelectionChanged() {
		PNotificationCenter.defaultCenter().postNotification(SELECTION_CHANGED_NOTIFICATION, this);
	}

	protected void computeMarqueeSelection(PInputEvent pie) {
		unselectList.clear();
		// Make just the items in the list selected
		// Do this efficiently by first unselecting things not in the list
		Iterator<WorldObjectImpl> selectionEn = selection.keySet().iterator();
		while (selectionEn.hasNext()) {
			WorldObjectImpl node = selectionEn.next();
			if (!allItems.containsKey(node)) {
				unselectList.add(node);
			}
		}
		unselect(unselectList);

		// Then select the rest
		selectionEn = allItems.keySet().iterator();
		while (selectionEn.hasNext()) {
			WorldObjectImpl node = selectionEn.next();
			if (!selection.containsKey(node) && !marqueeMap.containsKey(node) && isSelectable(node)) {
				marqueeMap.put(node, Boolean.TRUE);
			} else if (!isSelectable(node)) {
				selectionEn.remove();
			}
		}

		select(allItems);
	}

	protected void computeOptionMarqueeSelection(PInputEvent pie) {
		unselectList.clear();
		Iterator<WorldObjectImpl> selectionEn = selection.keySet().iterator();
		while (selectionEn.hasNext()) {
			WorldObjectImpl node = selectionEn.next();
			if (!allItems.containsKey(node) && marqueeMap.containsKey(node)) {
				marqueeMap.remove(node);
				unselectList.add(node);
			}
		}
		unselect(unselectList);

		// Then select the rest
		selectionEn = allItems.keySet().iterator();
		while (selectionEn.hasNext()) {
			WorldObjectImpl node = selectionEn.next();
			if (!selection.containsKey(node) && !marqueeMap.containsKey(node) && isSelectable(node)) {
				marqueeMap.put(node, Boolean.TRUE);
			} else if (!isSelectable(node)) {
				selectionEn.remove();
			}
		}

		select(allItems);
	}

	protected PNodeFilter createNodeFilter(PBounds bounds) {
		return new BoundsFilter(bounds);
	}

	protected void drag(PInputEvent e) {
		super.drag(e);

		if (shouldStartMarqueeMode() && marquee != null) {
			updateMarquee(e);

			if (!isOptionSelection(e)) {
				computeMarqueeSelection(e);
			} else {
				computeOptionMarqueeSelection(e);
			}
		} else {
			dragStandardSelection(e);

		}
	}

	/**
	 * This gets called continuously during the drag, and is used to animate the
	 * marquee
	 */
	protected void dragActivityStep(PInputEvent aEvent) {
		if (marquee != null) {
			float origStrokeNum = strokeNum;
			strokeNum = (strokeNum + 0.5f) % NUM_STROKES; // Increment by
			// partial steps to
			// slow down
			// animation
			if ((int) strokeNum != (int) origStrokeNum) {
				marquee.setStroke(strokes[(int) strokeNum]);
			}
		}
	}

	protected void dragStandardSelection(PInputEvent e) {

		Iterator<WorldObject> selectionEn = getSelection().iterator();

		if (selectionEn.hasNext()) {
			e.setHandled(true);
			PDimension d = e.getDeltaRelativeTo(selectableParent.getPiccolo());

			while (selectionEn.hasNext()) {
				WorldObject node = selectionEn.next();
				if (!node.isAnimating()) {
					PDimension gDist = new PDimension();
					gDist.setSize(d);

					node.localToParent(node.globalToLocal(gDist));

					node.dragOffset(gDist.getWidth(), gDist.getHeight());
				}
			}
		}
	}

	protected void endDrag(PInputEvent e) {
		super.endDrag(e);
		panHandler.setInverted(false);
		endSelection();
	}

	public void endSelection() {
		if (marquee != null) {
			// Remove marquee
			marquee.removeFromParent();
			marquee = null;
		}
		if (!shouldStartMarqueeMode()) {
			if (dragAction != null) {
				dragAction.setFinalPositions();
				dragAction.doAction();
				dragAction = null;
			}

			if (getSelection().size() == 1) {
				unselectAll();
			}
			endStandardSelection();
		}
	}

	// //////////////////////////////////////////////////////
	// The overridden methods from PDragSequenceEventHandler
	// //////////////////////////////////////////////////////

	protected void endStandardSelection() {
		pressNode = null;
	}

	protected PBounds getMarqueeBounds() {
		if (marquee != null) {
			return marquee.getBounds();
		}
		return new PBounds();
	}

	protected void init() {
		float[] dash = { DASH_WIDTH, DASH_WIDTH };
		strokes = new Stroke[NUM_STROKES];
		for (int i = 0; i < NUM_STROKES; i++) {
			strokes[i] = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, dash, i);
		}

		selection = new HashMap<WorldObjectImpl, Boolean>();
		allItems = new HashMap<WorldObjectImpl, Boolean>();
		unselectList = new ArrayList<WorldObjectImpl>();
		marqueeMap = new HashMap<WorldObjectImpl, Boolean>();
	}

	protected void initializeMarquee(PInputEvent e) {
		marquee = PPath.createRectangle((float) presspt.getX(), (float) presspt.getY(), 0, 0);
		marquee.setPaint(marqueePaint);
		marquee.setTransparency(marqueePaintTransparency);
		marquee.setStrokePaint(marqueeStrokePaint);
		marquee.setStroke(strokes[0]);

		marqueeParent.getPiccolo().addChild(marquee);

		marqueeMap.clear();
	}

	// //////////////////////////
	// Additional methods
	// //////////////////////////

	protected void initializeSelection(PInputEvent pie) {
		canvasPressPt = pie.getCanvasPosition();
		presspt = pie.getPosition();

		for (PNode node = pie.getPath().getPickedNode(); node != null; node = node.getParent()) {
			if (node instanceof PiccoloNodeInWorld) {
				WorldObjectImpl wo = (WorldObjectImpl) ((PiccoloNodeInWorld) node).getWorldObject();
				
				if (wo != null && wo.isSelectable()) {
					pressNode = wo;
					wo.moveToFront();
					
					// EH - move parents to front, so that clicking on an ensemble brings the network window forward
					// (I currently can't get this to work)
//					WorldObject pnode = wo.getParent();
//					while( pnode != null ) {
//						pnode.moveToFront();
//						pnode = pnode.getParent();
//					}
					
					return;
				}
			}
		}
	}

	protected boolean shouldStartMarqueeMode() {
		return (pressNode == null && world.isSelectionMode());
	}

	/**
	 * Determine if the specified node is selectable (i.e., if it is a child of
	 * the one the list of selectable parents.
	 */
	protected boolean isSelectable(WorldObjectImpl node) {
		boolean selectable = false;

		if (node != null && selectableParent.isAncestorOf(node)) {
			selectable = true;

		}

		return selectable;
	}

	protected void startDrag(PInputEvent e) {
		super.startDrag(e);

		initializeSelection(e);

		if (shouldStartMarqueeMode()) {
			initializeMarquee(e);

			if (!isOptionSelection(e)) {
				startMarqueeSelection(e);
			} else {
				startOptionMarqueeSelection(e);
			}
		} else {
			if (!isOptionSelection(e)) {
				startStandardSelection(e);
			} else {
				startStandardOptionSelection(e);
			}

			Collection<WorldObject> nodes = getSelection();
			if (nodes.size() > 0) {
				dragAction = new DragAction(nodes);
				panHandler.setInverted(true);
			}

		}
	}

	protected void startMarqueeSelection(PInputEvent e) {
		unselectAll();
	}

	protected void startOptionMarqueeSelection(PInputEvent e) {
	}

	protected void startStandardOptionSelection(PInputEvent pie) {
		// Option indicator is down, toggle selection
		if (isSelectable(pressNode)) {
			if (isSelected(pressNode)) {
				unselect(pressNode);
			} else {
				select(pressNode);
			}
		}
	}

	protected void startStandardSelection(PInputEvent pie) {
		// Option indicator not down - clear selection, and start fresh
		if (!isSelected(pressNode)) {
			unselectAll();

			if (isSelectable(pressNode)) {
				select(pressNode);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void updateMarquee(PInputEvent pie) {
		PBounds b = new PBounds();

		if (marqueeParent.getPiccolo() instanceof PCamera) {
			b.add(canvasPressPt);
			b.add(pie.getCanvasPosition());
		} else {
			b.add(presspt);
			b.add(pie.getPosition());
		}

		b.reset();
		b.add(presspt);
		b.add(pie.getPosition());

		PBounds marqueeBounds = (PBounds) b.clone();

		selectableParent.globalToLocal(marqueeBounds);

		marqueeParent.viewToLocal(marqueeBounds);

		// marquee.globalToLocal(b);
		marquee.setPathToRectangle((float) marqueeBounds.x, (float) marqueeBounds.y, (float) marqueeBounds.width,
				(float) marqueeBounds.height);

		allItems.clear();
		PNodeFilter filter = createNodeFilter(b);

		Collection<PNode> items;

		items = selectableParent.getPiccolo().getAllNodes(filter, null);

		Iterator<PNode> itemsIt = items.iterator();
		while (itemsIt.hasNext()) {
			PNode next = itemsIt.next();
			if (next instanceof PiccoloNodeInWorld) {
				WorldObjectImpl wo = (WorldObjectImpl) ((PiccoloNodeInWorld) next).getWorldObject();
				allItems.put(wo, Boolean.TRUE);
			}

		}

	}

	public void decorateSelectedNode(WorldObjectImpl node) {
		SelectionBorder frame = new SelectionBorder(world, node);

		node.setSelected(true);
		node.getPiccolo().addAttribute(SELECTION_HANDLER_FRAME_ATTR, frame);
	}

	/**
	 * Indicates the color used to paint the marquee.
	 * 
	 * @return the paint for interior of the marquee
	 */
	public Paint getMarqueePaint() {
		return marqueePaint;
	}

	/**
	 * Indicates the transparency level for the interior of the marquee.
	 * 
	 * @return Returns the marquee paint transparency, zero to one
	 */
	public float getMarqueePaintTransparency() {
		return marqueePaintTransparency;
	}

	/**
	 * Returns a copy of the currently selected nodes.
	 */
	public Collection<WorldObject> getSelection() {
		ArrayList<WorldObject> sel = new ArrayList<WorldObject>(selection.keySet());

		int destroyedCount = 0;
		for (WorldObject wo : sel) {
			if (wo.isDestroyed()) {
				destroyedCount++;
			}
		}

		if (destroyedCount > 0) {
			// filter the selection to only return the non-destroyed objects
			ArrayList<WorldObject> filteredSel = new ArrayList<WorldObject>(sel.size() - destroyedCount);

			for (WorldObject wo : sel) {
				if (!wo.isDestroyed()) {
					filteredSel.add(wo);
				} else {
					// remove the destroyed objects
					unselect((WorldObjectImpl) wo);
				}
			}
			return filteredSel;
		} else {
			return sel;
		}
	}

	/**
	 * Gets a reference to the currently selected nodes. You should not modify
	 * or store this collection.
	 */
	public Collection<WorldObjectImpl> getSelectionReference() {
		return Collections.unmodifiableCollection(selection.keySet());
	}

	public boolean getSupportDeleteKey() {
		return deleteKeyActive;
	}

	public boolean isDeleteKeyActive() {
		return deleteKeyActive;
	}

	public boolean isOptionSelection(PInputEvent pie) {
		return pie.isShiftDown();
	}

	public boolean isSelected(WorldObjectImpl node) {
		if ((node != null) && (selection.containsKey(node))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Delete selection when delete key is pressed (if enabled)
	 */
	public void keyPressed(PInputEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_DELETE:
			if (deleteKeyActive) {
				Iterator<WorldObjectImpl> selectionEn = selection.keySet().iterator();
				while (selectionEn.hasNext()) {
					WorldObjectImpl node = selectionEn.next();
					node.destroy();
				}
				selection.clear();
			}
		}
	}

	public void select(Collection<WorldObjectImpl> items) {
		boolean changes = false;
		Iterator<WorldObjectImpl> itemIt = items.iterator();
		while (itemIt.hasNext()) {
			WorldObjectImpl node = itemIt.next();
			changes |= internalSelect(node);
		}
		if (changes) {
			postSelectionChanged();
		}
	}

	public void select(Map<WorldObjectImpl, Boolean> items) {
		select(items.keySet());
	}

	public void select(WorldObjectImpl node) {
		if (internalSelect(node)) {
			postSelectionChanged();
		}
		singleObjectSelected(node);
	}

	/**
	 * Specifies if the DELETE key should delete the selection
	 */
	public void setDeleteKeyActive(boolean deleteKeyActive) {
		this.deleteKeyActive = deleteKeyActive;
	}

	/**
	 * Sets the color used to paint the marquee.
	 * 
	 * @param paint
	 *            the paint color
	 */
	public void setMarqueePaint(Paint paint) {
		this.marqueePaint = paint;
	}

	/**
	 * Sets the transparency level for the interior of the marquee.
	 * 
	 * @param marqueePaintTransparency
	 *            The marquee paint transparency to set.
	 */
	public void setMarqueePaintTransparency(float marqueePaintTransparency) {
		this.marqueePaintTransparency = marqueePaintTransparency;
	}

	public void setMarqueeStrokePaint(Paint marqueeStrokePaint) {
		this.marqueeStrokePaint = marqueeStrokePaint;
	}

	// ////////////////////
	// Inner classes
	// ////////////////////

	public void undecorateSelectedNode(WorldObjectImpl node) {

		Object frame = node.getPiccolo().getAttribute(SELECTION_HANDLER_FRAME_ATTR);
		if (frame != null && frame instanceof SelectionBorder) {
			((SelectionBorder) frame).destroy();

		}
		node.setSelected(false);
		node.getPiccolo().addAttribute(SELECTION_HANDLER_FRAME_ATTR, null);
	}

	public void unselect(Collection<WorldObjectImpl> items) {
		boolean changes = false;
		Iterator<WorldObjectImpl> itemIt = items.iterator();
		while (itemIt.hasNext()) {
			WorldObjectImpl node = (WorldObjectImpl) itemIt.next();
			changes |= internalUnselect(node);
		}
		if (changes) {
			postSelectionChanged();
		}
	}

	public void unselect(WorldObjectImpl node) {
		if (internalUnselect(node)) {
			postSelectionChanged();
		}
	}

	public void unselectAll() {
		// Because unselect() removes from selection, we need to
		// take a copy of it first so it isn't changed while we're iterating
		ArrayList<WorldObjectImpl> sel = new ArrayList<WorldObjectImpl>(selection.keySet());
		unselect(sel);
	}

	protected class BoundsFilter implements PNodeFilter {
		PBounds bounds;
		PBounds localBounds = new PBounds();

		protected BoundsFilter(PBounds bounds) {
			this.bounds = bounds;
		}

		public boolean accept(PNode node) {
			localBounds.setRect(bounds);
			node.globalToLocal(localBounds);

			boolean boundsIntersects = node.intersects(localBounds);
			boolean isMarquee = (node == marquee);

			if (node instanceof PiccoloNodeInWorld) {
				WorldObject wo = ((PiccoloNodeInWorld) node).getWorldObject();

				if (wo.isSelectable()) {
					return (node.getPickable() && boundsIntersects && !isMarquee && !(wo == selectableParent));
				}
			}
			return false;

		}

		public boolean acceptChildrenOf(PNode node) {
			return node == selectableParent.getPiccolo();
		}

	}

	public static interface SelectionListener {
		public void objectFocused(WorldObject obj);
	}

}