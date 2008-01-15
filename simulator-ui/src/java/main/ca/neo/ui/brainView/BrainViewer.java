package ca.neo.ui.brainView;

import ca.shu.ui.lib.world.IWorldObject;
import ca.shu.ui.lib.world.handlers.AbstractStatusHandler;
import ca.shu.ui.lib.world.handlers.EventConsumer;
import ca.shu.ui.lib.world.piccolo.WorldImpl;
import ca.shu.ui.lib.world.piccolo.WorldGroundImpl;
import ca.shu.ui.lib.world.piccolo.WorldObjectImpl;
import ca.shu.ui.lib.world.piccolo.primitives.PXImage;
import ca.shu.ui.lib.world.piccolo.primitives.Text;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

public class BrainViewer extends WorldImpl {

	private static final long serialVersionUID = 1L;

	AbstractBrainImage2D topView, sideView, frontView;

	public BrainViewer() {
		super("Brain View", createGround());

		setStatusBarHandler(null);
		init();

		// getSky().setScale(2);
		// setBounds(parentToLocal(getFullBounds()));
		// addInputEventListener(new EventConsumer());

	}

	private void init() {
		sideView = new BrainSideImage();
		frontView = new BrainFrontImage();

		topView = new BrainTopImage();

		getGround().addChild(new BrainImageWrapper(sideView));
		getGround().addChild(new BrainImageWrapper(frontView));
		getGround().addChild(new BrainImageWrapper(topView));

	}

	public int getZCoord() {
		return topView.getCoord();
	}

	public int getYCoord() {
		return frontView.getCoord();
	}

	private static WorldGroundImpl createGround() {
		return new WorldGroundImpl() {

			private static final long serialVersionUID = 1L;

			@Override
			public void layoutChildren() {

				int x = 0;
				double maxHeight = 0;

				for (IWorldObject wo : getChildren()) {

					if (wo.getHeight() > maxHeight) {
						maxHeight = wo.getHeight();
					}

				}

				for (IWorldObject wo : getChildren()) {

					wo.setOffset(x, maxHeight - wo.getHeight());
					x += wo.getWidth() + 10;

				}
			}
		};
	}
}

class BrainImageWrapper extends WorldObjectImpl {

	private static final long serialVersionUID = 1L;
	AbstractBrainImage2D myBrainImage;
	Text myLabel;

	public BrainImageWrapper(AbstractBrainImage2D brainImage) {
		super();
		myBrainImage = brainImage;
		addChild(new WorldObjectImpl(new PXImage(brainImage)));

		myLabel = new Text();
		addChild(myLabel);
		updateLabel();
		setSelectable(false);

		addInputEventListener(new EventConsumer());
		addInputEventListener(new BrainImageMouseHandler());

		layoutChildren();
		setBounds(parentToLocal(getFullBounds()));
		// addChild(new Border(this, Style.COLOR_FOREGROUND));

	}

	private void updateLabel() {
		myLabel.setText(myBrainImage.getViewName() + " ("
				+ myBrainImage.getCoordName() + " coord: "
				+ myBrainImage.getCoord() + ")");
	}

	@Override
	public void layoutChildren() {
		super.layoutChildren();

		myLabel.setOffset(0, myBrainImage.getHeight() + 10);
	}

	class BrainImageMouseHandler extends PDragSequenceEventHandler {

		double roundingError = 0;

		@Override
		protected void dragActivityStep(PInputEvent aEvent) {

			double dx = (aEvent.getCanvasPosition().getX() - getMousePressedCanvasPoint()
					.getX()) / 30;
			int dxInteger = (int) dx;

			roundingError += dx - (int) dx;

			if (roundingError > 1) {
				dxInteger++;
				roundingError--;
			} else if (roundingError < -1) {
				roundingError++;
				dxInteger--;
			}

			myBrainImage.setCoord(myBrainImage.getCoord() + dxInteger);
			updateLabel();
			repaint();

		}
	}
}

class BrainViewStatusHandler extends AbstractStatusHandler {

	public BrainViewStatusHandler(BrainViewer world) {
		super(world);
	}

	@Override
	protected String getStatusMessage(PInputEvent event) {
		return "Z Coord: " + getWorld().getZCoord();

	}

	@Override
	protected BrainViewer getWorld() {
		return (BrainViewer) super.getWorld();
	}
}
