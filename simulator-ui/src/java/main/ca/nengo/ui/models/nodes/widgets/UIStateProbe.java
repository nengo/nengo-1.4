package ca.nengo.ui.models.nodes.widgets;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import ca.nengo.io.MatlabExporter;
import ca.nengo.model.Network;
import ca.nengo.model.Node;
import ca.nengo.model.Probeable;
import ca.nengo.model.SimulationException;
import ca.nengo.ui.actions.PlotAdvanced;
import ca.nengo.ui.actions.PlotTimeSeries;
import ca.nengo.ui.models.UINeoNode;
import ca.nengo.ui.models.nodes.UIEnsemble;
import ca.nengo.ui.models.tooltips.TooltipBuilder;
import ca.nengo.ui.models.viewers.EnsembleViewer;
import ca.nengo.util.Probe;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.activities.TrackedAction;
import ca.shu.ui.lib.util.UserMessages;
import ca.shu.ui.lib.util.menus.MenuBuilder;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;

/**
 * UI Wrapper for a Simulator Probe
 * 
 * @author Shu Wu
 */
public class UIStateProbe extends UIProbe {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Probe createProbe(UINeoNode nodeAttachedTo, String state)
			throws SimulationException {

		/*
		 * Creates the probe
		 */
		Node node = nodeAttachedTo.getModel();
		Probe probe;
		try {
			if (nodeAttachedTo.getParentViewer() instanceof EnsembleViewer) {
				EnsembleViewer ensembleViewer = (EnsembleViewer) nodeAttachedTo.getParentViewer();

				UIEnsemble ensemble = ensembleViewer.getViewerParent();
				Network network = ensemble.getNetworkParent().getModel();

				probe = network.getSimulator().addProbe(ensemble.getName(), (Probeable) node,
						state, true);

				nodeAttachedTo.showPopupMessage("Probe (" + state + ") added to Simulator");

			} else {
				probe = nodeAttachedTo.getNetworkParent().getSimulator().addProbe(node.getName(),
						state, true);
			}
		} catch (SimulationException exception) {
			// nodeAttachedTo.popupTransientMsg("Could not add Probe (" + state
			// + ") added to Simulator");
			throw exception;
		}
		return probe;
	}

	public UIStateProbe(UINeoNode nodeAttachedTo, Probe probeModel) {
		super(nodeAttachedTo, probeModel);
	}

	public UIStateProbe(UINeoNode nodeAttachedTo, String state) throws SimulationException {
		super(nodeAttachedTo, createProbe(nodeAttachedTo, state));
	}

	@Override
	protected void constructMenu(PopupMenuBuilder menu) {
		super.constructMenu(menu);

		menu.addSection("Probe");
		MenuBuilder plotMenu = menu.addSubMenu("plot");
		plotMenu.addAction(new PlotTimeSeries(getModel().getData(), getName()));
		plotMenu.addAction(new PlotAdvanced(getModel().getData(), getName()));

		MenuBuilder exportMenu = menu.addSubMenu("export data");
		exportMenu.addAction(new ExportToMatlabAction());

	}

	@Override
	public void doubleClicked() {
		(new PlotTimeSeries(getModel().getData(), getName())).doAction();
	}

	@Override
	protected void constructTooltips(TooltipBuilder tooltips) {
		super.constructTooltips(tooltips);
		tooltips.addProperty("Attached to", getModel().getStateName());
	}

	@Override
	protected void prepareToDestroyModel() {
		try {
			getProbeParent().getNetworkParent().getSimulator().removeProbe(getModel());
			getProbeParent().showPopupMessage("Probe removed from Simulator");
		} catch (SimulationException e) {
			UserMessages.showError("Could not remove probe: " + e.getMessage());
		}

		super.prepareToDestroyModel();
	}

	/**
	 * @param name
	 *            prefix of the fileName to be exported to
	 * @throws IOException
	 */
	public void exportToMatlab(String name) {
		MatlabExporter me = new MatlabExporter();
		me.add(getName(), getModel().getData());
		try {
			me.write(new File(name + ".mat"));
		} catch (IOException e) {
			UserMessages.showError("Could not export file: " + e.toString());
		}
	}

	@Override
	public Probe getModel() {
		return (Probe) super.getModel();
	}

	@Override
	public String getTypeName() {
		return "State Probe";
	}

	@Override
	public void modelUpdated() {
		setName(((Probe) getModel()).getStateName());
	}

	/**
	 * Action for exporting to MatLab
	 * 
	 * @author Shu Wu
	 */
	class ExportToMatlabAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		String name;

		public ExportToMatlabAction() {
			super("Matlab");
		}

		@Override
		protected void action() throws ActionException {

			name = JOptionPane.showInputDialog("Enter name of file to export to: ");

			(new TrackedAction("Exporting to matlab") {

				private static final long serialVersionUID = 1L;

				@Override
				protected void action() throws ActionException {
					exportToMatlab(name);
				}

			}).doAction();

		}
	}
}
