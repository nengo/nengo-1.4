package ca.neo.ui.brainView;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

public abstract class AbstractBrainImage2D extends BufferedImage {
	private static ColorModel colorModel;

	private static ColorModel getBrainColorModel() {
		if (colorModel != null)
			return colorModel;

		byte[] arrR = new byte[256];
		byte[] arrG = new byte[256];
		byte[] arrB = new byte[256];

		for (int i = 0; i < 256; i++) {
			arrR[i] = (byte) (i * 0.8);
			arrG[i] = (byte) (i * 0.8);
			arrB[i] = (byte) (i * 1.0);
		}

		colorModel = new IndexColorModel(8, 256, arrR, arrG, arrB);

		return colorModel;
	}

	private static WritableRaster getBrainRaster(int width, int height) {
		return Raster.createWritableRaster(getBrainSampleModel(width, height),
				null);
	}

	private static SampleModel getBrainSampleModel(int width, int height) {
		int bitMasks[] = new int[] { 0xff };
		SinglePixelPackedSampleModel model = new SinglePixelPackedSampleModel(
				DataBuffer.TYPE_BYTE, width, height, bitMasks);

		return model;
	}

	int viewCoord;

	int imageWidth, imageHeight;

	public AbstractBrainImage2D(int width, int height) {
		super(getBrainColorModel(), getBrainRaster(width, height), false, null);
		imageWidth = width;
		imageHeight = height;
		setCoord(getCoordDefault());
	}

	private void updateViewCoord() {

		byte[] imageArray = new byte[imageWidth * imageHeight];
		int imageArrayIndex = 0;
		for (int imageY = imageHeight - 1; imageY >= 0; imageY--) {

			for (int imageX = 0; imageX < imageWidth; imageX++) {
				// image.getRaster().setPixel(x, y, new int[] { 0 });

				imageArray[imageArrayIndex++] = getImageByte(imageX, imageY);
			}
		}
		DataBuffer buffer = new DataBufferByte(imageArray, imageArray.length, 0);

		WritableRaster raster = Raster.createWritableRaster(getSampleModel(),
				buffer, null);

		setData(raster);

	}

	public int getCoordDefault() {
		return 0;
	}

	public abstract int getCoordMax();

	// protected abstract int get

	public abstract int getCoordMin();

	public int getCoord() {
		return viewCoord;
	}

	public void setCoord(int coord) {
		if (coord > getCoordMax()) {
			coord = getCoordMax();
		} else if (coord < getCoordMin()) {
			coord = getCoordMin();
		}
		viewCoord = coord;
		updateViewCoord();

	}

	public abstract byte getImageByte(int imageX, int imageY);

	public abstract String getViewName();

	public abstract String getCoordName();

}
