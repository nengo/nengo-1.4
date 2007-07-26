package ca.neo.ui.models.proxies;

import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.ui.models.PModelConfigurable;
import ca.neo.ui.models.icons.Icon;
import ca.neo.ui.views.objects.properties.PTBoolean;
import ca.neo.ui.views.objects.properties.PTFloat;
import ca.neo.ui.views.objects.properties.PTString;
import ca.neo.ui.views.objects.properties.PropertySchema;
import ca.shu.ui.lib.objects.lines.LineIn;
import ca.shu.ui.lib.world.impl.WorldObject;

public class PDecodedTermination extends PModelConfigurable {

	private static final long serialVersionUID = 1L;

	static String pIsModulatory = "Is Modulatory";

	static String pName = "Name";

	static String pStorageName = "Storage Name";

	static String pTauPSC = "tau";

	static PropertySchema[] zMetaProperties = { new PTString(pName),
			new PTFloat(pTauPSC), new PTBoolean(pIsModulatory) };

	PNEFENsemble ensembleProxy;

	public PDecodedTermination(PNEFENsemble ensembleProxy) {
		super();
		this.ensembleProxy = ensembleProxy;
		// createModel();
	}

	@Override
	public String getName() {
		return "Decoded termination";
	}

	@Override
	public PropertySchema[] getPropertiesSchema() {
		// TODO Auto-generated method stub
		return zMetaProperties;

	}

	@Override
	protected WorldObject createIcon() {
		return new TermIcon();
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

		setIcon(new Icon(new TermIcon(), (String) getProperty(pName)));
		setModel(term);
		return term;
	}

}

class TermIcon extends LineIn {

}
