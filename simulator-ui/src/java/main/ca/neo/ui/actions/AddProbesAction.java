package ca.neo.ui.actions;

import java.util.Collection;
import java.util.HashMap;

import javax.swing.JOptionPane;

import ca.neo.model.SimulationException;
import ca.neo.ui.models.UIModel;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.nodes.widgets.UISimulatorProbe;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.UserMessages;

/**
 * Action for adding probes to a collection of nodes
 * 
 * @author Shu Wu
 */
public class AddProbesAction extends ReversableAction {

	private static final long serialVersionUID = 1;

	private HashMap<UINeoNode, UISimulatorProbe> myCreatedProbesMap;

	private Collection<UIModel> myNodes;

	public AddProbesAction(Collection<UIModel> nodes) {
		super("Add probes");

		this.myNodes = nodes;

	}

	@Override
	protected void action() throws ActionException {
		myCreatedProbesMap = new HashMap<UINeoNode, UISimulatorProbe>(myNodes
				.size());

		String stateName = JOptionPane.showInputDialog(UIEnvironment
				.getInstance(), "State name to probe (Case Sensitive): ",
				"Adding probes", JOptionPane.QUESTION_MESSAGE);

		if (stateName != null && !stateName.equals("")) {
			int success = 0;
			int failed = 0;

			for (UIModel model : myNodes) {
				if (model instanceof UINeoNode) {
					UINeoNode node = (UINeoNode) model;
					UISimulatorProbe probeCreated;

					try {
						probeCreated = node.addProbe(stateName);
						myCreatedProbesMap.put(node, probeCreated);
						success++;
					} catch (SimulationException e) {

						failed++;
					}

				}

			}
			if (failed > 0) {
				UserMessages
						.showWarning(success
								+ "probes were successfully added. <BR> However it was not added to "
								+ failed
								+ " nodes. The state name specified may not exist on those nodes.");
			}
		}

	}

	@Override
	protected void undo() throws ActionException {
		for (UIModel model : myNodes) {
			if (model instanceof UINeoNode) {
				UINeoNode node = (UINeoNode) model;
				UISimulatorProbe probeCreated = myCreatedProbesMap.get(node);
				node.removeProbe(probeCreated);
			}

		}

	}

}