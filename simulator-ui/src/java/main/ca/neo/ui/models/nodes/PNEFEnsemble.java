package ca.neo.ui.models.nodes;

import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.StructuralException;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFEnsembleFactory;
import ca.neo.model.nef.impl.DecodedOrigin;
import ca.neo.model.nef.impl.NEFEnsembleFactoryImpl;
import ca.neo.plot.Plotter;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.ConfigParam;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.managers.UserTemplateConfigurer;
import ca.neo.ui.configurable.struct.PTInt;
import ca.neo.ui.configurable.struct.PTString;
import ca.neo.ui.models.nodes.connectors.PDecodedTermination;
import ca.neo.ui.models.nodes.connectors.PTermination;
import ca.neo.ui.models.tooltips.PropertyPart;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
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

	static final ConfigParamDescriptor pDim = new PTInt("Dimensions");

	static final ConfigParamDescriptor pName = new PTString("Name");

	static final ConfigParamDescriptor pNumOfNeurons = new PTInt(
			"Number of Neurons");

	static final ConfigParamDescriptor pStorageName = new PTString(
			"Storage Name");

	static final String typeName = "NEFEnsemble";

	static final ConfigParamDescriptor[] zProperties = { pName, pNumOfNeurons,
			pDim, pStorageName };

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
	 * 
	 * @return PTermination created, null if not
	 */
	public PTermination createDecodedTermintation() {
		PDecodedTermination termUI = new PDecodedTermination(this);

		try {
			UserTemplateConfigurer config = new UserTemplateConfigurer(termUI);
			config.configureAndWait();

			addWidget(termUI);
			return termUI;

		} catch (ConfigException e) {
			e.defaultHandledBehavior();
		}

		return null;
	}

	@Override
	public ConfigParamDescriptor[] getConfigSchema() {
		return zProperties;
	}

	@Override
	public NEFEnsemble getModel() {
		return (NEFEnsemble) super.getModel();
	}

	@Override
	public String getTypeName() {
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
	protected Node configureModel(ConfigParam prop) {
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
	protected PopupMenuBuilder constructMenu() {

		PopupMenuBuilder menu = super.constructMenu();

		// MenuBuilder nefMenu = menu.addSection("NEFEnsemble");

		MenuBuilder plotMenu = menu.createSubMenu("Plot");

		plotMenu.addAction(new StandardAction("NEFEnsemble") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void action() {
				Plotter.plot(getModel());
			}

		});

		Origin[] origins = getModel().getOrigins();

		for (Origin element : origins) {
			if (element instanceof DecodedOrigin) {
				plotMenu.addAction(new PlotDecodedOrigin("Decoded origin: "
						+ element.getName(), element.getName()));
			}
		}

		// Termination
		menu.addAction(new AddDecodedTerminationAction());
		return menu;
	}

	@Override
	protected TooltipBuilder constructTooltips() {
		TooltipBuilder tooltips = super.constructTooltips();
		tooltips.addPart(new PropertyPart("# Dimension", ""
				+ getModel().getDimension()));

		return tooltips;
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
				throw new UserCancelledException();
			else
				addedTermination = term;
		}

		@Override
		protected void undo() throws ActionException {
			addedTermination.destroy();

		}

	}

	class PlotDecodedOrigin extends StandardAction {
		private static final long serialVersionUID = 1L;
		String decodedOriginName;

		public PlotDecodedOrigin(String actionName, String decodedOriginName) {
			super("Plot decoded origin", actionName);
			this.decodedOriginName = decodedOriginName;
		}

		@Override
		protected void action() throws ActionException {
			Plotter.plot(getModel(), decodedOriginName);

		}

	}

}
