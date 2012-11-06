#!/bin/bash

# -- currently ant dist in simulator-ui actually does fail, but not critically
#set -e

( cd simulator && ant )
( cd simulator-ui && ant  && ant dist)  # -- known failure

# -- abort on failure from now on
set -e

CURRENT="$(ls -t | grep nengo- | grep -v .zip | head -n 1)"
rm -f nengo-current
ln -s "$CURRENT" nengo-current
NENGO_GIT_ROOT="$PWD"

( cd nengo-current && chmod +x nengo-cl external/ps* )
( cd nengo-current && mv python python_dontuse && ln -s "$NENGO_GIT_ROOT/simulator-ui/python" )

