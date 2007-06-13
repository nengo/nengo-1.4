package ca.neo.ui.views;

import ca.neo.ui.views.factories.SymbolDragHandler;
import ca.sw.graphics.basics.GButton;
import ca.sw.graphics.nodes.WorldObject;
import ca.sw.graphics.objects.controls.GControlBar;
import ca.sw.util.Util;
import edu.umd.cs.piccolo.PNode;

public class NeoCanvas extends WorldObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2580524986395972714L;

	WorldObject canvasConfig;

	public NeoCanvas() {
		super("Canvas");

		// this.setWidth(500);
		// setBounds(0,0,200,20000);

		// this.addItem(GNodeFactory.network.createIcon());
		// this.addItem(GNodeFactory.ensemble.createIcon());
		// this.addItem(PPath.createRectangle(0, 0, 50, 50));

		System.out.println("loading CanvasConfig");
		canvasConfig = (WorldObject) Util.loadObject("CanvasConfig");
		System.out.println("finished loading CanvasConfig");

		if (canvasConfig == null) {
			clearCanvas();
		} else {
			addToLayout(canvasConfig);
		}
		canvasConfig.setPickable(false);

		this.setFrameVisible(true);
		// addItem();

	}

	// public String getName() {
	// return "DefaultCanvas";
	// }

	public void addItem(PNode item) {

		canvasConfig.addToLayout(item);
		this.setBounds(this.getFullBounds());

		Util.saveObject(canvasConfig, "CanvasConfig");

	}

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		super.initialize();

		addInputEventListener(new SymbolDragHandler(this));
	}

	@Override
	public WorldObject getControls() {
		// TODO Auto-generated method stub
		return new CanvasControls(this);

	}

	public void clearCanvas() {
		if (canvasConfig != null)
			canvasConfig.removeFromParent();

		canvasConfig = new WorldObject();
		canvasConfig.getLayoutManager().setLeftPadding(0);
		canvasConfig.getLayoutManager().setVerticalPadding(0);
		addToLayout(canvasConfig);
	}
}

class CanvasControls extends GControlBar {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	NeoCanvas canvas;

	CanvasControls(NeoCanvas canvas) {
		super();
		this.canvas = canvas;
	}

	@Override
	protected void initButtons() {
		addButton(new GButton("Clear Canvas", new Runnable() {
			public void run() {
				canvas.clearCanvas();
			}
		}));

	}

}
