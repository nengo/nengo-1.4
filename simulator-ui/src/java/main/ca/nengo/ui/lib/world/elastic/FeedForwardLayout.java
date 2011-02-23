/**
 * 
 */
package ca.nengo.ui.lib.world.elastic;

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Iterator;

import ca.nengo.ui.lib.world.elastic.StretchedFeedForwardLayout.VoidVertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.visualization.AbstractLayout;
import edu.uci.ics.jung.visualization.Coordinates;

/**
 * Arrange the layout of neural network according to signal flow. 
 * 
 * @author Yan Wu
 * @version 1.0.1
 */
public class FeedForwardLayout extends AbstractLayout {

	/**
	 * @param g
	 */
	public FeedForwardLayout(Graph g) {
		super(g);
	}

	public boolean incrementsAreDone() {
		return false;
	}

	public boolean isIncremental() {
		return false;
	}

	@Override
	public void advancePositions() {}

	@Override
	protected void initialize_local_vertex(Vertex arg0) {}
	
	@Override
	protected void initializeLocations() {
		super.initializeLocations();
		
		LinkedList<LinkedList<Vertex>> sortedVertices = sortVertices();
		
		Dimension d = getCurrentSize();
		double height = d.getHeight();
		double width = d.getWidth();
		
		// find width and height step
		int lengthW = sortedVertices.size();
		int lengthH = 0;
		double dW = width / (lengthW + 1);
		double dH = 0;
		
		//initialize coordinates
		double x = width;
		double y = 0;
		Coordinates coord = null;
		
		//convert coordinate from array lists
		Vertex v;
		LinkedList<Vertex> vertices;
		while (!sortedVertices.isEmpty()) {
			vertices = sortedVertices.removeFirst();
			lengthH = vertices.size();
			dH = height / (lengthH + 1);
			x -= dW;
			y = 0;
			
			//down to each level of vertices
			while (!vertices.isEmpty()) {
				y += dH;
				v = vertices.removeFirst();
				// VoidVertex serves as place holder in LinkedList
				if (!(v instanceof VoidVertex)) {
					coord = this.getCoordinates(v);
					coord.setX(x);
					coord.setY(y);
				}
			}
		}
	}

	

	protected LinkedList<LinkedList<Vertex>> sortVertices() {
		LinkedList<LinkedList<Vertex>> sortedVertices = new LinkedList<LinkedList<Vertex>>();
		@SuppressWarnings("unchecked")
		Vertex[] vArray = (Vertex[]) this.getVisibleVertices().toArray(new Vertex[0]);
		LinkedList<Vertex> verticesLeft =  new LinkedList<Vertex>(Arrays.asList(vArray));
		Vertex v = null;
		
		
		/**
		 * Find ending and starting vertices
		 */
		int minimalOutDegree = 999;
		int outDegree;
		for (Vertex vt: verticesLeft) {
			outDegree = vt.outDegree();
			if (minimalOutDegree > outDegree)
				minimalOutDegree = outDegree;
		} 
		
		LinkedList<Vertex> startingVertices = new LinkedList<Vertex>();
		sortedVertices.add(new LinkedList<Vertex>());
		for (Iterator<Vertex> iV = verticesLeft.iterator(); iV.hasNext(); ){
			v = iV.next();
			// find ending vertices
			if (v.outDegree() == minimalOutDegree) {
				sortedVertices.getFirst().add(v);
				iV.remove();
			} 
			// find starting vertices
			else if (v.inDegree() == 0) {
				startingVertices.add(v);
				iV.remove();
			}
		}
		
		/**
		 * Construct layers from end to begin iteratively
		 */
		Vertex vEnd = null;
		LinkedList<Vertex> lastLayer = sortedVertices.getFirst();
		LinkedList<Vertex> newLayer = null;
		while (!verticesLeft.isEmpty()) {
			//initialize a new layer
			newLayer = new LinkedList<Vertex>();
			sortedVertices.add(newLayer);
			for (int i = 0; i < lastLayer.size(); i++) {
				vEnd = lastLayer.get(i);
				for (Iterator<Vertex> iV = verticesLeft.iterator(); iV.hasNext(); ){
					v = iV.next();
					if (v.isPredecessorOf(vEnd)){
						newLayer.add(v);
					    iV.remove();
					}
				}
			}
			
			// append starting vertices
			if (verticesLeft.isEmpty() && !(startingVertices==null)) {
				verticesLeft = startingVertices;
				startingVertices = null;
			}
			
			lastLayer = newLayer;
		}
		
		
		
		return sortedVertices;
	}

}
