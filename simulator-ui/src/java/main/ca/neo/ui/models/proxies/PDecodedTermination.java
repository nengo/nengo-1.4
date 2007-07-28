package ca.neo.ui.models.proxies;

import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.nef.impl.DecodedTermination;
import ca.neo.ui.models.PModel;
import ca.neo.ui.models.PModelConfigurable;
import ca.neo.ui.models.icons.IconWrapper;
import ca.neo.ui.views.objects.configurable.PTBoolean;
import ca.neo.ui.views.objects.configurable.PTFloat;
import ca.neo.ui.views.objects.configurable.PTString;
import ca.neo.ui.views.objects.configurable.PropertySchema;
import ca.shu.ui.lib.objects.lines.LineIn;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

public class PDecodedTermination extends PTermination {

	private static final long serialVersionUID = 1L;

	static PropertySchema pIsModulatory = new PTBoolean("Is Modulatory");

	static PropertySchema pName = new PTString("Name");

	static PropertySchema pTauPSC = new PTFloat("tau");

	static PropertySchema[] zProperties = { pName, pTauPSC, pIsModulatory };

	PNEFEnsemble ensembleProxy;

	public PDecodedTermination(PNEFEnsemble ensembleProxy) {
		super();
		this.ensembleProxy = ensembleProxy;

		startConfigManager(getDefaultConfigManager());
		setIcon(new TermIcon(this));
		this.setDraggable(false);
	}

	@Override
	public String getName() {
		if (getModelTermination() != null) {
			return getModelTermination().getName();
		} else
			return "";
	}

	public DecodedTermination getModelTermination() {
		return (DecodedTermination) getModel();
	}

	@Override
	public PropertySchema[] getPropertiesSchema() {
		// TODO Auto-generated method stub
		return zProperties;

	}

	protected Object createModel() {
		Termination term = null;

		try {
			term = ensembleProxy
					.getNEFEnsemble()
					.addDecodedTermination(
							(String) getProperty(pName),
							new float[][] { new float[] { (Float) getProperty(pTauPSC) } },
							(Float) getProperty(pTauPSC),
							(Boolean) getProperty(pIsModulatory));

		} catch (StructuralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return term;
	}

	static final String typeName = "Decoded Termination";

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return typeName;
	}

}

class TermIcon extends IconWrapper {

	public TermIcon(PModel parent) {
		super(parent, new LineIn());
		configureLabel(false);

		// TODO Auto-generated constructor stub
	}

}
