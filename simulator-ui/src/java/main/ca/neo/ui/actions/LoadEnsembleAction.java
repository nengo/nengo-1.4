package ca.neo.ui.actions;

import ca.neo.model.Ensemble;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.ui.models.nodes.PEnsemble;
import ca.neo.ui.models.nodes.PNEFEnsemble;
import ca.shu.ui.lib.util.Util;

public abstract class LoadEnsembleAction extends LoadObjectAction {

	private static final long serialVersionUID = 1L;

	public LoadEnsembleAction(String actionName) {
		super("Load Ensemble from file", actionName);
		// TODO Auto-generated constructor stub
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
			gotEnsemble(ensembleUI);

	}

	protected abstract void gotEnsemble(PEnsemble ensemble);

}