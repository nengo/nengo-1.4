package ca.neo.ui.models.proxies;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFEnsembleFactory;
import ca.neo.model.nef.impl.NEFEnsembleFactoryImpl;
import ca.neo.plot.Plotter;
import ca.neo.ui.models.PModelNode;
import ca.neo.ui.models.icons.EnsembleIcon;
import ca.neo.ui.style.Style;
import ca.neo.ui.views.objects.configurable.PTInt;
import ca.neo.ui.views.objects.configurable.PTString;
import ca.neo.ui.views.objects.configurable.PropertySchema;
import ca.neo.ui.views.objects.configurable.managers.IConfigurationManager;
import ca.shu.ui.lib.objects.GText;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;

/**
 * A UI object for NEFEnsemble
 * 
 * @author Shu Wu
 * 
 */
public class PNEFEnsemble extends PModelNode {
	private static final long serialVersionUID = 1L;

	static final PropertySchema pDim = new PTInt("Dimensions");

	static final PropertySchema pName = new PTString("Name");

	static final PropertySchema pNumOfNeurons = new PTInt("Number of Neurons");

	static final PropertySchema pStorageName = new PTString("Storage Name");

	static final String typeName = "NEFEnsemble";

	static final PropertySchema[] zProperties = { pName, pNumOfNeurons, pDim,
			pStorageName };

	boolean isCollectingSpikes = false;

	String name = "Unamed Ensemble";

	String storageName = "Unamed Ensemble";

	public PNEFEnsemble(String name, int numOfNeurons, int dimensions,
			String storageName) {
		setProperty(pName, name);
		setProperty(pNumOfNeurons, numOfNeurons);
		setProperty(pDim, dimensions);
		setProperty(pStorageName, storageName);
		setModel(createModel());
		init();
	}

	public PNEFEnsemble(boolean useDefaultConfigManager) {
		super(useDefaultConfigManager);
		init();
	}

	public PNEFEnsemble(IConfigurationManager configManager) {
		super(configManager);
		init();
	}

	private void init() {
		setIcon(new EnsembleIcon(this));
	}

	/*
	 * Adds a decoded termination to the UI and Ensemble Model
	 */
	public void addDecodedTermintation() {
		PDecodedTermination term;

		term = new PDecodedTermination(this);
		term.setScale(0.5);
		if (term.isModelCreated())
			addWidget(term);

		// term.initModel();

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

		String menuName;
		if (isCollectingSpikes) {
			menuName = "Stop Collecting Spikes";
		} else {
			menuName = "Collect Spikes";
		}

		menu.addAction(new AbstractAction(menuName) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (isCollectingSpikes) {
					getNEFEnsemble().collectSpikes(false);
					isCollectingSpikes = false;
				} else {
					getNEFEnsemble().collectSpikes(true);
					isCollectingSpikes = true;
				}
			}
		});

		// Termination

		menu.addAction(new AbstractAction("Add decoded termination") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				addDecodedTermintation();
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
	public PropertySchema[] getPropertiesSchema() {
		// TODO Auto-generated method stub
		return zProperties;
	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return typeName;
	}

	@Override
	protected Node createModel() {
		try {
			GText loadingText = new GText(
					"WARNING: Ensemble Model creation in progress");
			loadingText.setFont(Style.FONT_XXLARGE);
			addChild(loadingText);

			NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();

			String name = (String) getProperty(pName);

			Integer numOfNeurons = (Integer) getProperty(pNumOfNeurons);
			Integer dimensions = (Integer) getProperty(pDim);
			String storageName = (String) getProperty(pStorageName);

			setName(name);

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

}
