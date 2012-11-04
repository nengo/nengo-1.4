
import os
_mypath = os.path.abspath(os.path.split(__file__)[0])

import sys
sys.path.append(
    os.path.abspath(os.path.join(_mypath, '..', '..', 'python')))

from nengo_deco import nengo_log

