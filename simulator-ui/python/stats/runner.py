import os
import random
import time

def parse_code(lines):
    code=''
    defaults={}
    params=[]
    for i,line in enumerate(lines):
        line=line.strip()
        if len(line)==0: continue
        elif line.startswith('#'): continue
        elif '=' in line:
            k,v=line.split('=',1)
            params.append(k)
            defaults[k]=eval(v)
        else:
            code=''.join(lines[i:])
            break
    return params,defaults,code

def ensure_backup(fn,lines):
    name=fn[:-3]+'/code.py'
    if not os.path.exists(fn[:-3]+'/'): os.makedirs(fn[:-3])
    if not os.path.exists(name):
        f=file(name,'w')
        f.write(''.join(lines))
        f.close()
    else:
        f=file(name)
        if lines!=f.readlines():
            f.close()
            t=os.stat(name).st_mtime
            text=time.strftime('%Y%m%d-%H%M%S',time.localtime(t))
            name2='%s/code-%s.py'%(fn[:-3],text)
            
            os.rename(name,name2)
            f=file(name,'w')
            f.write(''.join(lines))
            f.close()

def make_param_code(params,defaults,settings):
    p=[]
    for pp in params:
        v=defaults[pp]
        if pp in settings: v=settings[pp]
        p.append('%s=%s'%(pp,`v`))
    return '\n'.join(p)

def make_param_text(params,defaults,settings):
    p=[]
    for pp in params:
        if pp in settings and settings[pp]!=defaults[pp]:
            v=settings[pp]
            p.append('%s=%s'%(pp,`v`))
    if len(p)==0: return 'default'        
    return ','.join(p)



def make_settings_combinations(settings,keys=None):
    if keys is None: keys=settings.keys()
    if len(keys)==0: 
        yield {}
        return
    
    k=keys.pop()
    v=settings[k]
    for setting in make_settings_combinations(settings,keys):
        if type(v) is list:
            for vv in v:
                setting[k]=vv
                yield setting
        else:
            setting[k]=v
            yield setting


run_external=None
def run_with(script):
    global run_external
    run_external=script
    
try:
    import nef 
except:
    run_external=os.path.join('.','nengo-cl') 
    
def run_once(_filename,**setting):
    if not _filename.endswith('.py'): _filename+='.py'
    if not os.path.exists(_filename):
        raise 'Could not find file: %s'%_filename
    
    lines=file(_filename).readlines()
    params,defaults,core_code=parse_code(lines)    
    core_code=core_code.replace('\r\n','\n')
    ensure_backup(_filename,lines)
        
    fname='.nef.temp.%08x.py'%random.randrange(0,0x70000000)
    
    param_code=make_param_code(params,defaults,setting)
    param_text=make_param_text(params,defaults,setting)
    
    logfile=time.strftime('%Y%m%d-%H%M%S')+'-%08x'%int(random.randrange(0x7FFFFFFF))
    
    logline='import nef\nef.log.LogOverride.override(directory="%s/%s",filename="%s")'%(_filename[:-3],param_text.replace('"',r'\"'),logfile)

    code='%s\n%s\n%s'%(param_code,logline,core_code)
            
    f=file(fname,'w')
    f.write(code)
    f.flush()
    
    if run_external is None:
        compiled=compile(code,fname,'exec')
        exec compiled in {}
        import nef
        nef.log.LogOverride.override(directory=None,filename=None)
    else:
        os.system('%s %s'%(run_external,fname))    
    f.close()
    os.remove(fname)
    
          

def run(_filename=None,_iterations=1,_call_after=None,**settings):
    if not _filename.endswith('.py'): _filename+='.py'
    if not os.path.exists(_filename):
        raise 'Could not find file: %s'%_filename
    
    lines=file(_filename).readlines()
    params,defaults,core_code=parse_code(lines)    
    core_code=core_code.replace('\r\n','\n')
    ensure_backup(_filename,lines)
        
    fname='.nef.temp.%08x.py'%random.randrange(0,0x70000000)
    
    print settings

    for i in xrange(_iterations):
      for setting in make_settings_combinations(settings):
        param_code=make_param_code(params,defaults,setting)
        param_text=make_param_text(params,defaults,setting)
        
        logfile=time.strftime('%Y%m%d-%H%M%S')+'-%08x'%int(random.randrange(0x7FFFFFFF))
        
        logline='import nef\nef.log.LogOverride.override(directory="%s/%s",filename="%s")'%(_filename[:-3],param_text.replace('"',r'\"'),logfile)

        code='%s\n%s\n%s'%(param_code,logline,core_code)
                
        print _filename,'%d/%d'%(i,_iterations),param_text
        
        f=file(fname,'w')
        f.write(code)
        f.flush()
        
        if run_external is None:
            compiled=compile(code,fname,'exec')
            exec compiled in {}
            import nef
            nef.log.LogOverride.override(directory=None,filename=None)
        else:
            os.system('%s %s'%(run_external,fname))    
        f.close()
        if _call_after is not None: _call_after()
    os.remove(fname)
        
    
