package ca.neo.ui.models.nodes.connectors;

import ca.neo.model.Termination;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.icons.IconWrapper;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.lines.ILineAcceptor;
import ca.shu.ui.lib.objects.lines.LineEnd;
import ca.shu.ui.lib.objects.lines.LineInIcon;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;

/**
 * Termination UI Object
 * 
 * @author Shu Wu
 * 
 */
public class PTermination extends PModelWidget implements ILineAcceptor {

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

		/*
		 * Set up the Icon
		 */

		IconWrapper icon = new IconWrapper(this, new LineInIcon());
		icon.configureLabel(false);

		setIcon(icon);

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

	@Override
	public PopupMenuBuilder constructMenu() {
		// TODO Auto-generated method stub
		PopupMenuBuilder menu = super.constructMenu();

		if (lineEnd != null) {
			menu.addAction(new RemoveConnectionAction("Remove connection"));
		}
		return menu;
	}

	class RemoveConnectionAction extends StandardAction {
		private static final long serialVersionUID = 1L;

		public RemoveConnectionAction(String actionName) {
			super("Remove connection from Termination", actionName);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void action() throws ActionException {
			lineEnd.destroy();
			lineEnd = null;
		}
	}

	LineEnd lineEnd;

	public boolean setLineEnd(LineEnd lineEnd) {
		this.lineEnd = lineEnd;
		return true;
	}

	public void removeLineEnd() {
		lineEnd = null;

	}
}
