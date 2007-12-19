package ca.shu.ui.lib.world.elastic;

import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import ca.shu.ui.lib.util.ElasticLayout;
import ca.shu.ui.lib.util.Util;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;

public class ElasticLayoutRunner {
	/**
	 * Used to determine when to pause the algorithm
	 */
	public static final double RELAX_DELTA = 2;
	public static final float SPRING_LAYOUT_FORCE_MULTIPLIER = 1f / 3f;
	public static final int SPRING_LAYOUT_NODE_DISTANCE = 300;
	public static final int SPRING_LAYOUT_REPULSION_DISTANCE = 200;
	private int relaxCount;
	private boolean continueLayout = true;

	private ElasticLayout layout;

	private DirectedSparseGraph myGraph;

	private final ElasticGround myParent;

	public ElasticLayoutRunner(ElasticGround world) {
		super();
		this.myParent = world;
		init();
	}

	private void init() {
		myGraph = myParent.getGraph();
		this.layout = new ElasticLayout(myGraph,
				new ElasticLayout.UnitLengthFunction(
						SPRING_LAYOUT_NODE_DISTANCE));
		layout.setRepulsionRange(SPRING_LAYOUT_REPULSION_DISTANCE);
		layout.setForceMultiplier(SPRING_LAYOUT_FORCE_MULTIPLIER);
		layout.initialize();

		for (Object obj : myGraph.getVertices()) {
			ElasticVertex vertex = (ElasticVertex) obj;
			Point2D vertexLocation = vertex.getLocation();
			layout.forceMove(vertex, vertexLocation.getX(), vertexLocation
					.getY());
		}
	}

	private void runLayout() {

		while (!layout.incrementsAreDone() && !myParent.isDestroyed()
				&& continueLayout) {

			/**
			 * Layout nodes needs to be done in the Swing dispatcher thread
			 */
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						if (updateLayout()) {
							myParent.updateChildrenFromLayout(layout, false,
									false);
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				Thread.sleep(1000 / 25);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (layout.incrementsAreDone()) {
			Util.Assert(false,
					"Iterable layout is done, this shouldn't be possible");
		}
	}

	private boolean updateLayout() {

		boolean graphUpdated = myParent.updateGraph();

		if (graphUpdated) {
			layout.update();
			relaxCount = 0;
		}

		boolean isResting = false;

		if (relaxCount >= 50) {
			relaxCount = 50;
			isResting = true;
		}
		if (!isResting) {
			layout.advancePositions();

			// Check to see if the elastic graph has settled in a certain
			// position
			double maxDelta = 0;
			for (Object obj : myGraph.getVertices()) {
				ElasticVertex vertex = (ElasticVertex) obj;
				Point2D vertexLocation = vertex.getLocation();
				Point2D layoutLocation = layout.getLocation(vertex);

				double delta = Math
						.abs(vertexLocation.distance(layoutLocation));

				if (delta > maxDelta) {
					maxDelta = delta;
				}
			}

			if (maxDelta < RELAX_DELTA) {
				relaxCount++;
			}
		}
		return !isResting;
	}

	public void start() {
		Thread myLayoutThread = new Thread(new Runnable() {
			public void run() {
				runLayout();
			}
		});

		// myLayoutThread.setPriority(Thread.NORM_PRIORITY);
		myLayoutThread.start();
	}

	public void stopLayout() {
		continueLayout = false;
	}

	public void forceMove(Vertex picked, double x, double y) {
		relaxCount = 0;
		layout.forceMove(picked, x, y);
	}

	public boolean isLocked(Vertex v) {
		return layout.isLocked(v);
	}

	public Point2D getLocation(ArchetypeVertex v) {
		return layout.getLocation(v);
	}

	public void lockVertex(Vertex v) {
		layout.lockVertex(v);
	}

	public void unlockVertex(Vertex v) {
		layout.unlockVertex(v);
	}
}