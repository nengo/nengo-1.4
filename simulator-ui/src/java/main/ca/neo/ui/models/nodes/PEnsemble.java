package ca.neo.ui.models.nodes;

import ca.neo.model.Node;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.icons.EnsembleIcon;
import ca.neo.ui.models.viewers.EnsembleViewer;
import ca.neo.ui.models.viewers.NodeViewer;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;
import ca.shu.ui.lib.util.Util;

public class PEnsemble extends PNodeContainer {

	public PEnsemble() {
		super();
		init();
	}

	public PEnsemble(Node model) {
		super(model);
		init();
	}

	private static final long serialVersionUID = 1L;

	private void init() {
		setIcon(new EnsembleIcon(this));
	}

	@Override
	protected NodeViewer createNodeViewerInstance() {
		return new EnsembleViewer(this);
	}

	@Override
	public String getTypeName() {

		return "Ensemble";
	}

	@Override
	public PropDescriptor[] getConfigSchema() {
		Util.Error("Ensemble has not been implemented yet");
		return null;
	}
}
