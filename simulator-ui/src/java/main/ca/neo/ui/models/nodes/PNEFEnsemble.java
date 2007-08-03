package ca.neo.ui.models.nodes;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFEnsembleFactory;
import ca.neo.model.nef.impl.NEFEnsembleFactoryImpl;
import ca.neo.plot.Plotter;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.icons.EnsembleIcon;
import ca.neo.ui.models.nodes.connectors.PDecodedTermination;
import ca.neo.ui.models.nodes.connectors.PTermination;
import ca.neo.ui.style.Style;
import ca.neo.ui.views.objects.configurable.managers.DialogConfig;
import ca.neo.ui.views.objects.configurable.managers.PropertySet;
import ca.neo.ui.views.objects.configurable.struct.PTInt;
import ca.neo.ui.views.objects.configurable.struct.PTString;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.GText;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;

/**
 * A UI object for NEFEnsemble
 * 
 * @author Shu Wu
 * 
 */
public class PNEFEnsemble extends PNeoNode {
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

	@Override
	public PopupMenuBuilder constructMenu() {
		// TODO Auto-generated method stub
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

		if (getNEFEnsemble().isCollectingSpikes())
			menu.addAction(new StopCollectSpikes());
		else
			menu.addAction(new StartCollectSpikes());

		// Termination

		menu.addAction(new AddDecodedTerminationAction());

		return menu;
	}

	/**
	 * Adds a decoded termination to the UI and Ensemble Model
	 * 
	 * The UI is used to configure it
	 * 
	 * @return PTermination created, null if not
	 */
	public PTermination createDecodedTermintation() {
		PDecodedTermination termUI = new PDecodedTermination(this);
		new DialogConfig(termUI);
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

	/*
	 * @return Ensemble Model
	 */
	public NEFEnsemble getNEFEnsemble() {
		return (NEFEnsemble) getModel();
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

		getNEFEnsemble().collectSpikes(collectingSpikes);
	}

	private void init() {
		setIcon(new EnsembleIcon(this));
	}

	@Override
	protected Node configureModel(PropertySet prop) {
		try {
			GText loadingText = new GText(
					"WARNING: Ensemble Model creation in progress");
			loadingText.setFont(Style.FONT_XXLARGE);
			addChild(loadingText);

			NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();

			String name = (String) prop.getProperty(pName);

			Integer numOfNeurons = (Integer) prop.getProperty(pNumOfNeurons);
			Integer dimensions = (Integer) prop.getProperty(pDim);
			String storageName = (String) prop.getProperty(pStorageName);

			// setName(name);

			NEFEnsemble ensemble = ef.make(name, numOfNeurons, dimensions,
					storageName, false);

			loadingText.removeFromParent();
			return ensemble;
		} catch (StructuralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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

	class StartCollectSpikes extends ReversableAction {

		private static final long serialVersionUID = 1L;

		public StartCollectSpikes() {
			super("Collect Spikes");
		}

		@Override
		protected void action() throws ActionException {
			if (getNEFEnsemble().isCollectingSpikes())
				throw new ActionException("Already collecting spikes");
			else
				getNEFEnsemble().collectSpikes(true);
		}

		@Override
		protected void undo() {
			getNEFEnsemble().collectSpikes(false);

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
			if (!getNEFEnsemble().isCollectingSpikes())
				throw new ActionException("Already not collecting spikes");
			else
				getNEFEnsemble().collectSpikes(false);

		}

		@Override
		protected void undo() throws ActionException {
			getNEFEnsemble().collectSpikes(true);

		}

	}

}
