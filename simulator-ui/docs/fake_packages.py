"""
@hunse: I designed this system to get around import ca.*, which fails in
  cPython since it depends on Java stuff

TODO: figure out how to fake `from pkg import *`. This might not be possible
  anyway, since files with this command will then try to use objects which
  haven't been added to the namespace. Best just to avoid this syntax.
"""

import sys

class FakePackages(object):
    def __init__(self, names):
        self.finder = _finder(names)
        sys.meta_path = sys.meta_path + [self.finder]

class _fake_object(object):
    def __new__(cls, *args, **kwargs):
        return object.__new__(cls)

    def __init__(self, *args, **kwargs):
        self.__all__ = [] # make sure `from pkg import *` gets nothing

    def __call__(self, *args, **kwargs):
        return self

    def __getattr__(self, name):
        return self

    def __getitem__(self, item):
        return self

class _finder(object):
    """A finder for fake packages (points Python to our loader)"""
    def __init__(self, names):
        if isinstance(names, str):
            self.names = [names]
        elif isinstance(names, list):
            self.names = names[:]
        else:
            raise ValueError("Names must be string or list of strings")

    def find_module(self, name, path):
        """Return our loader for the packages that we want to fake"""
        parts = name.split('.')
        if parts[0] in self.names:
            return _loader()
        else:
            return None

class _loader(object):
    """A loader for fake packages"""
    def load_module(self, name):
        """Load the fake package by returning a fake object.

        The idea of the fake object is that it can act as either a module,
        class, function, or anything else, and just let the code run.
        """
        return _fake_object()
