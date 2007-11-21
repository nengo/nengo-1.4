package ca.neo.ui.models.nodes.widgets;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import ca.neo.io.MatlabExporter;
import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.Probeable;
import ca.neo.model.SimulationException;
import ca.neo.ui.actions.PlotTimeSeries;
import ca.neo.ui.actions.PlotTimeSeriesTau;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.nodes.UIEnsemble;
import ca.neo.ui.models.tooltips.PropertyPart;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.neo.ui.models.viewers.EnsembleViewer;
import ca.neo.util.Probe;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.activities.AbstractActivity;
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

	public UIStateProbe(UINeoNode nodeAttachedTo, String state)
			throws SimulationException {
		super(nodeAttachedTo);

		/*
		 * Creates the probe
		 */
		Node node = nodeAttachedTo.getModel();
		Probe probe;
		try {
			if (nodeAttachedTo.getParentViewer() instanceof EnsembleViewer) {
				EnsembleViewer ensembleViewer = (EnsembleViewer) nodeAttachedTo
						.getParentViewer();

				UIEnsemble ensemble = ensembleViewer.getViewerParent();
				Network network = ensemble.getParentNetwork();

				probe = network.getSimulator().addProbe(ensemble.getName(),
						(Probeable) node, state, true);

			} else {
				probe = nodeAttachedTo.getParentNetwork().getSimulator()
						.addProbe(node.getName(), state, true);
			}
		} catch (SimulationException exception) {
			// nodeAttachedTo.popupTransientMsg("Could not add Probe (" + state
			// + ") added to Simulator");
			throw exception;
		}

		nodeAttachedTo.popupTransientMsg("Probe (" + state
				+ ") added to Simulator");

		setModel(probe);

	}

	@Override
	public String getTypeName() {
		return "Probe";
	}

	public UIStateProbe(UINeoNode nodeAttachedTo, Probe probeModel) {
		super(nodeAttachedTo, probeModel);
	}

	@Override
	protected TooltipBuilder constructTooltips() {
		TooltipBuilder tooltips = new TooltipBuilder("Probe");
		tooltips.addPart(new PropertyPart("Attached to", getModel()
				.getStateName()));
		return tooltips;
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

			name = JOptionPane
					.showInputDialog("Enter name of file to export to: ");

			(new AbstractActivity("Exporting to matlab") {
				@Override
				public void doActivity() {
					exportToMatlab(name);
				}

			}).startThread();

		}
	}

	@Override
	protected void prepareForDestroy() {

		super.prepareForDestroy();

		try {
			getProbeParent().getParentNetwork().getSimulator().removeProbe(
					getModel());
			popupTransientMsg("Probe removed from Simulator");
		} catch (SimulationException e) {
			UserMessages.showError("Could not remove probe");
		}

		getProbeParent().removeProbe(this);

	}

	@Override
	protected void constructMenu(PopupMenuBuilder menu) {
		super.constructMenu(menu);

		menu.addSection("Probe");
		MenuBuilder plotMenu = menu.createSubMenu("plot");
		plotMenu.addAction(new PlotTimeSeries(getModel().getData(), getName()));
		plotMenu.addAction(new PlotTimeSeriesTau(getModel().getData(),
				getName()));

		MenuBuilder exportMenu = menu.createSubMenu("export data");
		exportMenu.addAction(new ExportToMatlabAction());

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
	public void setModel(Object model) {
		super.setModel(model);

		if (model instanceof Probe) {
			setName(((Probe) model).getStateName());
		} else {
			throw new IllegalArgumentException("Wrong model type");
		}
	}

}
