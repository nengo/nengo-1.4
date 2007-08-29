package ca.neo.ui.test;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import ca.neo.examples.FuzzyLogicExample;
import ca.neo.model.StructuralException;
import ca.neo.ui.NeoGraphics;
import ca.neo.ui.models.nodes.UINetwork;
import ca.shu.ui.lib.objects.Window;

/**
 * Just a quick check for one type of memory leaks in Network Viewer.
 * 
 * @author Shu
 */
public class NetworkViewerMemoryTest {
	private static int i;
	private static NeoGraphics neoGraphics;
//	private static NetworkViewer netView;
	private static final int NUM_OF_LOOPS = 500;
	private static Window window;
//	private static Window[] windows;

	public static long getApproximateUsedMemory() {
		System.gc();
		System.runFinalization();
		long totalMemory = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		return totalMemory - free;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		printMemoryUsed("Start");
		neoGraphics = new NeoGraphics("Test");

//		Window windows = new Window[NUM_OF_LOOPS];
		for (i = 0; i < NUM_OF_LOOPS; i++) {

			try {

				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {

						UINetwork network;
						try {
							network = new UINetwork(FuzzyLogicExample
									.createNetwork());

							network.openViewer();
							//							
							// netView = new NetworkViewer(network);
							//							
							window = new Window(neoGraphics.getWorld()
									.getGround(), network);
						} catch (StructuralException e) {
							e.printStackTrace();
						}

					}
				});
				Thread.sleep(1000);

				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						// network.destroy()
						// netView.destroy();
						window.destroy();
					}
				});

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			printMemoryUsed("Loop # " + i);
		}

	}

	public static void printMemoryUsed(String msg) {
		System.out.println("*** " + msg + " ***");
		System.out.println("Approximate used memory: "
				+ getApproximateUsedMemory() / 1024 + " k");
	}

}
