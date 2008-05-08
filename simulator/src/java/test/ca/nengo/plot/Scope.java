/*
 * 
 *  TODO: give 'scale to extrema' some headroom. 
 * */

package ca.nengo.plot;

import com.jeta.forms.components.panel.FormPanel;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Color;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import ca.nengo.util.*;
import ca.nengo.math.Function;
import ca.nengo.math.FunctionBasis;
import ca.nengo.math.impl.FunctionBasisImpl;
import ca.nengo.math.impl.GaussianPDF;
import ca.nengo.math.impl.PostfixFunction;
import ca.nengo.math.impl.SigmoidFunction;
import ca.nengo.model.Network;
import ca.nengo.model.SimulationException;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Units;
import ca.nengo.model.impl.FunctionInput;
import ca.nengo.model.impl.NetworkImpl;
import ca.nengo.plot.Plotter;
import ca.nengo.util.Probe;

public class Scope {
	private Probe _probe;
	private float[][] _probeValues;
	
	private java.util.Timer _workerTimer = new java.util.Timer();
	private JSlider _slider;
	
	/** non-null when playing (or fast-forwarding, or rewinding). */
	private volatile SimpleRecurController _curRecurController;
	private int _curTime = 0;
	private int _timeStep=100, _trailLength=20;
	private boolean _scalingToExtrema = false;
	/** [min, max] */
	private float[] _extrema;
	
	private JPanel _graphPanel, _ctrlPanel;
	
	public Scope(Probe probe_) {
		assert probe_!=null;
		_probe = probe_;
		_probeValues = _probe.getData().getValues();
		resetExtrema();
		try {
			SwingUtilities.invokeAndWait(new Runnable() { public void run() {
				_graphPanel = new JPanel();
				initCtrlPanel();
			}});
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		showChartPanelForCurTime(true);
		
		// hack for pack():  (TODO: fix)  
		try { Thread.sleep(1000); } catch(InterruptedException e) {}
	}
	
	private void resetExtrema() {
		_extrema = new float[]{Float.MAX_VALUE, Float.MIN_VALUE};
	}
	
	private void initCtrlPanel() {
		_ctrlPanel = new JPanel(new GridBagLayout());
		
		JButton playButton = new JButton(">");
		playButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				pause();
				_curRecurController = new SimpleRecurController();
				move(0, 1, false, _curRecurController, true);
			}});
		
		JButton pauseButton = new JButton("II");
		pauseButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				pause();
			}});
		
		JButton stepBackButton = new JButton("|<");
		stepBackButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				move(0, -1, false, null, true);
			}});
		JButton stepForwardButton = new JButton(">|");
		stepForwardButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				move(0, 1, false, null, true);
			}});
		
		JButton goToStartButton = new JButton("|<<");
		goToStartButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				move(0, 0, true, null, true);
			}});
		JButton goToEndButton = new JButton(">>|");
		goToEndButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				move(0, _probeValues.length-1, true, null, true);
			}});
		
		_slider = new JSlider(0, _probeValues.length-1, _curTime);
		_slider.addChangeListener(new ChangeListener() { 
				public void stateChanged(ChangeEvent e) {
					// rounding down to time step: 
					int newTime = (_slider.getValue()/_timeStep)*_timeStep;
					move(0, newTime, true, null, false);
				}
			});
		
		final JCheckBox scaleToExtremaCheckbox = new JCheckBox("Scale range to extrema (seen so far)");
		scaleToExtremaCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_scalingToExtrema = scaleToExtremaCheckbox.isSelected();
				resetExtrema();
				if(!isPlaying()) {
					showChartPanelForCurTime(true);
				}
			}});
		
		JButton fastForwardButton = new JButton(">>");
		fastForwardButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				pause();
				_curRecurController = new SimpleRecurController();
				move(0, 3, false, _curRecurController, true);
			}});
		JButton rewindButton = new JButton("<<");
		rewindButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				pause();
				_curRecurController = new SimpleRecurController();
				move(0, -3, false, _curRecurController, true);
			}});
		
		final JSpinner timeStepSpinner = new JSpinner(
				new SpinnerNumberModel(_timeStep, 1, Integer.MAX_VALUE, 1));
		timeStepSpinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					_timeStep = (Integer)(timeStepSpinner.getValue());
					if(!isPlaying()) {
						showChartPanelForCurTime(true);
					}
				}
			});
		
		final JSpinner trailLengthSpinner = new JSpinner(
				new SpinnerNumberModel(_trailLength, 0, Integer.MAX_VALUE, 1));
		trailLengthSpinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					_trailLength = (Integer)(trailLengthSpinner.getValue());
					if(!isPlaying()) {
						showChartPanelForCurTime(true);
					}
				}
			});
		
		GridBagConstraints c = new GridBagConstraints();
		JPanel vcrCtrls = new JPanel();
		vcrCtrls.add(goToStartButton);
		vcrCtrls.add(rewindButton);
		vcrCtrls.add(stepBackButton);
		vcrCtrls.add(playButton);
		vcrCtrls.add(pauseButton);
		vcrCtrls.add(stepForwardButton);
		vcrCtrls.add(fastForwardButton);
		vcrCtrls.add(goToEndButton);
		c.gridy = 0;
		_ctrlPanel.add(vcrCtrls);
		
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		_ctrlPanel.add(_slider, c);
		
		JPanel scaleCtrls = new JPanel();
		
		JPanel incCtrls = new JPanel();
		incCtrls.add(scaleToExtremaCheckbox);
		incCtrls.add(new JLabel("time step (milliseconds):"));
		incCtrls.add(timeStepSpinner);
		incCtrls.add(new JLabel("trail length (in time steps):"));
		incCtrls.add(trailLengthSpinner);
		c.gridy++;
		c.fill = GridBagConstraints.NONE;
		_ctrlPanel.add(incCtrls, c);
		
	}
	
	/** this is not probably not accurate.  
	 * but then it is not used for anything that requires it to be accurate.  */
	private boolean isPlaying() {
		return _curRecurController!=null;
	}
	
	private void pause() {
		if(_curRecurController!=null) {
			_curRecurController._recur = false;
			_curRecurController = null;
		}
	}
	
	private static interface RecurController {
		public boolean shouldRecur();
	}
	
	private static class SimpleRecurController implements RecurController {
		public volatile boolean _recur = true;
		
		public boolean shouldRecur() {
			return _recur;
		}
	}
	
	/** @param x_  if posIsAbsoluteAsOpposedToRelative_ is true then this is an absolute 
	 * location (index in _probeValues) 
	 * else this is relative, units = multiples of _timeStep, and represents a 'delta' 
	 * 	in a _probeValues index.  (also, can be negative). */ 
	private void move(final int delayTimeMillis_, 
			final int x_, final boolean posIsAbsoluteAsOpposedToRelative_,  
			final RecurController recurController_, final boolean adjustSliderToMatch_) {
		TimerTask t = new TimerTask() { public void run() {
			int wouldBeCurTime = (posIsAbsoluteAsOpposedToRelative_ 
					? x_ : _curTime + x_*_timeStep);
			wouldBeCurTime = reinIn(wouldBeCurTime, 0, _probeValues.length-1);
			if(wouldBeCurTime != _curTime) {
				if(recurController_!=null && recurController_.shouldRecur()) {
					move(100, x_, posIsAbsoluteAsOpposedToRelative_, recurController_, 
						adjustSliderToMatch_);
				}
				_curTime = wouldBeCurTime;
				showChartPanelForCurTime(false);
				if(adjustSliderToMatch_) {
			    	SwingUtilities.invokeLater(new Runnable() { public void run() {
			    			setValueNoFire(_slider, _curTime);
			    		}});
				}
			} else {
				_curRecurController = null;
			}
		}};
		_workerTimer.schedule(t, delayTimeMillis_);
	}
	
	private static void setValueNoFire(JSlider slider_, int value_) {
		ChangeListener[] listeners = slider_.getChangeListeners();
		for(int i=0; i<listeners.length; ++i) {
			slider_.removeChangeListener(listeners[i]);
		}
		slider_.setValue(value_);
		for(int i=0; i<listeners.length; ++i) {
			slider_.addChangeListener(listeners[i]);
		}
	}
	
	private void showChartPanelForCurTime(boolean invokeLater_) {
		showChartPanel(getChartPanelForCurTime(), invokeLater_);
	}
	
	private void showChartPanel(final JPanel panel_, boolean invokeLater_) {
		Runnable r = new Runnable() { public void run() {
			_graphPanel.removeAll();
			_graphPanel.add(panel_);
			_graphPanel.validate();
    	}};
    	if(invokeLater_) {
    		SwingUtilities.invokeLater(r);
    	} else {
    		try {
    			SwingUtilities.invokeAndWait(r);
    		} catch(Exception e) {
    			throw new RuntimeException(e);
    		}
    	}
	}
	
	private ChartPanel getChartPanelForCurTime() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        int numDimensions = _probeValues[0].length;
		for(int dim=0; dim<numDimensions; ++dim) {
			XYSeries series = new XYSeries("dimension "+dim);
	        for(int trailPosTime=_curTime; trailPosTime>=_curTime-_trailLength*_timeStep; 
	        		trailPosTime-=_timeStep) {
	        	if(trailPosTime>=0) {
					series.add(trailPosTime/1000.0, _probeValues[trailPosTime][dim]);
					if(_scalingToExtrema) {
						updateExtrema(_probeValues[trailPosTime][dim]);
					}
	        	} else {
					series.add(trailPosTime/1000.0, null);
	        	}
	        }
			dataset.addSeries(series);
        }
		JFreeChart chart = ChartFactory.createScatterPlot("Function", "Input", "Output",
				dataset, PlotOrientation.VERTICAL, false, false, false);
		if(_scalingToExtrema) {
			chart.getXYPlot().getRangeAxis().setRange(_extrema[0], _extrema[1]);
		}
		animPlotVectorSetupRenderer(chart.getXYPlot(), _trailLength);
		return new ChartPanel(chart);
	}
	
	private void updateExtrema(float v_) {
		_extrema[0] = Math.min(_extrema[0], v_);
		_extrema[1] = Math.max(_extrema[1], v_);
	}
	
	private static void animPlotVectorSetupRenderer(XYPlot plot, final int trailLength) {
		final XYLineAndShapeRenderer origRenderer = (XYLineAndShapeRenderer)(plot.getRenderer());
		
		for(int i=0; i<=trailLength; ++i) {
			origRenderer.setSeriesShape(i, new Ellipse2D.Float(-3, -3, 6, 6));
		}
	
		InvocationHandler handler = new InvocationHandler() {
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					if(method.getName().equals("drawItem")) {
						int series = (Integer)(args[8]), item = (Integer)(args[9]);
						Color color = getColorForSeriesIdx(series);
						color = fadeToWhite(color, (trailLength-item)/(float)trailLength);
						origRenderer.setSeriesPaint(series, color, false);
					}
					return method.invoke(origRenderer, args);
				}
			};
		XYItemRenderer wrappingRenderer = (XYItemRenderer) Proxy.newProxyInstance(
				ScopeExample.class.getClassLoader(),
				new Class[] {XYItemRenderer.class},
				handler);

		plot.setRenderer(wrappingRenderer);
	}
	
	private static Color getColorForSeriesIdx(int seriesIdx) {
		Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, 
				Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.YELLOW, 
				Color.PINK, Color.BLACK, Color.GRAY};
		return colors[seriesIdx % colors.length];
	}
	
	private static Color fadeToWhite(Color c, float percent) {
		return new Color(fadeColorCompToWhite(c.getRed(), percent), 
			fadeColorCompToWhite(c.getGreen(), percent),
			fadeColorCompToWhite(c.getBlue(), percent));
	}
	
	private static int fadeColorCompToWhite(int comp, float percent) {
		return reinIn((int)(comp + (255-comp)*percent), 0, 255);
	}
	
	private static int reinIn(int x_, int lowerBound_, int upperBound_) {
		if(x_ < lowerBound_) {
			return lowerBound_;
		} else if(x_ > upperBound_) {
			return upperBound_;
		} else {
			return x_;
		}
	}
	
	public JPanel getGraphPanel() { return _graphPanel; }
	public JPanel getCtrlPanel() { return _ctrlPanel; }
	
}

