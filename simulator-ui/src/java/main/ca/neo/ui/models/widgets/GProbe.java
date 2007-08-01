package ca.neo.ui.models.widgets;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import ca.neo.io.MatlabExporter;
import ca.neo.model.Node;
import ca.neo.model.SimulationException;
import ca.neo.plot.Plotter;
import ca.neo.ui.models.PModel;
import ca.neo.ui.models.PModelNode;
import ca.neo.ui.models.icons.IconWrapper;
import ca.neo.ui.views.objects.configurable.IConfigurable;
import ca.neo.util.Probe;
import ca.neo.util.TimeSeries;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;

public class GProbe extends PModel {

	private static final long serialVersionUID = 1L;
	PModelNode nodeProxy;

	public GProbe(PModelNode nodeProxy, String state) {
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
			removeModel();
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
	public void removeModel() {
		// TODO Auto-generated method stub
		super.removeModel();
		nodeProxy.removeProbe(this);

		Util.Error("Ability to remove probes functionality is not implemented");
	}

	class ExportToMatlabAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ExportToMatlabAction() {
			super("Matlab");
			// TODO Auto-generated constructor stub
		}

		public void actionPerformed(ActionEvent arg0) {
			String name = JOptionPane
					.showInputDialog("Enter name of file to export to: ");

			exportToMatlab(name);

		}
	}

	class PlotAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public PlotAction() {
			super("Plot");
			// TODO Auto-generated constructor stub
		}

		public void actionPerformed(ActionEvent arg0) {
			plot();

		}

	}

	class PlotTauFilterAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public PlotTauFilterAction() {
			super("Plot w/ filter");
		}

		public void actionPerformed(ActionEvent e) {
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
