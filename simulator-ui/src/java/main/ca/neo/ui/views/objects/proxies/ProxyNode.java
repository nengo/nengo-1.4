package ca.neo.ui.views.objects.proxies;

import ca.neo.model.Node;
import ca.sw.graphics.nodes.lines.LineEndWell;
import ca.sw.graphics.nodes.lines.LineIn;

public abstract class ProxyNode extends ProxyGeneric<Node> {

	@Override
	protected void updateSymbol() {
		super.updateSymbol();

		if (proxy instanceof Node) {
			if (lineEndWell == null) {
				lineEndWell = new LineEndWell();

				lineEndWell.setOffset(icon.getBounds().getMaxY() + 5, 0);
				addChild(lineEndWell);

			}

			if (lineIn == null) {
				lineIn = new LineIn();

				lineIn.setOffset(icon.getBounds().getMinY() - 15
						- lineIn.getWidth(), 0);
				addChild(lineIn);
			}

			setBounds(parentToLocal(getFullBounds()));
		}

	}

}
