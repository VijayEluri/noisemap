#!/bin/bash

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
##################################
# Usage:
# Run the command `sh ~/.lamake/job.sh` from the directory of the main latex file

#set -x # logging bash commands for debugging
if [ -f main.tex ]; then
	if [ "`grep "\enable{greek}" $(pwd)/*.tex | grep -v '^%'`" ]; then	# echo pwd to check if pwd is correct
		cp ~/.lamake/* . &&
		xelatex -interaction=nonstopmode main.tex &&											# stderr to main.log
		rm main.aux main.log main.out
		gvfs-trash `ls ~/.lamake`																					# to prevent rm if mistakenly ran from ~/.lamake
	else
		cp ~/.lamake/* . &&
		make &&																														# stderr to main.log
		make clean &&
		gvfs-trash `ls ~/.lamake`																					# to prevent rm if mistakenly ran from ~/.lamake
	fi
else
	echo "Error: Set the name of the main Latex file to main.tex, or configure ~/.lamake/Makefile.ini" > main.log
	return 1
fi
#set +x
