Icons are normally selected from the Gnome Tango set

They are viewable at
http://commons.wikimedia.org/wiki/GNOME_Desktop_icons

You can get a 32x32.png from that website.  For example, by going to 
http://upload.wikimedia.org/wikipedia/commons/thumb/8/89/Gnome-edit-cut.svg/32px-Gnome-edit-cut.svg.png you get the Gnome-edit-cut icon at 32x32.

(change the "32px" to "48px" or other numbers to get different sizes)

To get the slightly shaded version (used on mouseover), use ImageMagik as follows:

convert clear.png -fill black -colorize 20%% clear-pressed.png
