#!/bin/bash

# -- currently ant dist in simulator-ui actually does fail, but not critically
#set -e

( cd simulator && ant )
( cd simulator-ui && ant  && ant dist)  # -- known failure

CURRENT=$(ls -t | grep nengo- | head -n 1)
rm -f nengo-current
ln -s $CURRENT nengo-current

( cd nengo-current  \
    && chmod +x nengo-cl external/ps* \
    && mv python python_dontuse \
    && ln -s ~/src/nengo/simulator-ui/python )

