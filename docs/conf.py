#!/usr/bin/env python3

import os
import sys

import guzzle_sphinx_theme

sys.path.insert(0, os.path.abspath('../simulator-ui/python'))
sys.path.insert(0, os.path.abspath('../simulator-ui/python/nef'))

extensions = [
    "sphinx.ext.autodoc",
    "sphinx.ext.githubpages",
    "sphinx.ext.mathjax",
    "sphinx.ext.todo",
    "guzzle_sphinx_theme",
    "javasphinx",
]

source_suffix = ".rst"
master_doc = "index"
exclude_patterns = ["_build", "Thumbs.db", ".DS_Store"]
suppress_warnings = ['image.nonlocal_uri']
autodoc_mock_imports = ['ca', 'com', 'java', '_javax', 'numeric']

project = "Nengo 1.4"
copyright = ("2006-2017, Bryan Tripp & Centre for "
             "Theoretical Neuroscience, University of Waterloo")
author = ("Bryan Tripp & Centre for Theoretical Neuroscience, "
          "University of Waterloo")
version = "1.4.1"
release = "1.4"
language = None

todo_include_todos = True
autoclass_content = "both"
todo_include_todos = True
autodoc_member_order = "bysource"

# HTML theming
pygments_style = "sphinx"
templates_path = ['_templates']
html_static_path = ['_static']

html_theme_path = guzzle_sphinx_theme.html_theme_path()
html_theme = "guzzle_sphinx_theme"

html_theme_options = {
    "project_nav_name": "Nengo 1.4",
    "base_url": "https://www.nengo.ai/nengo_1.4",
}

# Other builders
htmlhelp_basename = "Nengo 1.4"

latex_elements = {
    # "papersize": "letterpaper",
    # "pointsize": "11pt",
    # "preamble": "",
    # "figure_align": "htbp",
}

latex_documents = [
    (master_doc,  # source start file
     "nengo.tex",  # target name
     "Nengo 1.4 Documentation",  # title
     author,  # author
     "manual"),  # documentclass
]

man_pages = [
    # (source start file, name, description, authors, manual section).
    (master_doc, "nengo_1.4", "Nengo 1.4 Documentation", [author], 1)
]

texinfo_documents = [
    (master_doc,  # source start file
     "Nengo 1.4",  # target name
     "Nengo 1.4 Documentation",  # title
     author,  # author
     "Nengo",  # dir menu entry
     "Large-scale brain modelling in Java",  # description
     "Miscellaneous"),  # category
]
