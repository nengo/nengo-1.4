package ca.neo.ui.models.proxies;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFEnsembleFactory;
import ca.neo.model.nef.impl.NEFEnsembleFactoryImpl;
import ca.neo.plot.Plotter;
import ca.neo.ui.models.PModelNode;
import ca.neo.ui.models.icons.EnsembleIcon;
import ca.neo.ui.style.Style;
import ca.neo.ui.views.objects.properties.PTInt;
import ca.neo.ui.views.objects.properties.PTString;
import ca.neo.ui.views.objects.properties.PropertySchema;
import ca.shu.ui.lib.objects.GText;
import ca.shu.ui.lib.objects.widgets.TrackedTask;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.world.impl.Frame;
import ca.shu.ui.lib.world.impl.WorldObject;

public class PNEFENsemble extends PModelNode {
	private static final long serialVersionUID = 1L;

	static String pDim = "Dimensions";

	static String pName = "Name";

	static String pNumOfNeurons = "Number of Neurons";

	static String pStorageName = "Storage Name";

	static PropertySchema[] zMetaProperties = { new PTString(pName),
			new PTInt(pNumOfNeurons), new PTInt(pDim),
			new PTString(pStorageName)

	};

	boolean isCollectingSpikes = false;

	String name = "Unamed Ensemble";

	String storageName = "Unamed Ensemble";

	public PNEFENsemble() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * Adds a decoded termination to the UI and Ensemble Model
	 */
	public void addDecodedTermintation() {
		PDecodedTermination term;

		term = new PDecodedTermination(this);
		if (term.isModelCreated())
			addChild(term);
		// term.initModel();

	}

	@Override
	public PopupMenuBuilder constructPopup() {
		// TODO Auto-generated method stub
		PopupMenuBuilder menu = super.constructPopup();

		menu.addAction(new AbstractAction("Plot") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Plotter.plot((NEFEnsemble) getModel());
			}
		});

		menu.addAction(new AbstractAction("Plot X") {
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
		menu.addSection("Connections");

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
	protected WorldObject createIcon() {
		return new EnsembleIcon();
	}

	@Override
	public PropertySchema[] getPropertiesSchema() {
		// TODO Auto-generated method stub
		return zMetaProperties;
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

			TrackedTask task = new TrackedTask("Creating ensemble: " + name);

			NEFEnsemble ensemble = ef.make(name, numOfNeurons, dimensions,
					storageName, false);

			task.finished();

			loadingText.removeFromParent();
			return ensemble;
		} catch (StructuralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
