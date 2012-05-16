import re, os

def run_in_nengo(code):
    m=re.search('nef\.Network[(](.+)[),]',code)
    if m is None:
        fn='experiment-'+time.strftime('%Y%m%d-%H%M%S')+'.py'
    else:
        fn='experiment-%s.py'%(m.group(1).replace(' ','_').replace('"','').replace("'",''))
    f=open(fn,'w')    
    f.write(code)
    f.close()
    os.system(os.path.join('.','nengo-cl')+' '+fn)
    print 'finished running',fn

