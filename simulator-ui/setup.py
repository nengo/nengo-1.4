from distutils.core import setup
import setuptools;
setup(name='NengoUI',
      version='1.4',
      author='Chris Eliasmith',
      author_email='celiasmith@uwaterloo.ca',
      url='http://nengo.ca/',
      packages = ['ccm', 'ccm.legacy', 'nef', 'nps', 'plasticity', 'spa', 'stats', 'stats.plot', 'stats.view', 'timeview'],
      package_dir = {'': 'python'},
      )

install_requires = ['github', 'xyaiz']

setuptools.command.easy_install.main(['github'])
