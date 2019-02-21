# you need to download PIL: 
	#for python 3: python3 -m pip install Pillow
	#for python 2: sudo pip install Pillow
#file reader: reads file name and returns 2d array
from PIL import Image
import numpy as np

def readImage(filename):
	im = Image.open(filename)
	pix = im.load()
	width, height = im.size
	data = fixData(list(im.getdata()))
	#get pixel (x,y) by pixel_values[width*y+x]
	try:
		return np.array(data).reshape((width,height))
	except:
		raise Exception("Mo")

def fixData(data):
	#if data contains tuples, then replace every tuple with the first number in the tuple
	check = type(data[0])
	if check == tuple:
		for i in range(len(data)):
			data[i] = data[i][0]
	return data

def readTextFile(filename):
	width = 0
	height = 0
	im = np.array([])
	file = open(filename, "r")
	for line in file:
		imLine = line.strip().split(",")		
		width = len(imLine)
		#print(width)
		for i in range(len(imLine)):
			imLine[i] = int(imLine[i])
		im = np.append(im, np.array(imLine))
		height += 1
	#print(height)
	try:
		im.reshape((width,height))
	except:
		raise Exception('\n\nSome error with the shape of the .txt image file \n\n')
	return im.reshape((width,height))

	
	