#! /usr/bin/env python

"Replace CRLF with LF in argument files.  Print names of changed files."

import sys, re, os

def processPath(level, path):
  total = 0
  print " " * 2 * level, "--Process directory:", path
  for file in os.listdir(path):
    file = os.path.normpath(os.path.join(path, file))
    if os.path.isdir(file):
      if file != "jextlauncher":
        total += processPath(level + 1, file)
      continue
    data = open(file, "rb").read()
    if '\0' in data:
      print " " * 2 * level, "  Skip binary file:", file
      continue
    newdata = re.sub("\r\n", "\n", data)
    if newdata != data:
      print " " * 2 * level, "  Processing file:", file
      total += 1
      f = open(file, "wb")
      f.write(newdata)
      f.close()
  return total

files = 0
if len(sys.argv) == 1:
  files = processPath(0, ".")
else:
  files = processPath(0, sys.argv[1])
print "\nProcessed a total of", files, "file(s)"
