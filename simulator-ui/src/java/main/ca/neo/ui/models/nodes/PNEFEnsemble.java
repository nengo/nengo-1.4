package ca.neo.ui.models.nodes;

import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFEnsembleFactory;
import ca.neo.model.nef.impl.NEFEnsembleFactoryImpl;
import ca.neo.plot.Plotter;
import ca.neo.ui.models.nodes.connectors.PDecodedTermination;
import ca.neo.ui.models.nodes.connectors.PTermination;
import ca.neo.ui.views.objects.configurable.managers.UserConfig;
import ca.neo.ui.views.objects.configurable.managers.PropertySet;
import ca.neo.ui.views.objects.configurable.struct.PTInt;
import ca.neo.ui.views.objects.configurable.struct.PTString;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;

/**
 * A UI object for NEFEnsemble
 * 
 * @author Shu Wu
 * 
 */
public class PNEFEnsemble extends PEnsemble {
	private static final long serialVersionUID = 1L;

	static final PropDescriptor pDim = new PTInt("Dimensions");

	static final PropDescriptor pName = new PTString("Name");

	static final PropDescriptor pNumOfNeurons = new PTInt("Number of Neurons");

	static final PropDescriptor pStorageName = new PTString("Storage Name");

	static final String typeName = "NEFEnsemble";

	static final PropDescriptor[] zProperties = { pName, pNumOfNeurons, pDim,
			pStorageName };

	boolean collectingSpikes = false;

	String name = "Unamed Ensemble";

	String storageName = "Unamed Ensemble";

	public PNEFEnsemble() {
		super();
		init();
	}

	public PNEFEnsemble(NEFEnsemble model) {
		super(model);
		init();
	}

	/**
	 * Adds a decoded termination to the UI and Ensemble Model
	 * 
	 * 
	 * The UI is used to configure it
	 * @return PTermination created, null if not
	 */
	public PTermination createDecodedTermintation() {
		PDecodedTermination termUI = new PDecodedTermination(this);
		new UserConfig(termUI);
		if (termUI.isConfigured()) {
			addWidget(termUI);
			return termUI;
		}
		return null;
	}

	@Override
	public PropDescriptor[] getConfigSchema() {
		// TODO Auto-generated method stub
		return zProperties;
	}

	class AddDecodedTerminationAction extends ReversableAction {

		private static final long serialVersionUID = 1L;

		PTermination addedTermination;

		public AddDecodedTerminationAction() {
			super("Add decoded termination");
		}

		@Override
		protected void action() throws ActionException {
			PTermination term = createDecodedTermintation();
			if (term == null)
				throw new ActionException(
						"Could not create decoded termination");

			else
				addedTermination = term;
		}

		@Override
		protected void undo() throws ActionException {
			addedTermination.destroy();

		}

	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return typeName;
	}

	public boolean isCollectingSpikes() {
		return collectingSpikes;
	}

	public void setCollectingSpikes(boolean isCollectingSpikes) {
		this.collectingSpikes = isCollectingSpikes;

		getModel().collectSpikes(collectingSpikes);
	}

	private void init() {

	}

	@Override
	protected Node configureModel(PropertySet prop) {
		try {

			NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();

			String name = (String) prop.getProperty(pName);

			Integer numOfNeurons = (Integer) prop.getProperty(pNumOfNeurons);
			Integer dimensions = (Integer) prop.getProperty(pDim);
			String storageName = (String) prop.getProperty(pStorageName);

			NEFEnsemble ensemble = ef.make(name, numOfNeurons, dimensions,
					storageName, false);

			return ensemble;
		} catch (StructuralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public PopupMenuBuilder constructMenu() {

		PopupMenuBuilder menu = super.constructMenu();

		menu.addSection("NEFEnsemble");

		MenuBuilder plotMenu = menu.createSubMenu("Plot");

		plotMenu.addAction(new StandardAction("Ensemble") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void action() {
				Plotter.plot((NEFEnsemble) getModel());
			}

		});

		plotMenu.addAction(new StandardAction("Origin X") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void action() {
				Plotter.plot((NEFEnsemble) getModel(), NEFEnsemble.X);
			}
		});

		if (getModel().isCollectingSpikes())
			menu.addAction(new StopCollectSpikes());
		else
			menu.addAction(new StartCollectSpikes());

		// Termination
		menu.addAction(new AddDecodedTerminationAction());
		return menu;
	}

	class StartCollectSpikes extends ReversableAction {

		private static final long serialVersionUID = 1L;

		public StartCollectSpikes() {
			super("Collect Spikes");
		}

		@Override
		protected void action() throws ActionException {
			if (getModel().isCollectingSpikes())
				throw new ActionException("Already collecting spikes");
			else
				getModel().collectSpikes(true);
		}

		@Override
		protected void undo() {
			getModel().collectSpikes(false);

		}

	}

	class StopCollectSpikes extends ReversableAction {
		private static final long serialVersionUID = 1L;

		public StopCollectSpikes() {
			super("Stop Collecting Spikes");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void action() throws ActionException {
			if (!getModel().isCollectingSpikes())
				throw new ActionException("Already not collecting spikes");
			else
				getModel().collectSpikes(false);

		}

		@Override
		protected void undo() throws ActionException {
			getModel().collectSpikes(true);

		}

	}

	@Override
	public NEFEnsemble getModel() {
		return (NEFEnsemble) super.getModel();
	}
}
