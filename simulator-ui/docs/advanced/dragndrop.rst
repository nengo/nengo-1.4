Creating a Drag And Drop Template
==================================

Nengo comes with a variety of templates: pre-built components that can be used to
build your models.  These are the various icons on the left side of the screen that
can be dragged in to your model.

These components are defined in ``python/nef/templates``.  There is one file for each
item, and the following example uses ``thalamus.py``.

The file starts with basic information, including the full name (title) of the component, the text to be used in the interface (label),
and an image to use as an icon.  The image should be stored in ``/images/nengoIcons``::

    title='Thalamus'
    label='Thalamus'
    icon='thalamus.png'

Next, we define the parameters that should be set for the component.  These can be strings (str), integers (int), 
real numbers (float), or checkboxes (bool).  For each one, we must indicate the name of the parameter, the label
text, the type, and the help text::
    
    params=[
        ('name','Name',str,'Name of thalamus'),
        ('neurons','Neurons per dimension',int,'Number of neurons to use for each possible action'),
        ('D','Dimensions',int,'Number of actions the thalamus can represent'),
        ('useQuick', 'Quick mode', bool,'If true, the same distribution of neurons will be used for each action'),
        ]

Next, we need a function that will test if the parameters are valid.  This function will be given the parameters as
a dictionary and should return a string containing the error message if there is an error, or not return anything
if there is no error::

    def test_params(net,p):
        try:
           net.network.getNode(p['name'])
           return 'That name is already taken'
        except:
            pass

Finally, we define the function that actually makes the component.  This function will be passed in a 
:py:class:`nef.Network` object that corresponds to the network we have dragged the template into, along with
all of the parameters specified in the ``params`` list above.  This script can now do any scripting calculations
desired to build the model::

    def make(net,name='Network Array', neurons=50, D=2, useQuick=True):
        thal = net.make_array(name, neurons, D, max_rate=(100,300), intercept=(-1, 0), radius=1, encoders=[[1]], quick=useQuick)    
        def addOne(x):
            return [x[0]+1]            
        net.connect(thal, None, func=addOne, origin_name='xBiased', create_projection=False)
        
The last step to make the template appear in the Nengo interface is to add it to the list in ``python/nef/templates/__init__.py``.
