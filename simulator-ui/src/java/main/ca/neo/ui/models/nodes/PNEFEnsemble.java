package ca.neo.ui.models.nodes;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
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
import ca.shu.ui.lib.objects.GText;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;

/**
 * A UI object for NEFEnsemble
 * 
 * @author Shu Wu
 * 
 */
public class PNEFEnsemble extends PNeoNode {
	public PNEFEnsemble(NEFEnsemble model) {
		super(model);
		init();
	}

	public PNEFEnsemble() {
		super();
		init();
	}

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

	private void init() {
		setIcon(new EnsembleIcon(this));
	}

	/*
	 * Adds a decoded termination to the UI and Ensemble Model
	 * 
	 * The UI is used to configure it
	 * 
	 */
	public PTermination createDecodedTermintation() {
		PDecodedTermination termUI = new PDecodedTermination(this);
		new DialogConfig(termUI);
		if (termUI.isConfigured()) {
			addWidget(termUI);
		}
		return termUI;
	}

	class CollectSpikesAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public CollectSpikesAction() {
			super();
			updateState();
		}

		public void actionPerformed(ActionEvent e) {
			setCollectingSpikes(!isCollectingSpikes());
			updateState();
		}

		public void updateState() {
			if (isCollectingSpikes()) {
				putValue(Action.NAME, "Stop collecting spikes");
			} else {
				putValue(Action.NAME, "Collect spikes");
			}
			repaint();
		}
	}

	@Override
	public PopupMenuBuilder constructMenu() {
		// TODO Auto-generated method stub
		PopupMenuBuilder menu = super.constructMenu();
		menu.addSection("NEFEnsemble");

		MenuBuilder plotMenu = menu.createSubMenu("Plot");

		plotMenu.addAction(new AbstractAction("Ensemble") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Plotter.plot((NEFEnsemble) getModel());
			}
		});

		plotMenu.addAction(new AbstractAction("Origin X") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Plotter.plot((NEFEnsemble) getModel(), NEFEnsemble.X);
			}
		});

		menu.addAction(new CollectSpikesAction());

		// Termination

		menu.addAction(new AbstractAction("Add decoded termination") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				createDecodedTermintation();
			}
		});

		return menu;
	}

	/*
	 * @return Ensemble Model
	 */
	public NEFEnsemble getNEFEnsemble() {
		return (NEFEnsemble) getModel();
	}

	@Override
	public PropDescriptor[] getConfigSchema() {
		// TODO Auto-generated method stub
		return zProperties;
	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return typeName;
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

	public boolean isCollectingSpikes() {
		return collectingSpikes;
	}

	public void setCollectingSpikes(boolean isCollectingSpikes) {
		this.collectingSpikes = isCollectingSpikes;

		getNEFEnsemble().collectSpikes(collectingSpikes);
	}

}
