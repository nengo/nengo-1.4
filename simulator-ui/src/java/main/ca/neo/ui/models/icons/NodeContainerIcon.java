package ca.neo.ui.models.icons;

import ca.neo.ui.models.nodes.PNodeContainer;
import ca.neo.ui.style.Style;
import ca.shu.ui.lib.objects.GText;
import edu.umd.cs.piccolo.PNode;

public abstract class NodeContainerIcon extends ModelIcon {

	private static final long serialVersionUID = 1L;

	static final float MAX_SCALE = 1.5f;

	static final float MIN_SCALE = 0.5f;

	public abstract int getNodeCountNormalization();

	public NodeContainerIcon(PNodeContainer parent, PNode icon) {
		super(parent, icon);
		sizeLabel = new GText("Hello world!");
		sizeLabel.setFont(Style.FONT_SMALL);
		sizeLabel.setConstrainWidthToTextWidth(true);
		addChild(sizeLabel);
		layoutChildren();
		modelUpdated();
	}

	@Override
	public PNodeContainer getModelParent() {

		return (PNodeContainer) super.getModelParent();
	}

	private int myNumOfNodes;
	private GText sizeLabel;

	/**
	 * Scales the icon display size depending on how many nodes are contained
	 * within it
	 * 
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

	@Override
	protected void modelUpdated() {
		super.modelUpdated();
		updateIconScale();
	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();

		sizeLabel.setOffset(0, -(sizeLabel.getHeight() + 1));

		sizeLabel.moveToFront();
	}

}
