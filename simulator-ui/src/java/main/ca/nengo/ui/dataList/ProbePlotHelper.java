package ca.nengo.ui.dataList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.prefs.Preferences;

import ca.nengo.model.SimulationMode;
import ca.nengo.model.nef.NEFEnsemble;
import ca.nengo.ui.actions.PlotTimeSeries;
import ca.nengo.ui.lib.actions.StandardAction;
import ca.nengo.ui.NengoConfig;
import ca.nengo.util.Probe;
import ca.nengo.util.TimeSeries;

/**
 * Helps plot probes
 * 
 * @author Shu Wu
 */
public class ProbePlotHelper {

    private static ProbePlotHelper singleton;

    /**
     * @return TODO
     */
    public static ProbePlotHelper getInstance() {
        if (singleton == null) {
            singleton = new ProbePlotHelper();
        }
        return singleton;
    }

    private ProbePlotHelper() {
    }

    /**
     * @param probe
     * @param plotName
     * @return The default plotting action
     */
    public StandardAction getDefaultAction(Probe probe, String plotName) {

        return getDefaultAction(probe.getData(), plotName, isApplyTauFilterByDefault(probe));

    }

    /**
     * @param applyFilterByDefault
     * @param data
     * @param plotName
     * @return The default plotting action
     */
    public StandardAction getDefaultAction(TimeSeries data,
            String plotName,
            boolean applyFilterByDefault) {

        if (applyFilterByDefault) {
            return new PlotTimeSeries("Plot w/ filter", data, plotName, false,
                    getDefaultTauFilter(), getDefaultSubSampling());
        } else {
            return new PlotTimeSeries("Plot raw data", data, plotName, false, 0, 0);
        }

    }

    /**
     * @return TODO
     */
    public int getDefaultSubSampling() {
    	Preferences prefs = NengoConfig.getPrefs();
    	return prefs.getInt(NengoConfig.I_PLOTTER_SUBSAMPLING,
    			            NengoConfig.I_PLOTTER_SUBSAMPLING_DEF);
    }


    /**
     * @param value TODO
     */
    public void setDefaultSubSampling(int value) {
    	Preferences prefs = NengoConfig.getPrefs();
    	prefs.putInt(NengoConfig.I_PLOTTER_SUBSAMPLING, value);
    }

    /**
     * @return TODO
     */
    public float getDefaultTauFilter() {
    	Preferences prefs = NengoConfig.getPrefs();
    	return prefs.getFloat(NengoConfig.F_PLOTTER_TAU,
    			              NengoConfig.F_PLOTTER_TAU_DEF);
    }

    /**
     * @param value TODO
     */
    public void setDefaultTauFilter(float value) {
    	Preferences prefs = NengoConfig.getPrefs();
    	prefs.putFloat(NengoConfig.F_PLOTTER_TAU, value);
    }
    
    /**
     * @param data TODO
     * @param plotName TODO
     * @return TODO
     */
    public Collection<StandardAction> getPlotActions(TimeSeries data, String plotName) {
        List<StandardAction> actions = new ArrayList<StandardAction>(2);

        actions.add(new PlotTimeSeries("Plot raw data", data, plotName, false, 0, 0));
        actions.add(new PlotTimeSeries("Plot w/ options", data, plotName, true,
                getDefaultTauFilter(), getDefaultSubSampling()));
        return actions;
    }

    /**
     * @param probe
     *            Probe
     * @return Whether to apply tau filters in timeseries plots for that probe
     */
    public boolean isApplyTauFilterByDefault(Probe probe) {

        if (probe.getTarget() instanceof NEFEnsemble) {
            NEFEnsemble e = (NEFEnsemble) probe.getTarget();
            if ((e.getMode() == SimulationMode.DEFAULT )
            		|| (e.getMode() == SimulationMode.PRECISE) 
            		|| (e.getMode() == SimulationMode.APPROXIMATE)
            		|| (e.getMode() == SimulationMode.EXPRESS)) {
                return true;
            }
        }
        return false;
    }



}
