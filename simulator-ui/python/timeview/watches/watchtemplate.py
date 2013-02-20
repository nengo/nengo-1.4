class WatchTemplate:
    """Template class for watches used in view.py.  To create a new Watch,
    override this class and place the new watch file in the timeview.watches folder. Note:
    in order for the new watch to be automatically detected and added to the viewer,
    it must be in its own file, and the name of the class must match the name of the
    file.  For example, the new watch class MyNewWatch should be in .../timeview/watches/mynewwatch.py
    (the name detection is case insensititve)."""
    
    def check(self, obj):
        """Should return True if the given object is an instance
        of the class associated with this watch."""
        return False
    
    def views(self, obj):
        """Should return a list of 3-tuples, where each tuple 
        consists of a string label, a display component (from timeview.components)
        and a dict giving a list of arguments for that component."""
        return [(None, None, None)]