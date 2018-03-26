Running experiments
===================

Once a model has been designed,
we often want to run controllable experiments
to gather statistics about the performance of the model.
As an example,
we may want to read the input data from a file
and save the corresponding outputs to a separate file.
This allows us to automate the process of running these simulations,
rather than using the interactive plots viewer.

Inputs
------

Input can be provided for the simulation
using the :class:`nef.SimpleNode` approach,
covered in more detail in :doc:`/scripting/simplenodes`.
Here, we create an origin
that has a fixed set of input stimuli,
and shows each one for 100 milliseconds each::

  inputs = [[1, 1], [1, -1], [-1, -1], [-1, 1]]
  dt = 0.001
  steps_per_input = 100

  class Input(nef.SimpleNode):
      def origin_input(self, dimensions=2):
          # find time step we are on
          step = int(round(self.t / dt))
          # find stimulus to show
          index = (step / steps_per_input) % len(inputs)
          return inputs[index]

This origin will cycle through
the values ``1, 1``, ``1, -1``, ``-1, -1``, and ``-1, 1``,
presenting each for 0.1 seconds.
Instead of manually specifying the inputs,
we could also have read these values from a file::

  inputs = []
  for line in open('inputs.csv').readlines():
      row = [float(x) for x in line.strip().split(',')]
      inputs.append(row)

Outputs
-------

When running experiments,
we often don't want the complete record
of every output the network makes over time.
Instead, we're interested in what it is doing at specific points.
For this case,
we want to find out what the model's output is
for each of the inputs.
In particular, we want its output value
just before we change the input to the next one in the list.
The following code will collect these values
and save them to a file called ``experiments.csv``::

  class Output(nef.SimpleNode):
      def termination_save(self, x, dimensions=1, pstc=0.01):
          step = int(round(self.t / dt))
          if step % steps_per_input == (steps_per_input - 1):
              f = file('experiment.csv', 'a+')
              f.write('%d,%1.3f\n' % (step / steps_per_input, x[0]))
              f.close()

Connecting the model
--------------------

For this particular example,
here is a model that simply computes the product of its inputs.
The inputs to the model are
connected to the ``Input`` node
and the outputs go to the ``Output`` node to be saved::

  net = nef.Network('Experiment example')
  input = net.add(Input('input'))  # create the input node
  output = net.add(Output('output'))  # create the output node
  net.make('A', 100, 2, radius=1.5)
  net.make('B', 50, 1)
  net.connect(input.getOrigin('input'), 'A')  # connect the input
  net.connect('B', output.getTermination('save'))  # connect the output

  def multiply(x):
      return x[0] * x[1]

  net.connect('A', 'B', func=multiply)

  net.add_to_nengo()


Running the simulation
----------------------

The model so far
should run successfully within Nengo
using the standard approach of opening the interactive plots
and clicking the run button.
However, we can also have the model automatically run
right from within the script.
This bypasses the visual display, making it run faster.
The following commands run the simulation for 2 seconds
(this is 2 simulated seconds, of course --
the actual time needed to run the simulation
is dependent on your computer's speed
and the complexity of the network)::

  net.run(2.0)

The parameter indicates how long
to run the simulation for,
and you can also optionally specify the time step
(defaults to ``dt=0.001``).

As an alternative method,
you can also run the simulation like this,
producing an equivalent result::

  t = 0
  while t <= 2.0:
      net.run(0.1)
      # insert any code you want to run every 0.1 seconds here
      t += dt

With either approach,
the simulation will be automatically run when you run the script.

With these approaches,
you can even run a script
without using the Nengo user interface at all.
Instead, you run the model from the command line.
Instead of running ``nengo`` (or ``nengo.bat`` on Windows),
you would do::

  nengo-cl experiment.py

This will run the ``experiment.py`` script.
