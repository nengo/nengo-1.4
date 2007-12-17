package ca.shu.ui.lib.world.elastic;

import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.util.ElasticLayout;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;

public class ElasticLayoutRunner {
	public static final float SPRING_LAYOUT_FORCE_MULTIPLIER = 1f / 3f;
	public static final int SPRING_LAYOUT_NODE_DISTANCE = 400;
	public static final int SPRING_LAYOUT_REPULSION_DISTANCE = 400 / 2;
	private boolean continueLayout = true;
	private ElasticLayout layout;
	private final ElasticGround world;

	public ElasticLayoutRunner(ElasticGround world) {
		super();
		this.world = world;
		init();
	}

	private void init() {
		DirectedSparseGraph graph = world.getGraph(true);
		this.layout = new ElasticLayout(graph,
				new ElasticLayout.UnitLengthFunction(
						SPRING_LAYOUT_NODE_DISTANCE));
		layout.setRepulsionRange(SPRING_LAYOUT_REPULSION_DISTANCE);
		layout.setForceMultiplier(SPRING_LAYOUT_FORCE_MULTIPLIER);
		layout.initialize();

		for (Object obj : graph.getVertices()) {
			ElasticVertex vertex = (ElasticVertex) obj;
			Point2D vertexLocation = vertex.getLocation();
			layout.forceMove(vertex, vertexLocation.getX(), vertexLocation
					.getY());
		}
	}

	private void runLayout() {

		while (!layout.incrementsAreDone() && !world.isDestroyed()
				&& continueLayout) {
			updateLayout();

			/**
			 * Layout nodes needs to be done in the Swing dispatcher thread
			 */
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						world.updateChildrensFromLayout(layout, false);
					}
				});
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
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

	private void updateLayout() {
		world.getGraph(true);
		layout.update();
		layout.advancePositions();
	}

	protected ElasticLayout getLayout() {
		return layout;
	}

	public void start() {
		Thread myLayoutThread = new Thread(new Runnable() {
			public void run() {
				runLayout();
			}
		});

		myLayoutThread.setPriority(Thread.MIN_PRIORITY);
		myLayoutThread.start();
	}

	public void stopLayout() {
		continueLayout = false;
	}
}