package ca.shu.ui.lib.objects;

import java.util.Collection;
import java.util.Vector;

import ca.shu.ui.lib.world.WorldObject;

public abstract class Tooltip extends WorldObject {
	Collection<Button> buttons;

	WorldObject buttonsNode;

	double xPos = 0;

	double yPos = 0;

	public Tooltip() {
		super();
		buttons = new Vector<Button>();

		this.setSelectable(false);

		buttonsNode = new WorldObject();

		buttonsNode.setSelectable(false);

		initButtons();

		addChild(buttonsNode);

		// this.setF
	}

	@Override
	public Tooltip getTooltip() {
		return null;
	}

	protected Button addButton(Button btn) {
		btn.setOffset(xPos, yPos);
		xPos += btn.getWidth() + 10;

		buttons.add(btn);
		buttonsNode.addChild(btn);

		buttonsNode.setBounds(buttonsNode.getFullBounds());

		return btn;

	}

	protected void initButtons() {

	}

}
