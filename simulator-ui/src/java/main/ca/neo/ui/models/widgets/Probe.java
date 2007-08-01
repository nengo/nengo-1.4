package ca.neo.ui.models.widgets;

import ca.neo.ui.models.PModel;
import ca.neo.ui.models.PModelNode;
import ca.neo.ui.models.icons.IconWrapper;
import ca.shu.ui.lib.util.Util;

public class Probe extends PModel {

	PModelNode node;

	public Probe(PModelNode node) {
		super();
		this.node = node;

		IconWrapper icon = new ProbeIcon(this);
		icon.configureLabel(false);
		icon.setLabelVisible(false);
		setIcon(icon);

		// node.getNetworkModel().getSimulator().addProbe(nodeName, state,
		// record)
		Util.Warning("Probe functionality is incomplete");

		setName("probe");
	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Probe";
	}

}
