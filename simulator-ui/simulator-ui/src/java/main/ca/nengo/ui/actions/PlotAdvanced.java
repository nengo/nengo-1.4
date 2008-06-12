/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "PlotAdvanced.java". Description: 
"Action for Plotting with additional options
  
  @author Shu Wu"

The Initial Developer of the Original Code is Bryan Tripp & Centre for Theoretical Neuroscience, University of Waterloo. Copyright (C) 2006-2008. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of the GNU 
Public License license (the GPL License), in which case the provisions of GPL 
License are applicable  instead of those above. If you wish to allow use of your 
version of this file only under the terms of the GPL License and not to allow 
others to use your version of this file under the MPL, indicate your decision 
by deleting the provisions above and replace  them with the notice and other 
provisions required by the GPL License.  If you do not delete the provisions above,
a recipient may use your version of this file under either the MPL or the GPL License.
*/

package ca.nengo.ui.actions;

import ca.nengo.plot.Plotter;
import ca.nengo.ui.configurable.ConfigException;
import ca.nengo.ui.configurable.ConfigResult;
import ca.nengo.ui.configurable.Property;
import ca.nengo.ui.configurable.descriptors.PFloat;
import ca.nengo.ui.configurable.descriptors.PInt;
import ca.nengo.ui.configurable.managers.UserConfigurer;
import ca.nengo.ui.configurable.managers.ConfigManager.ConfigMode;
import ca.nengo.util.DataUtils;
import ca.nengo.util.TimeSeries;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.UserMessages;

/**
 * Action for Plotting with additional options
 * 
 * @author Shu Wu
 */
public class PlotAdvanced extends StandardAction {

	private static final long serialVersionUID = 1L;
	private TimeSeries timeSeries;
	private String plotName;

	public PlotAdvanced(TimeSeries timeSeries, String plotName) {
		super("Plot with options", "Plot w/ options");
		this.timeSeries = timeSeries;
		this.plotName = plotName;
	}

	@Override
	protected void action() throws ActionException {
		try {
			// float tauFilter = new Float(JOptionPane
			// .showInputDialog("Time constant of display filter (s): "));

			PFloat pTauFilter = new PFloat(
					"Time constant of display filter [0 = off] ");
			PInt pSubSampling = new PInt("Subsampling [0 = off]");

			ConfigResult result;
			try {
				result = UserConfigurer.configure(new Property[] {
						pTauFilter, pSubSampling }, "Plot Options",
						UIEnvironment.getInstance(),
						ConfigMode.TEMPLATE_NOT_CHOOSABLE);

				float tauFilter = (Float) result.getValue(pTauFilter);
				int subSampling = (Integer) result.getValue(pSubSampling);

				TimeSeries timeSeriesToShow;

				if (subSampling != 0) {
					timeSeriesToShow = DataUtils.subsample(timeSeries,
							subSampling);
				} else {
					timeSeriesToShow = timeSeries;
				}

				if (tauFilter != 0) {
					Plotter.plot(timeSeriesToShow, tauFilter, plotName);
				} else {
					Plotter.plot(timeSeriesToShow, plotName);
				}
			} catch (ConfigException e) {
				e.defaultHandleBehavior();
			}

		} catch (java.lang.NumberFormatException exception) {
			UserMessages.showWarning("Could not parse number");
		}

	}
}