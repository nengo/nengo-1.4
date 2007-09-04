package ca.neo.ui.brainView;


public class BrainSideImage extends AbstractBrainImage2D {

	public BrainSideImage() {
		super(BrainData.Y_DIMENSIONS, BrainData.Z_DIMENSIONS);

	}

	@Override
	public int getCoordMax() {
		return BrainData.getVoxelData()[0][0].length - BrainData.X_START - 1;
	}

	@Override
	public int getCoordMin() {
		return -BrainData.X_START;
	}

	public byte getImageByte(int imageX, int imageY) {
		return BrainData.getVoxelData()[imageY][imageX][getCoord()
				+ BrainData.X_START];
	}

	@Override
	public String getCoordName() {
		return "X";
	}

	@Override
	public String getViewName() {
		return "Side View";
	}
}
