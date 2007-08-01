package ca.neo.ui.models.wrappers;

import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.model.nef.impl.DecodedTermination;
import ca.neo.ui.models.PModel;
import ca.neo.ui.models.icons.IconWrapper;
import ca.neo.ui.views.objects.configurable.struct.PTBoolean;
import ca.neo.ui.views.objects.configurable.struct.PTFloat;
import ca.neo.ui.views.objects.configurable.struct.PTString;
import ca.neo.ui.views.objects.configurable.struct.PropertyStructure;
import ca.shu.ui.lib.objects.lines.LineIn;

public class PDecodedTermination extends PTermination {

	private static final long serialVersionUID = 1L;

	static final PropertyStructure pIsModulatory = new PTBoolean("Is Modulatory");

	static final PropertyStructure pName = new PTString("Name");

	static final PropertyStructure pTauPSC = new PTFloat("tau");

	static final PropertyStructure[] zProperties = { pName, pTauPSC, pIsModulatory };

	PNEFEnsemble ensembleProxy;

	public PDecodedTermination(PNEFEnsemble ensembleProxy, String name,
			float tauPSC, boolean isModulatory) {
		super(ensembleProxy);
		setProperty(pName, name);
		setProperty(pTauPSC, tauPSC);
		setProperty(pIsModulatory, isModulatory);

		init(ensembleProxy);
		initModel();

	}

	public PDecodedTermination(PNEFEnsemble ensembleProxy) {
		super(ensembleProxy);

		init(ensembleProxy);
		startConfigManager(getDefaultConfigManager());
	}

	private void init(PNEFEnsemble ensembleProxy) {
		this.ensembleProxy = ensembleProxy;
		setIcon(new TermIcon(this));
		this.setDraggable(false);
	}


	@Override
	public PropertyStructure[] getPropertiesSchema() {
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

			setName(term.getName());
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
