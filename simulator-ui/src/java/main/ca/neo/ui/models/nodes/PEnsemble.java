package ca.neo.ui.models.nodes;

import ca.neo.model.Ensemble;
import ca.neo.model.Node;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.plot.Plotter;
import ca.neo.ui.models.icons.EnsembleIcon;
import ca.neo.ui.models.viewers.EnsembleViewer;
import ca.neo.ui.models.viewers.NodeViewer;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;

public class PEnsemble extends PNodeContainer {

	private static final long serialVersionUID = 1L;

	public PEnsemble() {
		super();
		init();
	}

	public PEnsemble(Node model) {
		super(model);
		init();
	}

	@Override
	public PropDescriptor[] getConfigSchema() {
		Util.Error("Ensemble has not been implemented yet");
		return null;
	}

	/*
	 * @return Ensemble Model
	 */
	public Ensemble getModel() {
		return (Ensemble) super.getModel();
	}

	@Override
	public String getTypeName() {

		return "Ensemble";
	}

	private void init() {
		setIcon(new EnsembleIcon(this));
	}

	@Override
	protected NodeViewer createNodeViewerInstance() {
		return new EnsembleViewer(this);
	}

}
