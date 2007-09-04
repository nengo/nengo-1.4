package ca.neo.ui.brainView;

import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class BrainTopImage extends AbstractBrainImage2D {

	public BrainTopImage() {
		super(BrainData.Y_DIMENSIONS, BrainData.X_DIMENSIONS);

	}

	@Override
	public int getCoordMax() {
		return BrainData.getVoxelData().length - BrainData.Z_START - 1;
	}

	@Override
	public int getCoordMin() {
		return -BrainData.Z_START;
	}

	@Override
	public void updateViewCoord() {
		int coord = getCoord();

		byte[] imageArray = new byte[BrainData.Y_DIMENSIONS
				* BrainData.Z_DIMENSIONS];
		int imageArrayIndex = 0;
		for (int x = 0; x < BrainData.X_DIMENSIONS; x++) {

			for (int y = 0; y < BrainData.Y_DIMENSIONS; y++) {
				// image.getRaster().setPixel(x, y, new int[] { 0 });

				imageArray[imageArrayIndex++] = BrainData.getVoxelData()[coord
						+ BrainData.Z_START][y][x];
			}
		}
		DataBuffer buffer = new DataBufferByte(imageArray, imageArray.length, 0);

		WritableRaster raster = Raster.createWritableRaster(getSampleModel(),
				buffer, null);

		setData(raster);

	}

	@Override
	public String getCoordName() {
		return "Z";
	}

	@Override
	public String getViewName() {
		return "Top View";
	}
}
