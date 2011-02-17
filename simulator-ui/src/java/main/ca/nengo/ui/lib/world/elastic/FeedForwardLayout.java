/**
 * 
 */
package ca.nengo.ui.lib.world.elastic;

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Iterator;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.visualization.AbstractLayout;
import edu.uci.ics.jung.visualization.Coordinates;

/**
 * Arrange the layout of neural network according to signal flow. 
 * 
 * @author Yan Wu
 *
 */
public class FeedForwardLayout extends AbstractLayout {

	/**
	 * @param g
	 */
	public FeedForwardLayout(Graph g) {
		super(g);
	}

	@Override
	public boolean incrementsAreDone() {
		return false;
	}

	@Override
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
			vertices = sortedVertices.pop();
			lengthH = vertices.size();
			dH = height / (lengthH + 1);
			x -= dW;
			y = 0;
			
			//down to each level of vertices
			while (!vertices.isEmpty()) {
				y += dH;
				v = vertices.pop();
				coord = this.getCoordinates(v);
				coord.setX(x);
				coord.setY(y);
			}
		}
	}

	

	private LinkedList<LinkedList<Vertex>> sortVertices() {
		LinkedList<LinkedList<Vertex>> sortedVertices = new LinkedList<LinkedList<Vertex>>();
		@SuppressWarnings("unchecked")
		Vertex[] vArray = (Vertex[]) this.getVisibleVertices().toArray(new Vertex[0]);
		LinkedList<Vertex> verticesLeft =  new LinkedList<Vertex>(Arrays.asList(vArray));
		Vertex v = null;
		
		
		/**
		 * Find ending vertices
		 */
		int minimalOutDegree = 999;
		int outDegree;
		for (Vertex vt: verticesLeft) {
			outDegree = vt.outDegree();
			if (minimalOutDegree > outDegree)
				minimalOutDegree = outDegree;
		} 
		
		sortedVertices.add(new LinkedList<Vertex>());
		for (Iterator<Vertex> iV = verticesLeft.iterator(); iV.hasNext(); ){
			v = iV.next();
			if (v.outDegree() == minimalOutDegree) {
				sortedVertices.getFirst().add(v);
				iV.remove();
			}
		}
		
		/**
		 * Construct levels from end to begin iteratively
		 */
		Vertex vEnd = null;
		LinkedList<Vertex> lastLevel = sortedVertices.getFirst();
		LinkedList<Vertex> newLevel = null;
		while (!verticesLeft.isEmpty()) {
			//initialize a new level
			newLevel = new LinkedList<Vertex>();
			sortedVertices.add(newLevel);
			for (int i = 0; i < lastLevel.size(); i++) {
				vEnd = lastLevel.get(i);
				for (Iterator<Vertex> iV = verticesLeft.iterator(); iV.hasNext(); ){
					v = iV.next();
					if (v.isPredecessorOf(vEnd)){
						newLevel.add(v);
					    iV.remove();
					}
				}
			}
			
			lastLevel = newLevel;
		}
		
		return sortedVertices;
	}

}
