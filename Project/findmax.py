# -*- coding: utf-8 -*-
"""
Created on Sun Dec 13 18:29:49 2015

@author: Pragya Sardana
"""

def sortandArrange (infilename, outfilename):
    infile = open (infilename, "r")
    outfile = open (outfilename, "w")

    maxstring = ""
    maxvalue = 0
    
    for line in infile:
        twowords = line.split()
        if long(twowords[1]) > maxvalue:
            maxvalue = twowords[1]
            maxstring = twowords[0]
    
    outfile.write (str(maxvalue) + " " + maxtring + "\n")

        
        
    infile.close()
    outfile.close()
    
    

    
    

    
    