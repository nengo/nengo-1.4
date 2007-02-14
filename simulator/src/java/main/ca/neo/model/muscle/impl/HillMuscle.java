/*
 * Created on 25-Nov-2006
 */
package ca.neo.model.muscle.impl;

import ca.neo.dynamics.DynamicalSystem;
import ca.neo.dynamics.Integrator;
import ca.neo.dynamics.impl.EulerIntegrator;
import ca.neo.math.Function;
import ca.neo.math.RootFinder;
import ca.neo.math.impl.AbstractFunction;
import ca.neo.math.impl.ConstantFunction;
import ca.neo.math.impl.NewtonRootFinder;
import ca.neo.model.SimulationException;
import ca.neo.model.Units;
import ca.neo.model.muscle.TorqueMuscle;
import ca.neo.plot.Plotter;
import ca.neo.util.TimeSeries;
import ca.neo.util.impl.TimeSeries1DImpl;
import ca.neo.util.impl.TimeSeriesImpl;

/**
 * A Hill-type muscle model.
 *
 * Use RootFinder with function f_CE - f_SE, parameter l_SE, range 0 to breaking length. 
 * This finds force given activation and inputs. Could alternatively find dl_CE and have
 * state variables l_CE and activation? 
 * 
 * TODO: ref Keener & Sneyd
 * TODO: test
 * 
 * @author Bryan Tripp
 */
public class HillMuscle implements TorqueMuscle {

	private Function myCEForceLength;
	private Function myCEForceVelocity;
	private Function mySEForceLength;
	
	private float myLengthCE;
	
	public HillMuscle() {
	}
	
	public void setInputs(float angle, float velocity) {
		// TODO Auto-generated method stub
		
	}

	public float getTorque() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void run(float startTime, float endTime) throws SimulationException {
		// TODO Auto-generated method stub
		
	}

	public void setExcitation(float excitation) {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) {
		
		final float rl = 0.2f; //resting length
		
		float tauEA = .05f;
		Function CEForceLength = new ConstantFunction(1, 1f);
		
		final float vmax = -2f;
		final float af = 1f; //shaping parameter between 0.1 and 1
		Function CEForceVelocity = new AbstractFunction(1) {
			private static final long serialVersionUID = 1L;
			public float map(float[] from) {
				return Math.min(1.3f, (1 - from[0]/vmax) / (1 + from[0]/(vmax*af)));
			}
		};
		
		Function SEForceLength = new AbstractFunction(1) {
			private static final long serialVersionUID = 1L;
			public float map(float[] from) {
				return 200f * ( (float) Math.exp(200f*from[0]) - 1f );
			}
		};
		
		//Plotter.plot(CEForceLength, 0f, rl*.01f, rl*2f, "CE Force-Length");
		//Plotter.plot(CEForceVelocity, vmax, .1f, -vmax, "CE Force-Velocity");
		//Plotter.plot(SEForceLength, 0f, rl*.001f, rl*.1f, "SE Force-Length");
		
		Dynamics d = new Dynamics(tauEA, 5000f, CEForceLength, CEForceVelocity, SEForceLength, false);
		d.setState(new float[]{.001f, rl});
		Integrator i = new EulerIntegrator(.0001f);
		
//		TimeSeries input = new TimeSeriesImpl(new float[]{0f, 1f}, 
//				new float[][]{new float[]{1, rl, 0f}, new float[]{1, rl, 0f}}, 
//				new Units[]{Units.UNK, Units.M, Units.M_PER_S});
		TimeSeries input = new TimeSeriesImpl(new float[]{0f, .001f, .5f}, 
				new float[][]{new float[]{1, rl, 0f}, new float[]{0, rl, 0f}, new float[]{0, rl, 0f}},
				new Units[]{Units.UNK, Units.M, Units.M_PER_S});
		
		long startTime = System.currentTimeMillis();
		TimeSeries output = i.integrate(d, input);
		System.out.println("Elapsed time: " + (System.currentTimeMillis() - startTime));
		
		Plotter.plot(output, "Force");
	}
	
	public static class Dynamics implements DynamicalSystem {
		
		private static final long serialVersionUID = 1L;
		
		private float myTauEA;
		private float myMaxIsometricForce;
		private Function myCEForceLength;
		private Function myCEForceVelocity;
		private Function mySEForceLength;
		
		private RootFinder myRootFinder;
		
		private Units[] myUnits; 		
		private float[] myState;

		/**
		 * @param maxIsometricForce Isometric force produced by CE at maximal activation and optimal length
		 * @param CEForceLength
		 * @param CEForceVelocity
		 * @param SEForceLength
		 * @param torque true indicates a torque muscle (input in rads, output in Nm); false indicates 
		 * 		a linear muscle (input in m, output in N)
		 */
		public Dynamics(float tauEA, float maxIsometricForce, Function CEForceLength, Function CEForceVelocity, Function SEForceLength, boolean torque) {
			myTauEA = tauEA;
			myMaxIsometricForce = maxIsometricForce;
			myCEForceLength = CEForceLength;
			myCEForceVelocity = CEForceVelocity;
			mySEForceLength = SEForceLength;
			myUnits = new Units[]{torque ? Units.Nm : Units.N};
			
			myRootFinder = new NewtonRootFinder(20, true);
		}
		
		/**
		 * @param t Simulation time (s)
		 * @param u Input: [excitation (0-1), muscle-tendon length, muscle-tendon lengthening velocity]
		 *  
		 * @see ca.neo.dynamics.DynamicalSystem#f(float, float[])
		 */
		public float[] f(float t, float[] u) {
			float a = myState[0]; //activation
			
			//first-order excitation-activation dynamics ... 
			float dadt = (u[0] - a) / myTauEA;
			
			//CE-SE dynamics
			float lenCE = myState[1];
			float lenSE = u[1] - lenCE;
			
			float force = mySEForceLength.map(new float[]{lenSE});
			float lm = myCEForceLength.map(new float[]{lenCE}); //length multiplier
			final float vm = Math.min(1.3f, force / (myMaxIsometricForce * a * lm)); //TODO: fix this
						
			
			System.out.println("force: " + force + " lm: " + lm + " vm: " + vm + " a: " + a + " dadt: " + dadt);
			
			//find velocity corresponding to this multiplier
			final Function fv = myCEForceVelocity;
			Function f = new AbstractFunction(1) {
				public float map(float[] from) {
					float result = fv.map(from) - vm; 
					//System.out.println("from: " + from[0] + " result: " + result);
					return result;
				}
			};
			float dlCEdt = myRootFinder.findRoot(f, -2f, 2f, 0.001f); //velocity of CE
			
			return new float[]{dadt, dlCEdt};
		}

		/**
		 * @param t Simulation time (s)
		 * @param u Input: [excitation (0-1), muscle-tendon length, muscle-tendon lengthening velocity]
		 *  
		 * @see ca.neo.dynamics.DynamicalSystem#g(float, float[])
		 */
		public float[] g(float t, float[] u) {
			float lenSE = u[1] - myState[1];
			return new float[]{mySEForceLength.map(new float[]{lenSE})};
		}

		/**
		 * @return [activation, CE length]
		 * @see ca.neo.dynamics.DynamicalSystem#getState()
		 */
		public float[] getState() {
			return myState;
		}

		/**
		 * @param state [activation, CE length]
		 * @see ca.neo.dynamics.DynamicalSystem#setState(float[])
		 */
		public void setState(float[] state) {
			assert state.length == 2;			
			myState = state;
		}

		/**
		 * @return 3 (activation, muscle-tendon length, muscle-tendon velocity)
		 * @see ca.neo.dynamics.DynamicalSystem#getInputDimension()
		 */
		public int getInputDimension() {
			return 3;
		}

		/**
		 * @return 1 (force) 
		 * @see ca.neo.dynamics.DynamicalSystem#getOutputDimension()
		 */
		public int getOutputDimension() {
			return 1;
		}

		/**
		 * @see ca.neo.dynamics.DynamicalSystem#getOutputUnits(int)
		 */
		public Units getOutputUnits(int outputDimension) {
			return myUnits[outputDimension];
		}
		
		/**
		 * @see ca.neo.dynamics.DynamicalSystem#clone()
		 */
		public Object clone() throws CloneNotSupportedException {
			boolean torque = myUnits[0].equals(Units.Nm);
			return new Dynamics(myTauEA, myMaxIsometricForce, myCEForceLength, myCEForceVelocity, mySEForceLength, torque);
		}
		
	}

}
