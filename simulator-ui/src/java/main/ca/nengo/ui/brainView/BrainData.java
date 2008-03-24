package ca.nengo.ui.brainView;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BrainData {
	public static String DATA_FILE_NAME = "t1_icbm_normal_1mm_pn3_rf20.rawb";
	public static int X_DIMENSIONS = 181;
	public static int Y_DIMENSIONS = 217;
	public static int Z_DIMENSIONS = 181;

	public static int X_START = 72;
	public static int Y_START = 126;
	public static int Z_START = 90;

	public static String DATA_FOLDER = "data";

	static File dataFile = new File(DATA_FOLDER, DATA_FILE_NAME);

	private static final byte[][][] VOXEL_DATA = new byte[Z_DIMENSIONS][Y_DIMENSIONS][X_DIMENSIONS];

	private static boolean fileProcessed = false;

	public static void initVoxelData() {
		processFile();

	}

	private static void processFile() {
		if (fileProcessed) {
			return;
		}
		fileProcessed = true;

		try {
			FileInputStream fileStream = new FileInputStream(dataFile);

			for (int zIndex = 0; zIndex < Z_DIMENSIONS; zIndex++) {
				for (int yIndex = 0; yIndex < Y_DIMENSIONS; yIndex++) {
					fileStream.read(VOXEL_DATA[zIndex][yIndex]);

				}
			}

			if (fileStream.available() != 0) {
				throw new IOException(
						"File size incorrect, does not match data dimensions");
			}
			fileStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		MyCanvas canvas = new MyCanvas();
		Frame frame = new Frame("BrainView");
		frame.add(canvas);
		frame.setSize(300, 200);
		frame.setVisible(true);

		for (int i = -50; i < 50; i++) {

			canvas.setImagePosition(i);
			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	static class MyCanvas extends Canvas {

		private static final long serialVersionUID = 1L;

		BrainTopImage image;

		public void setImagePosition(int zCoord) {
			image.setCoord(zCoord);
			repaint();
		}

		MyCanvas() {

			image = new BrainTopImage();

			// Add a listener for resize events
			addComponentListener(new ComponentAdapter() {
				// This method is called when the component's size changes
				public void componentResized(ComponentEvent evt) {
					Component c = (Component) evt.getSource();

					// Regenerate the image
					c.repaint();

				}
			});
		}

		public void paint(Graphics g) {
			if (image != null) {
				g.drawImage(image, 0, 0, null);
			}
		}
	}

	protected static byte[][][] getVoxelData() {
		initVoxelData();
		return VOXEL_DATA;
	}
}
