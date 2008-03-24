package ca.nengo.ui.brainView;

public class BrainFrontImage extends AbstractBrainImage2D {

	public BrainFrontImage() {
		super(BrainData.X_DIMENSIONS, BrainData.Z_DIMENSIONS);

	}

	@Override
	public int getCoordMax() {
		return BrainData.getVoxelData()[0].length - BrainData.Y_START - 1;
	}

	@Override
	public int getCoordMin() {
		return -BrainData.Y_START;
	}

	public byte getImageByte(int imageX, int imageY) {
		return BrainData.getVoxelData()[imageY][getCoord() + BrainData.Y_START][imageX];
	}

	@Override
	public String getCoordName() {
		return "Y(mm)";
	}

	@Override
	public String getViewName() {
		return "Front View";
	}
}
