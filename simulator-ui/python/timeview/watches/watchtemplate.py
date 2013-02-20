class WatchTemplate:
    """Template class for watches used in view.py"""
    
    def check(self, obj):
        """Should return True if the given object is an instance
        of the class associated with this watch."""
        return False
    
    def views(self, obj):
        """Should return a list of 3-tuples, where each tuple 
        consists of a string label, a display component (from timeview.components)
        and a dict giving a list of arguments for that component."""
        return [(None, None, None)]