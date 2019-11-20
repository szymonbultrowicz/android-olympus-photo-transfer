#!/usr/bin/python

import os
import re

q = os.environ.get("QUERY_STRING", "No Query String in url")
file = re.compile('DIR=(.*)').search(q).group(1)
jpg_file = file[1:].replace("ORF", "JPG")

print("Content-Type: image/jpeg")
print("Content-Length: " + str(os.stat(jpg_file).st_size))
print("")

with open(jpg_file, 'rb') as f:
    print(f.read())
