Saving Time with the Quick Option
==================================

Whenever Nengo creates an ensemble, it needs to compute a decoder.  This is done via the NEF
method of doing a least-squares minimization computation.  For large ensembles (~500 neurons or more), 
this can take some time, since it needs to invert an NxN matrix.  

To speed up this process, the scripting system can optionally save the decoders it has computed and
re-use them if it ever needs to create an ensemble with the exact same parameters.  That is, if
the particular parameters used in the :func:`nef.Network.make()` call are seen again, instead of
randomly creating a new ensemble, Nengo will make an exact copy of the old ensemble.

To turn on this feature, set ``quick=True`` when calling :func:`nef.Network.make()`::

  A=net.make('A',500,40,quick=True)
  
This can save considerable time in terms of loading a network (the time required to run a
simulation is unaffected), since it only needs to solve for the decoders once.  Each time the
script is run after this, it will just re-use those same decoders.

However, setting does mean that if you have two ensembles with the same parameters in the
same model, those ensembles will
have the exact same tuning curves and the exact same representational accuracy.  If this is a
problem, you can optionally specify a *storage_code* for the ensembles.  Two ensembles with
different *storage_code* values will not end up being identical, but there will still be
time savings when re-running the script.

  A=net.make('A',500,40,quick=True,storage_code='A')
  B=net.make('B',500,40,quick=True,storage_code='B')
  
If you create an array of ensembles using :func:`nef.Network.make_array()`, these ensembles
will all have the same parameters, leading them to all have the same tuning curves if ``quick=True``.
To avoid this, you can use the special storage code marker of ``'%d'``, which will be replaced by 
the index number of the ensemble in the array.  This allows you to make use of the quick option
and have all ensembles have separate tuning curves, if needed::

  A=net.make_array('A',500,40,quick=True,storage_code='A%d')
  B=net.make_array('B',500,40,quick=True,storage_code='B%d')
  
You can also indicate the default value for quick when you create a :class:`nef.Network`::

  net=nef.Network('My Network',quick=True)  
  
The saved files can be found in the ``quick`` directory as separate files for each saved parameter
setting.  If you delete these, Nengo will automatically regenerate new ensembles as needed.
  

