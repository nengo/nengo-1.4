package ca.neo.ui.models.nodes.connectors;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ca.neo.model.Termination;
import ca.neo.ui.exceptions.ModelConfigurationException;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.icons.ModelIcon;
import ca.neo.ui.models.tooltips.PropertyPart;
import ca.neo.ui.models.tooltips.TitlePart;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.neo.ui.views.objects.configurable.managers.PropertySet;
import ca.neo.ui.views.objects.configurable.struct.PropDescriptor;
import ca.neo.util.Configuration;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.lines.ILineAcceptor;
import ca.shu.ui.lib.objects.lines.LineEnd;
import ca.shu.ui.lib.objects.lines.LineInIcon;
import ca.shu.ui.lib.util.PopupMenuBuilder;

/**
 * Termination UI Object
 * 
 * @author Shu Wu
 * 
 */
public class PTermination extends PWidget implements ILineAcceptor {

	private static final long serialVersionUID = 1L;
	LineEnd lineEnd;

	PNeoNode nodeParent;

	public PTermination(PNeoNode nodeParent) {
		super(nodeParent);
		init();
	}

	public PTermination(PNeoNode nodeParent, Termination term) {
		super(nodeParent, term);
		setName(term.getName());
		init();

	}

	@Override
	public PropDescriptor[] getConfigSchema() {
		return null;
	}

	public LineEnd getLineEnd() {
		return lineEnd;
	}

	public Termination getModelTermination() {
		return (Termination) getModel();
	}

	@Override
	public String getTypeName() {
		return "Termination";
	}

	@Override
	protected TooltipBuilder constructTooltips() {
		TooltipBuilder tooltips = super.constructTooltips();

		tooltips.addPart(new PropertyPart("Dimensions", ""
				+ getModel().getDimensions()));
		
		tooltips.addPart(new TitlePart("Configuration"));
		Configuration config = getModel().getConfiguration();
		String[] configProperties = config.listPropertyNames();
		for (int i = 0; i < configProperties.length; i++) {
			tooltips.addPart(new PropertyPart(configProperties[i], config
					.getProperty(configProperties[i]).toString()));
		}
		return tooltips;
	}

	public boolean setLineEnd(LineEnd lineEnd) {
		this.lineEnd = lineEnd;
		if (lineEnd != null) {
			addChild(lineEnd);
			this.lineEnd = lineEnd;

		}
		return true;
	}

	private void init() {

		/*
		 * Set up the Icon
		 */

		ModelIcon icon = new ModelIcon(this, new LineInIcon());
		icon.configureLabel(false);

		setIcon(icon);

	}

	@Override
	protected Object configureModel(PropertySet configuredProperties)
			throws ModelConfigurationException {
		throw new NotImplementedException();
	}

	@Override
	protected PopupMenuBuilder constructMenu() {
		// TODO Auto-generated method stub
		PopupMenuBuilder menu = super.constructMenu();

		if (lineEnd != null) {
			menu.addAction(new RemoveConnectionAction("Remove connection"));
		}
		return menu;
	}

	class RemoveConnectionAction extends StandardAction {
		private static final long serialVersionUID = 1L;

		public RemoveConnectionAction(String actionName) {
			super("Remove connection from Termination", actionName);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void action() throws ActionException {
			lineEnd.destroy();
			lineEnd = null;
		}
	}

	@Override
	public Termination getModel() {
		return (Termination) super.getModel();
	}
}
