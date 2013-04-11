GPU Computing in Nengo
===================================

Since neurons are parallel processors, Nengo can take advantage of the parallelization offered by GPUs to speed up simulations. Currently, only NEF Ensembles and Network Arrays containing NEF Ensembles can benefit from GPU acceleration (and they must be composed solely of leaky integrate-and-fire (LIF) neurons). You can still use the GPU for Networks which contain other types of nodes, but only nodes that meet these criteria will actually be executed on the GPU(s), the rest will run on the CPU. This restriction is necessary because GPUs take advantage of a computing technique called Single-Instruction Multiple-Data, wherein we have many instances of the same code running on different data. If we are only using NEF Ensembles containing LIF neurons then we are well within this paradigm: we want to execute many instances of the code simulating the LIF neurons, but each neuron has different parameters and input. On the other hand, a SimpleNode that you have defined yourself in a Python script cannot run on the GPU because SimpleNodes can contain arbitrary code. 

The GPU can also be used to speed up the process of creating NEF Ensembles. The dominant component in creating an NEF Ensemble in terms of runtime is solving for the decoders, which requires performing a Singular Value Decomposition (SVD) on a matrix with dimensions (number of neurons) x (number of neurons). If the number of neurons in the ensemble is large, this can be an extremely computationally expensive operation. You can use CUDA to perform this SVD operation much faster. 

The Nengo GPU implementation requires the CUDA developer driver and runtime libraries for your operating system. You may also want to install the CUDA code samples which let you test whether your machine can access and communicate with the GPU and whether the GPU is performing up to standards. Installers for each of these can be downloaded from here: http://developer.nvidia.com/cuda-toolkit-40. As a first step, you should download a copy of each of these. 

The SVD computation requires a third party GPU linear algebra toolkit called CULA Dense, in addition to the CUDA toolkit. CULA Dense can be downloaded free of charge (though does require a quick registration) from here: http://culatools.com/downloads/dense/. Download version R13a as it is the latest release that is compatible with version 4.0 of the CUDA toolkit. CULA is NOT required if you only want to run simulations on the GPU. 

Note: Most of the following steps have to be performed for both ``NengoGPU``, the library for running Nengo simulations on the GPU, and ``NengoUtilsGPU``, the library for performing the SVD on the GPU. Here I will detail the process of installing NengoGPU, but the process of installing ``NengoUtilsGPU`` is almost identical. I will add comments about how to adjust the process for installing ``NengoUtilsGPU``, and will mark these with ``***``. The main difference is that ``NengoUtilsGPU`` relies on CULA whereas ``NengoGPU`` has no such dependency, and this manifests itself in several places throughout the process. 

Linux
------

Step 1: Install CUDA Developer Driver
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

1. Be sure you have downloaded the CUDA developer driver installer for your system from the link provided above. Note where the file gets downloaded.

2. In a shell, enter the command::

    sudo gdm service stop

  This stops the X server. The X server relies on the GPU driver, so the former can't be running while the latter is being changed/updated.

3. Hit ``Ctrl-Alt-F1``. This should take you to a login shell. Enter your credentials and then cd into the directory where the driver installer was downloaded. 

4. To start the installer, run the command::

    sudo sh <driver-installer-name>

5. Answer yes to the queries from the installer, especially the one that asks to change xorg.conf.

6. Once installation has finished, restart the X server with::

    sudo gdm service start

Step 2: Install CUDA Toolkit
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

1. Be sure you have downloaded the CUDA toolkit installer for your system from the link provided above. Note where the file gets downloaded.

2. Run the installer with::

    sudo sh <toolkit-installer-name>

3. The installer will ask where you want to install CUDA. The default location, ``/usr/local/cuda``, is the most convenient since parts of the NengoGPU implementation assume it will be there (however, we can easily change these assumptions by changing some text files, so just note where you install it).

4. At the end, the installer gives a message instructing you to set the values of certain environment variables. Be sure to do this. The best way to do it permanently is to set them in your ``~/.bashrc`` file. For example, to change the PATH variable to include the path to the ``bin`` directory of the cuda installation, add the following lines to the end of your ``~/.bashrc``::

    PATH=$PATH:<path-to-cuda-bin-dir>
    export PATH

  and then restart your bash shell. You can type ``printenv`` to see whether the changes have taken effect. 


Step 3: Install CUDA code samples (optional)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This step is not strictly necessary, but can help to ensure that your driver is installed properly and that CUDA code has access to the GPU, and can be useful in troubleshooting various other problems.

1. Be sure you have downloaded the CUDA code sample installer for your system from the link provided above. Note where the file gets downloaded.

2. Run the installer with::

    sudo sh <samples-installer-name>

  Your home directory is generally a good place to install it. The installer creates a folder called ``NVIDA_GPU_Computing_SDK`` in the location you chose. ``NVIDIA_GPU_COMPUTING_SDK/C/src`` contains a series of subdirectories, each of which contains CUDA source code which can be compiled into binary files and then executed.

3. cd into ``NVIDA_GPU_Computing_SDK/C`` and enter the command: ``make``. This will compile the many CUDA source code samples in the ``C/src`` directory, creating a series of executable binaries in ``C/bin/linux/release``. Sometimes ``make`` may fail to compile one of the programs, which will halt the entire compilation process. Thus, all programs which would have been compiled after the failed program will remain uncompiled. To get around this, you can either fix the compilation issues with the failed program, or you can compile each individual code sample on its own (there are a lot of them, so you probably won't want to compile ALL of them this way, just the ones that seem interesting). Just cd into any of the directories under ``C/src`` and type ``make`` there. If compilation succeeds, a binary executable file will be created in ``C/bin/linux/release``.

4. To run any sample program, cd into ``C/bin/linux/release`` and type ``./<name of program>``. If the program in question was compiled properly, you should see a bunch of output about what computations are being performed, as well as either a PASS or a FAIL. FAIL's are bad.

5. Some useful samples are:

   ``deviceQueryDrv`` - Simple test to make sure CUDA programs have access to the GPU. Also displays useful information about the CUDA-enabled GPUs on your system, if it can find and access them. 

   ``bandwidthTest`` - Tests bandwidth between CPU and GPU. This bandwidth can sometimes be a bottleneck of the NengoGPU implementation. Online you can usually find bandwidth benchmarks which say roughly what the bandwidth should be for a given card. If your bandwidth is much lower than the benchmark for your card, there may be a problem with your setup. 

   ``simpleMultiGPU`` - Useful if your system has multiple GPUs. Tests whether they can all be used together.

``***`` Step 4: Install CULA Dense (only required if installing NengoUtilsGPU)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
This step is very similar to ``Step 2: Install CUDA Toolkit``. 

1. Be sure you have downloaded the CULA toolkit installer for your system as mentioned in the introduction. Note where the file gets downloaded.

2. Run the installer with::

    sudo sh <CULA-installer-name>

3. The installer will ask where you want to install CULA. Again, the default location ``/usr/local/cula``, is the most convenient since parts of the GPU implementation assume it will be there (however, we can easily change these assumptions by changing some text files, so just note where you install it).

4. Be sure to set the environment variables as recommended by the installer. See ``Step 2: Install CUDA Toolkit`` for the best way to do this.

Step 5: Compiling the shared libraries
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

1. cd into the directory NengoGPU. For developers this is can be found in ``simulator/src/c/NengoGPU``. For users of the prepackaged version of Nengo, it should just be a subdirectory of the main Nengo folder.

2. Run ``./configure`` to create the necessary symbolic links (you may have to chmod this to ensure that the permissions are set to execute).

3. If you installed CUDA in a location other than the default, open the file ``Makefile`` with your favourite text editor and edit it so that the variables ``CUDA_INC_PATH`` and ``CUDA_LIB_PATH`` point to the correct locations. If you installed CUDA in the default location, you don't have to change anything. 

4. ``nvcc``, the CUDA compiler, is incompatible with versions of gcc that are too new, where ``too new`` is a function of the nvcc version. gcc-4.4 is generally a safe bet. Install it with ``sudo apt-get install gcc-4.4``. If you have gcc-4.4 installed in some directory other than the default (/usr/bin), then you have to edit the ``GCC_PATH`` variable in the NengoGPU Makefile to point there.

5. Type ``make`` to compile the code in the directory. If successful, this creates a shared library called ``libNengoGPU.so``. This is the native library that Nengo will call to perform the neural simulations on the GPU(s). 

6. ``***`` Redo steps 1-3 in the ``NengoUtilsGPU`` directory, which should be located in the same directory as the ``NengoGPU`` directory. In this case, there are two additional variables in the ``Makefile`` that you might have to edit which point to CULA libraries and include files: ``CULA_INC_PATH`` and ``CULA_LIB_PATH``. Again, you only have to edit these if you installed CULA in a location other than the default.

7. We have make sure that the CUDA libraries, which are referenced by ``libNengoGPU.so``, can be found at runtime. To acheive this, cd into ``/etc/ld.so.conf.d/``. Using your favourite text editor and ensuring you have root priveleges, create a text file called ``cuda.conf`` (``eg. sudo vim cuda.conf``). In this file type the lines::

    <absolute-path-to-CUDA-dir>/lib/
    <absolute-path-to-CUDA-dir>/lib64/

  So, for example, if you installed CUDA in the default location you should have::

    /usr/local/cuda/lib/
    /usr/local/cuda/lib64/
  
  ``***`` If you are installing NengoUtilsGPU as well, then you also have to add the lines::

    <absolute-path-to-CULA-dir>/lib/
    <absolute-path-to-CULA-dir>/lib64/

  Save the file and exit. Finally, run ``sudo ldconfig``. This populates the file ``/etc/ld.so.cache`` using the files in ``/etc/ld.so.conf.d/``. ``ld.so.cache`` tells the machine were to look for shared libraries at runtime (in addition to the default locations like ``/usr/lib`` and ``/usr/local/lib``). 

8. This step is only for developers running Nengo through an IDE like Eclipse. Those using a prepackaged version of Nengo can skip this step.
  The Java Virtual Machine has to be told where to look for native libraries. Edit the JVM arguments in your Run and Debug configurations so that they contains the following text::

    -Djava.library.path=<absolute-path-to-NengoGPU-dir>
  
  ``***`` If you are also installing NengoUtilsGPU, then you must also add:: 

    <absolute-path-to-NengoUtilsGPU-dir>

  using a colon (:) as the separator between paths. 

Step 6: Using NengoGPU and NengoUtilsGPU 
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. Commented this stuff out until we reimplement the UI elements.
.. 1. Now open up Nengo. Click on the |parallel| icon on the right side of the tool bar at the top of the Nengo UI. This will open up a menu which lets you configure the parallelization of Nengo.

.. 2. If the previous steps worked properly, then the field ``Number of GPUs for Simulation`` will be enabled and you will be able to choose the number of GPUs to use for Nengo simulations. This field will not let you choose more GPUs than Nengo can detect on your system. If the ``libNengoGPU`` library wasn't found (either because building it didn't succeed or Java doesn't know where to find it) or no CUDA enabled GPUs can be detected on your system, then this field will be grayed out and an error message will appear to the right of the field. In this case, revisit the relevant step above.

.. 3. After setting the ``Number of GPUs for Simulation`` field to a non-zero value, any simulations you run that contain NEF Ensembles should run those NEF Ensembles on the GPU! There should be no change at all in the way you simulate networks except, of course, that they will run faster. You can still set probes, collect spikes, etc, in the same way you did before.  

1. NengoGPU provides support for running certain NEF Ensembles on the CPU while the rest are simulated on the GPU(s). Right click on the NEF Ensembles that you want to stay on the CPU and select the ``configure`` option. Set the ``useGPU`` field to false, and the ensemble you are configuring will run on the CPU no matter what. You can also edit the same field on a Network object, and it will force all NEF Ensembles within the Network to run on the CPU. 

2. You can also set the number of GPUs to use for simulation in a python script. This is useful if you want to ensure that a given network, created by a script (and maybe even run in that script), always runs with the same number of devices. To achieve this, add the following line to your script::

    ca.nengo.util.impl.NEFGPUInterface.setRequestedNumDevices(x) 
      
  where x is the number of devices you want to use for the resulting network.

3. GPU simulations can be combined with CPU multithreading. In the parallelization dialog, it lets you select the number of CPU threads to use. All NEF Ensembles that are set to run on the GPU will run there, and the rest of the nodes in the Network will be parallelized via multithreading. This is especially useful for speeding up Simple Nodes that do a lot of computation. The optimal number of threads will vary greatly depending on the particular network you are running and the specs of your machine, and generally takes some experimentation to get right. However, using a number of threads equivalent to the number of cores on your machine is usually a good place to start.

4. ``***`` If you installed ``libNengoUtilsGPU`` and it succeeded, then the parallelization dialog will have the ``Use GPU for Ensemble Creation`` checkbox enabled. If you check the box and press OK, then all NEF Ensembles you create afterwards will use the GPU for the Singular Value Decomposition, and this process should be significantly faster, especially for larger ensembles. If the install failed, Nengo cannot detect a CUDA-enabled GPU, or you simply chose not to install NengoUtilsGPU, then the box will be disabled and an error message will appear to its right. Note that the SVD implementation cannot take advantage of multiple GPUs, which is why there is no option to select the number of GPUs for ensemble creation. To change whether a GPU is used for ensemble creation from a python script, use the line::

    ca.nengo.math.impl.WeightedCostApproximator.setUseGPU(x)

  where x is either TRUE or FALSE.

      
.. |parallel| image:: ../../python/images/parallelization.png
    :scale: 75 %


Windows
------
Coming soon.

