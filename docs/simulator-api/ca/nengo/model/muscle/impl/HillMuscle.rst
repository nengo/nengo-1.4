.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.config Configuration

.. java:import:: ca.nengo.dynamics DynamicalSystem

.. java:import:: ca.nengo.dynamics Integrator

.. java:import:: ca.nengo.dynamics.impl EulerIntegrator

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math RootFinder

.. java:import:: ca.nengo.math.impl AbstractFunction

.. java:import:: ca.nengo.math.impl ConstantFunction

.. java:import:: ca.nengo.math.impl NewtonRootFinder

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.plot Plotter

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

HillMuscle
==========

.. java:package:: ca.nengo.model.muscle.impl
   :noindex:

.. java:type:: public class HillMuscle extends SkeletalMuscleImpl

   A Hill-type muscle model. Use RootFinder with function f_CE - f_SE, parameter l_SE, range 0 to breaking length. This finds force given activation and inputs. Could alternatively find dl_CE and have state variables l_CE and activation? TODO: ref Keener & Sneyd TODO: review -- this has been made to compile after model change but it might not make sense TODO: implement getConfiguration()

   :author: Bryan Tripp

Constructors
------------
HillMuscle
^^^^^^^^^^

.. java:constructor:: public HillMuscle(String name, float tauEA, float maxIsometricForce, Function CEForceLength, Function CEForceVelocity, Function SEForceLength) throws StructuralException
   :outertype: HillMuscle

   :param name: Muscle name
   :param tauEA: see Hill model
   :param maxIsometricForce: see Hill model
   :param CEForceLength: see Hill model
   :param CEForceVelocity: see Hill model
   :param SEForceLength: see Hill model
   :throws StructuralException: if Dynamics creation fails

Methods
-------
getTorque
^^^^^^^^^

.. java:method:: public float getTorque()
   :outertype: HillMuscle

   :return: Torque

main
^^^^

.. java:method:: public static void main(String[] args)
   :outertype: HillMuscle

   :param args: commandline args

run
^^^

.. java:method:: public void run(float startTime, float endTime) throws SimulationException
   :outertype: HillMuscle

setExcitation
^^^^^^^^^^^^^

.. java:method:: public void setExcitation(float excitation)
   :outertype: HillMuscle

   :param excitation: Excitation

setInputs
^^^^^^^^^

.. java:method:: public void setInputs(float angle, float velocity)
   :outertype: HillMuscle

   :param angle: Muscle angle
   :param velocity: Muscle velocity

