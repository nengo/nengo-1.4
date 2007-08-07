package ca.neo.ui.models.nodes.connectors;

import ca.neo.model.Termination;
import ca.neo.ui.models.PModel;
import ca.neo.ui.models.PModelConfigurable;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.icons.IconWrapper;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;
import ca.shu.ui.lib.objects.lines.LineIn;
import ca.shu.ui.lib.util.Util;

/**
 * Termination UI Object
 * 
 * @author Shu Wu
 * 
 */
public class PTermination extends PModelWidget {

	@Override
	public void destroy() {
		Util
				.Warning("Terminations can only be removed from the UI, not the Model. Projections will be removed.");
		super.destroy();
	}

	private static final long serialVersionUID = 1L;
	PNeoNode nodeParent;

	public PTermination(PNeoNode nodeParent, Termination term) {
		super(nodeParent, term);
		setName(term.getName());
		init();
	}

	public PTermination(PNeoNode nodeParent) {
		super(nodeParent);
		init();
	}

	private void init() {
		setIcon(new TermIcon(this));

	}

	public Termination getModelTermination() {
		return (Termination) getModel();
	}

	@Override
	public PropDescriptor[] getConfigSchema() {
		return null;
	}

	@Override
	public String getTypeName() {
		return "Termination";
	}

}

class TermIcon extends IconWrapper {

	public TermIcon(PModel parent) {
		super(parent, new LineIn());
		configureLabel(false);

		// TODO Auto-generated constructor stub
	}

}
