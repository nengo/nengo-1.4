package ca.neo.ui.brainView;

import java.util.Iterator;

import ca.shu.ui.lib.handlers.AbstractStatusHandler;
import ca.shu.ui.lib.handlers.EventConsumer;
import ca.shu.ui.lib.objects.TextNode;
import ca.shu.ui.lib.world.World;
import ca.shu.ui.lib.world.WorldGround;
import ca.shu.ui.lib.world.WorldObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

public class BrainViewer extends World {

	private static final long serialVersionUID = 1L;

	AbstractBrainImage2D topView, sideView, frontView;

	public BrainViewer() {
		super("Brain View");

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

	@Override
	protected WorldGround createGround() {
		return new WorldGround(this) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void layoutChildren() {
				Iterator<?> it = getChildrenReference().iterator();
				int x = 0;
				double maxHeight = 0;

				while (it.hasNext()) {
					PNode obj = (PNode) it.next();
					if (obj.getHeight() > maxHeight) {
						maxHeight = obj.getHeight();
					}

				}

				it = getChildrenReference().iterator();
				while (it.hasNext()) {
					PNode obj = (PNode) it.next();

					obj.setOffset(x, maxHeight - obj.getHeight());
					x += obj.getWidth() + 10;

				}
			}
		};
	}
}

class BrainImageWrapper extends WorldObject {

	private static final long serialVersionUID = 1L;
	AbstractBrainImage2D myBrainImage;
	TextNode myLabel;

	public BrainImageWrapper(AbstractBrainImage2D brainImage) {
		super();
		myBrainImage = brainImage;
		addChild(new PImage(brainImage));

		myLabel = new TextNode();
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
	protected void layoutChildren() {
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
