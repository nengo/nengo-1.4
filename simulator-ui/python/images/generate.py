import os



def make(fn1,fn2,size=32,vflip=False):
    if vflip:
        os.system("convert -background '#ffffff00' %s.svg -flop -resize %dx%d %s.png"%(fn1,size,size,fn2))
        os.system("convert -background '#ffffff00' %s.svg -flop -resize %dx%d -fill black -colorize 20%% %s-pressed.png"%(fn1,size,size,fn2))
    else:
        os.system("convert -background '#ffffff00' %s.svg -resize %dx%d %s.png"%(fn1,size,size,fn2))
        os.system("convert -background '#ffffff00' %s.svg -resize %dx%d -fill black -colorize 20%% %s-pressed.png"%(fn1,size,size,fn2))
def make_bw(fn1,fn2):
    os.system("convert -background '#ffffff00' %s.svg -resize 32x32 -colorspace Gray %s.png"%(fn1,fn2))
    os.system("convert -background '#ffffff00' %s.svg -resize 32x32 -colorspace Gray -fill black -colorize 20%% %s-pressed.png"%(fn1,fn2))

def make_arrows(fn1,fn2):
    os.system("convert -background '#ffffff00' -density 300x300 %s.svg -resize 200x200 -crop 100x50+50+0 -trim -resize 30%% %sup.png"%(fn1,fn2))
    os.system("convert %sup.png -rotate 180 %sdown.png"%(fn2,fn2))
    os.system("convert -background '#ffffff00' -density 300x300 %s.svg -resize 200x200 -crop 100x50+50+0 -trim -resize 30%% -fill black -colorize 20%% %sup-pressed.png"%(fn1,fn2))
    os.system("convert %sup-pressed.png -rotate 180 %sdown-pressed.png"%(fn2,fn2))

def make_arrows2():
    size=60
    os.system("convert play.png -rotate -90 -resize %s%% -trim arrowup.png"%size)
    os.system("convert play-pressed.png -rotate -90 -resize %s%% -trim arrowup-pressed.png"%size)
    os.system("convert play.png -rotate 90 -resize %s%% -trim arrowdown.png"%size)
    os.system("convert play-pressed.png -rotate 90 -resize %s%% -trim arrowdown-pressed.png"%size)
    
    
    


make('Gnome-media-playback-pause','pause')
make('Gnome-media-playback-start','play')
make('Gnome-media-seek-backward','backward')
make('Gnome-media-seek-forward','forward')
make('Gnome-media-skip-forward','end')
make('Gnome-media-skip-backward','start')
make('Gnome-preferences-system','configure')
#make('go-home','restore',size=24)
make('go-jump','restore',size=24,vflip=True)
make('media-floppy','save',size=24)
make_bw('Gnome-edit-undo','restart')
make_arrows('Gnome-view-fullscreen','arrow')
make_arrows2()

make('Gnome-view-refresh','refresh',size=24)
make('Gnome-x-office-spreadsheet','data',size=24)




#for name in ['pause','start']:
#    os.system("convert -size 48x48 -background '#ffffff00' Gnome-media-playback-%s.svg %s.png"%(name,name))
#    os.system("convert -size 48x48 -background '#ffffff00' Gnome-media-playback-%s.svg  -fill black -colorize 20%% %s-pressed.png"%(name,name))
    
    
