package ca.shu.ui.lib.objects;

import ca.neo.ui.style.Style;
import ca.shu.ui.lib.world.impl.WorldObject;
import edu.umd.cs.piccolo.nodes.PPath;

public class GContextButton extends WorldObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	WorldObject node;

	PPath circle;

	public GContextButton(WorldObject node) {
		super();
		this.node = node;
		this.setDraggable(false);

		circle = PPath.createEllipse(0, 0, 25, 25);
		circle.setPaint(Style.FOREGROUND_COLOR);
		// circle.setStrokePaint(Color.white);
		this.setFrameVisible(false);
		circle.setPickable(false);

		addToLayout(circle, false);

	}

	Tooltip buttons;

	@Override
	public Tooltip getTooltipObject() {
//		G node = new GFramedNode();
		Tooltip buttons = new Buttons();

//		node.addToLayout(buttons);
//		node
//				.setOffset(this.getWidth() - node.getWidth(),
//						this.getHeight() + 10);

		// node.setFrameVisible(true);
		return buttons;
	}

	@Override
	public long getControlDelay() {
		return 200;
	}

	class Buttons extends Tooltip {
		private static final long serialVersionUID = 1L;

		@Override
		protected void initButtons() {
			this.setFrameVisible(false);
			addButton(new GButton("Expand", new Runnable() {
				public void run() {
					node.animateToScale(node.getScale() * 1.5, 1000);
				}
			}));

			addButton(new GButton("Shrink", new Runnable() {
				public void run() {
					node.animateToScale(node.getScale() / 1.5, 1000);
				}
			}));

			addButton(new GButton("Close", new Runnable() {
				public void run() {
					node.removeFromParent();
				}
			}));
		}

	}

}
