package ca.neo.ui.models.icons;

import ca.neo.ui.models.nodes.UINodeViewable;
import ca.shu.ui.lib.Style.Style;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.piccolo.primitives.Text;

/**
 * Icon for a Node Container. The size of this icon scales depending on the
 * number of nodes contained by the model.
 * 
 * @author Shu
 */
public abstract class NodeContainerIcon extends ModelIcon {

	private static final long serialVersionUID = 1L;

	public static final float MAX_SCALE = 1.5f;

	public static final float MIN_SCALE = 0.5f;

	private int myNumOfNodes = -1;

	private final Text sizeLabel;

	public NodeContainerIcon(UINodeViewable parent, WorldObject icon) {
		super(parent, icon);
		sizeLabel = new Text("");
		sizeLabel.setFont(Style.FONT_SMALL);
		sizeLabel.setConstrainWidthToTextWidth(true);
		addChild(sizeLabel);
		layoutChildren();
		modelUpdated();
	}

	/**
	 * Scales the icon display size depending on how many nodes are contained
	 * within it
	 */
	private void updateIconScale() {

		int numOfNodes = getModelParent().getNodesCount();

		if (myNumOfNodes == numOfNodes) {
			return;
		}
		myNumOfNodes = numOfNodes;

		sizeLabel.setText(myNumOfNodes + " nodes");

		float numOfNodesNormalized;
		if (numOfNodes >= getNodeCountNormalization())
			numOfNodesNormalized = 1;
		else {
			numOfNodesNormalized = (float) Math.sqrt((float) numOfNodes
					/ (float) getNodeCountNormalization());
		}

		float scale = MIN_SCALE
				+ (numOfNodesNormalized * (MAX_SCALE - MIN_SCALE));

		getIconReal().setScale(scale);

	}

	protected abstract int getNodeCountNormalization();

	@Override
	public void layoutChildren() {
		super.layoutChildren();

		sizeLabel.setOffset(0, -(sizeLabel.getHeight() + 1));

		sizeLabel.moveToFront();
	}

	@Override
	protected void modelUpdated() {
		super.modelUpdated();
		updateIconScale();
	}

	@Override
	public UINodeViewable getModelParent() {

		return (UINodeViewable) super.getModelParent();
	}

}
