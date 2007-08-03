package ca.neo.ui.models.nodes.widgets;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import ca.neo.io.MatlabExporter;
import ca.neo.model.Node;
import ca.neo.model.SimulationException;
import ca.neo.plot.Plotter;
import ca.neo.ui.models.PModel;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.icons.IconWrapper;
import ca.neo.util.Probe;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.widgets.TrackedActivity;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;

public class GProbe extends PModel {

	private static final long serialVersionUID = 1L;
	PNeoNode nodeProxy;

	public GProbe(PNeoNode nodeProxy, String state) {
		super();
		this.nodeProxy = nodeProxy;

		setDraggable(false);
		setName(state);

		/*
		 * Create icon
		 */
		IconWrapper icon = new ProbeIcon(this);
		icon.configureLabel(false);
		icon.setLabelVisible(false);
		setIcon(icon);

		/*
		 * Create the node
		 */
		Node node = nodeProxy.getNode();

		try {
			Probe probe = nodeProxy.getNetworkModel().getSimulator().addProbe(
					node.getName(), state, true);

			setModel(probe);
		} catch (SimulationException e) {
			destroy();
			Util.Error("Could not add probe: " + e.toString());
		}

	}

	@Override
	public PopupMenuBuilder constructMenu() {
		// TODO Auto-generated method stub
		PopupMenuBuilder menu = super.constructMenu();

		menu.addSection("Probe");
		MenuBuilder plotMenu = menu.createSubMenu("plot");
		plotMenu.addAction(new PlotAction());
		plotMenu.addAction(new PlotTauFilterAction());

		MenuBuilder exportMenu = menu.createSubMenu("export data");
		exportMenu.addAction(new ExportToMatlabAction());

		return menu;
	}

	/**
	 * @param name
	 *            prefix of the fileName to be exported to
	 * @throws IOException
	 */
	public void exportToMatlab(String name) {
		MatlabExporter me = new MatlabExporter();
		me.add(getName(), getProbe().getData());
		try {
			me.write(new File(name + ".mat"));
		} catch (IOException e) {
			Util.Error("Could not export file: " + e.toString());
		}
	}

	/**
	 * 
	 * @return Probe object
	 */
	public Probe getProbe() {
		return (Probe) getModel();
	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Probe";
	}

	/**
	 * Plots the probe data
	 */
	public void plot() {
		Plotter.plot(getProbe().getData(), getName());
	}

	/**
	 * @param tauFilter
	 *            Time constant of display filter (s)
	 */
	public void plot(float tauFilter) {
		Plotter.plot(getProbe().getData(), tauFilter, getName());
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
		nodeProxy.removeProbe(this);

		Util.Error("Ability to remove probes functionality is not implemented");
	}

	class ExportToMatlabAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public ExportToMatlabAction() {
			super("Matlab");
			// TODO Auto-generated constructor stub
		}

		String name;

		@Override
		protected void action() throws ActionException {

			name = JOptionPane
					.showInputDialog("Enter name of file to export to: ");

			(new TrackedActivity("Exporting to matlab") {
				@Override
				public void doActivity() {
					exportToMatlab(name);
				}

			}).startAsThread();

		}
	}

	class PlotAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public PlotAction() {
			super("Plot");
		}

		@Override
		protected void action() throws ActionException {
			plot();
		}

	}

	class PlotTauFilterAction extends StandardAction {

		private static final long serialVersionUID = 1L;

		public PlotTauFilterAction() {
			super("Plot w/ filter");
		}

		@Override
		protected void action() throws ActionException {
			try {
				float tauFilter = new Float(
						JOptionPane
								.showInputDialog("Time constant of display filter (s): "));
				plot(tauFilter);
			} catch (java.lang.NumberFormatException exception) {
				Util.Warning("Could not parse number");
			}

		}
	}

}
