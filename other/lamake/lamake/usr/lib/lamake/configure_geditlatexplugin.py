#!/usr/bin/python
# -*- coding: utf-8 -*-
# This file is part of lamake
#
# Copyright (C) 2010 Nikos Maris
#
# This program is free software; you can redistribute it and/or modify it under
# the terms of the GNU General Public Licence as published by the Free Software
# Foundation; either version 2 of the Licence, or (at your option) any later
# version.
#
# This program is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
# FOR A PARTICULAR PURPOSE.  See the GNU General Public Licence for more 
# details.
#
# You should have received a copy of the GNU General Public Licence along with
# this program; if not, write to the Free Software Foundation, Inc., 51 Franklin
# Street, Fifth Floor, Boston, MA  02110-1301, USA
####################################################
# Copies files to home folder and configures gedit-latex-plugin to use these files

import os,sys,re, shutil
from os.path import join
home = os.environ.get("HOME")									# root needs double quotes

# configure gedit-latex-plugin to use the above files
confpath = join(home,".gnome2/gedit/plugins/GeditLaTeXPlugin/tools.xml")
lines = open(confpath,'r').readlines()				# Each <tool> has one <job> per line
if re.search(r'"Make PDF"',lines[1])==None:		# set latex-makefile <tool> at first position to generate Ctrl+Alt+1 accelerator
	lines.insert(1,'\t</tool>\n')								# list used as a stack
	lines.insert(1,'\t\t<job mustSucceed="True" postProcessor="GenericPostProcessor">sh '+path+'/job.sh</job>\n')
	lines.insert(1,'\t<tool description="Make PDF using the latex-makefile" extensions=".tex" id="1" label="Make PDF">\n')
	open(confpath,'w').writelines(lines)
