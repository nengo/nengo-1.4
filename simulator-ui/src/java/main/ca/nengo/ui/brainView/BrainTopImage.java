package ca.nengo.ui.brainView;


public class BrainTopImage extends AbstractBrainImage2D {

	public BrainTopImage() {
		super(BrainData.X_DIMENSIONS, BrainData.Y_DIMENSIONS);

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
	public String getCoordName() {
		return "Z(mm)";
	}

	@Override
	public String getViewName() {
		return "Top View";
	}

	public byte getImageByte(int imageX, int imageY) {
		return BrainData.getVoxelData()[getCoord() + BrainData.Z_START][imageY][imageX];
	}
}
