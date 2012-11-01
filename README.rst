==========================
The Nengo neural simulator
==========================

Nengo is a software package for simulating large-scale neural systems.

Homepage: http://nengo.ca/


Eclipse setup
=============

Basic setup
-----------

The project is straightforward to set-up with Eclipse.

#) Add the projects to Eclipse using "File->New->Java project...", and creating the project from the source directory. Add "simulator" first, and then "simulator-ui".
#) To run the program, add a new run configuration ("Run->Run configurations"). The configuration should be a "Java Application", the project "simulator-ui", and the main class "ca.nengo.ui.NengoGraphics".

Increasing Java heap size
-------------------------

If you receive errors about running out of memory while running Nengo, you can increase the heap size that Eclipse allocates for the program. Simply add the command "-Xmx4g" to the "Arguments->VM arguments" box in the run configurations dialog. (The "4g" specifies 4 gigabytes of memory. You can replace this with any integer and either "m" for megabytes or "g" for gigabytes, e.g., "512m" or "2g". The default is "384m".)
