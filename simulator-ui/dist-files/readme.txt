Thank you for using the Nengo neural simulator! For full details on Nengo and
the Neural Engineering Framework, please go to
 http://nengo.ca/
 http://compneuro.uwaterloo.ca/cnrglab/

                       ==============
                       =INSTALLATION=
                       ==============

Nengo requires no installation; simply unzip the associated archive, which
you have likely already done if you're reading this readme.

On an operating system with file-level access permissions (e.g. Linux) you
should also ensure that you have read and execute permissions on the file
 external/pseudoInverse

This can be done by executing the following command from a terminal in the
main nengo directory.
 chmod +x external/pseudoInverse

You may also need to mark nengo-ui and nengo-cl as executable.
 chmod +x nengo nengo-cl

                       =======
                       =USAGE=
                       =======

The Nengo neural simulator is written in Java with Python bindings through the
Jython library. To run Nengo, a Java virtual machine (JVM) version 1.5 or above
must be installed. The most recent version of Java can be found at
 http://java.com/en/download/index.jsp

There are two ways to use Nengo: through its graphical interface, or from the
command-line.

                       =====================
                       =GRAPHICAL INTERFACE=
                       =====================

Nengo has a graphical user interface built using Java's Swing API and the
Piccolo graphical framework.

Beginning users and those who aren't sure what to use should use the graphical
interface. To start it:

(Windows)
run nengo.bat
(Linux, Mac OS X)
run nengo

                       ==============
                       =COMMAND-LINE=
                       ==============

Advanced users, or those running Nengo in restricted environments, can use a
command-line environment through which to run simulations.

1. Running interactively

To get an interactive shell, where models can be run and later examined:
(Windows) Run nengo-cl.bat
(Linux, Mac OS X) Run nengo-cl

In this environment, you can run pre-written scripts using the command
 execfile('directory/script.py')

If you are not running in a headless environment, and you're using the
nef.Network class, you can see interactive plost with the command
 net.view()

2. Running a single script

To generate simulation results, it is often useful to run a single script many
times in a non-interactive environment. This can be done by passing the path
to the script as an argument to nengo-cl or nengo-cl.bat. For example,

(Windows) nengo-cl.bat my_scripts/simulation.py
(Linux, Mac OS X) nengo-cl my_scripts/simulation.py

                       ==============
                       =GETTING HELP=
                       ==============

If you have encounter any bugs when running Nengo, please report them on the
bug tracker at
 http://sourceforge.net/tracker/?group_id=216267&atid=1036998

For general inquiries and support, please contact one of the project
administrators through sourceforge at
 http://sourceforge.net/project/memberlist.php?group_id=216267

