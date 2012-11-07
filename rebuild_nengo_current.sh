#!/bin/bash

set -e

( cd simulator && ant clean && ant )
( cd simulator-ui && ant clean && ant dist )

CURRENT="$(ls -t | grep nengo- | grep -v .zip | head -n 1)"
echo 'If you see "BUILD FAILED" above and you are worried... relax!' "It's normal."
echo "Moving right along..."
rm -f nengo-current
ln -s "$CURRENT" nengo-current
NENGO_GIT_ROOT="$PWD"

( cd nengo-current && chmod +x nengo-cl external/ps* )
( cd nengo-current && mv python python_dontuse && ln -s "$NENGO_GIT_ROOT/simulator-ui/python" )

echo "Nengo has been compiled to ./$CURRENT and symlinked as ./nengo-current"
