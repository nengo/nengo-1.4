==========================
The Nengo neural simulator
==========================

Nengo is a software package for simulating large-scale neural systems.

Homepage: http://nengo.ca/


Eclipse setup
=============

Brief setup instructions
------------------------

The important details of the Eclipse setup are as follows. See the next section for detailed setup instructions.

#) Add the projects to Eclipse using "File->New->Java project...", and creating the project from the source directory. Add "simulator" first, and then "simulator-ui".
#) To run the program, add a new run configuration ("Run->Run configurations"). The configuration should be a "Java Application", the project "simulator-ui", and the main class "ca.nengo.ui.NengoLauncher".


Detailed setup instructions
---------------------------

The following instructions should get you up and running the GUI from within
Eclipse. These instructions are valid for Eclipse version 3.7.2 (and hopefully
other versions too!)

#) If you are in linux or OSX, open a terminal and check out the nengo source
into the current working directory.

    git clone https://github.com/jaberg/nengo.git

#) Now start the eclipse IDE in the nengo root directory:

    cd nengo
    eclipse

#) If this is the first time you are running Eclipse, then it will ask you to
"Select a workspace". Eclipse stores *projects* in a workspace, and uses the
workspace to store temporary files. Choose a workspace path with "nengo" in it
somewhere to remind you what it is for, but don't make it a subdirectory of
the source you just checked out using git. A good choice would be
`~/.eclipse_workspaces/nengo`

#) Eclipse will finish loading up at this point and load a welcome screen,
which you can feel free to close.

#) At this point you're looking at an empty project view: there are menus and
a toolbar across the top of the window, and four panes in the main part of the
window. One of the panes (for me it's the top left) is labeled "Project
Explorer". We are going to add two projects to it: "simulator" and
"simulator-ui".

#) Add the "simulator" project by right clicking within the "Project Explorer"
window and selecting "import".  Expand the "General" tree and choose "Existing
Projects into Workspace". Where it gives the option to "Select root directory"
use the search button to browse to the "simulator" subdirectory of the nengo
project you checked out using git. Alternately, you can just type that path
into the text box. Press finish.

#) Add the "simulator-ui" project by repeating these steps but select the
"simulator-ui" subdirectory this time, when it asks you to "Select root directory".

#) Back in the "Project Explorer" pane, right-click your new "simulator-ui"
project and select "Run as -> Java Application" from the drop-down menu.  You'll be presented with
a huge list of options, but type "Nengo" into the search bar at the top,
and that should narrow the selection down to just one: NengoLauncher. Click
it, and you should be looking at the Nengo GUI running from within Eclipse.
You're done!


Increasing Java heap size
-------------------------

If you receive errors about running out of memory while running Nengo, you can increase the heap size that Eclipse allocates for the program. Simply add the command "-Xmx4g" to the "Arguments->VM arguments" box in the run configurations dialog. (The "4g" specifies 4 gigabytes of memory. You can replace this with any integer and either "m" for megabytes or "g" for gigabytes, e.g., "512m" or "2g". The default is "384m".)
