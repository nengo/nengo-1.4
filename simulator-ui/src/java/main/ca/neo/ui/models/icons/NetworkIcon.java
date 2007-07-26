package ca.neo.ui.models.icons;

import edu.umd.cs.piccolo.nodes.PImage;

public class NetworkIcon extends Icon {
	private static final long serialVersionUID = 1L;

	public NetworkIcon() {
		super(new PImage("images/NetworkIcon.gif"), "Network", 0.7f);

		// innerNode.setScale(0.7);
		// System.out.println(innerNode.getFullBounds());
		// setBounds(innerNode.getFullBounds());

		// super(PPath.createEllipse(0, 0, 50, 50), "Network");

	}

}

