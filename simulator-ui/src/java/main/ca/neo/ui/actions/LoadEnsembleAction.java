package ca.neo.ui.actions;

import ca.neo.model.Ensemble;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.ui.models.INodeContainer;
import ca.neo.ui.models.nodes.PEnsemble;
import ca.neo.ui.models.nodes.PNEFEnsemble;
import ca.shu.ui.lib.util.Util;

public class LoadEnsembleAction extends LoadObjectAction {

	private static final long serialVersionUID = 1L;
	INodeContainer nodeContainer;

	public LoadEnsembleAction(String actionName, INodeContainer nodeContainer) {
		super("Load Ensemble from file", actionName);
		this.nodeContainer = nodeContainer;
	}

	@Override
	protected void processObject(Object objLoaded) {

		PEnsemble ensembleUI = null;
		if (objLoaded instanceof Ensemble) {
			ensembleUI = new PEnsemble((Ensemble) objLoaded);
		} else if (objLoaded instanceof NEFEnsemble) {
			ensembleUI = new PNEFEnsemble((NEFEnsemble) objLoaded);
		} else {
			Util.Error("Could not load Ensemble file");
		}
		if (ensembleUI != null)
			nodeContainer.addNeoNode(ensembleUI);

	}

}