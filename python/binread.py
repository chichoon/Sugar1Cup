# -*- coding:cp949 -*-
import csv
import os
import json
import numpy as np
from matplotlib import pyplot as plt
import struct


#raw integer to rgb (0 : 255)
def getrgb888(r, g, b):
    i = 1
    if r >= g and r >= b:
        i = r / 255 + 1
    elif g >= r and g >= b:
        i = g / 255 + 1
    elif b >= g and b >= r:
        i = b / 255 + 1

    if i != 0:
        r_ = r / i
        g_ = g / i
        b_ = b / i
    else:
        r_ = r
        g_ = g
        b_ = b

    if r_ > 30:
        r_ = r_ - 30
    if g_ > 30:
        g_ = g_ - 30
    if b > 30:
        b_ = b_ - 30

    r_ = r_ * 255 / 225
    g_ = g_ * 255 / 225
    b_ = b_ * 255 / 225

    if r_ > 255:
        r_ = 255
    if g_ > 255:
        g_ = 255
    if b_ > 255:
        b_ = 255

    rgblist = [np.uint8(r_), np.uint8(g_), np.uint8(b_)]
    return rgblist


class NumpyEncoder(json.JSONEncoder):
    """ Special json encoder for numpy types """
    def default(self, obj):
        if isinstance(obj, np.integer):
            return int(obj)
        elif isinstance(obj, np.floating):
            return float(obj)
        elif isinstance(obj, np.ndarray):
            return obj.tolist()
        return json.JSONEncoder.default(self, obj)


#if binfile path is placed with python file :
#direction = input("input the foldername : ")
#curr_path = os.path.join(os.path.abspath(os.getcwd()), direction)
#if binfile path is not placed with python file :
curr_path = 'C:/Users/jiyoo/Documents/code/binfiles'
txt_path = os.path.join(os.path.abspath(os.getcwd()), 'tmp_filelist.txt')

with open(txt_path, 'wt') as f:
    for root, dirs, files in os.walk(curr_path):
        if files:
            for file in files:
                if file.find('.bin') > 0:
                    filename = os.path.join(root, file)
                    f.write(filename + '\n')

with open(txt_path, 'r') as file:
    lines = file.readlines()
    lines.sort()
    for line in lines:
        bfname = ''.join([os.path.splitext(line)[0], '.bin'])
        tfname = ''.join([os.path.splitext(line)[0], '.json'])
        csvname = ''.join([os.path.splitext(line)[0], '.csv'])
        pngname = ''.join([os.path.splitext(line)[0], '.png'])
        with open(bfname, 'rb') as BF: #read bin file for conversion
            #with open(tfname, 'w') as TF: #open json file
                CF = open(csvname, 'w', newline='')
                wr = csv.writer(CF) #open csv file
                wr.writerow(['index', 'AcX', 'AcY', 'AcZ', 'tmp', 'GyX', 'GyY', 'GyZ', 'C', 'R', 'G', 'B', 'PPM']) #csv
                #bin_data = {} #json array for dump
                j = 0
                img = []
                while True:
                    temp = BF.read(1)  # 0x02 (Start of File)
                    if not temp: break
                    #bin to actual integer
                    m_AcX = np.int16(0x0000 | int(struct.unpack('B', BF.read(1))[0]) |
                                     (int(struct.unpack('B', BF.read(1))[0]) << 8))
                    m_AcY = np.int16(0x0000 | int(struct.unpack('B', BF.read(1))[0]) |
                                     (int(struct.unpack('B', BF.read(1))[0]) << 8))
                    m_AcZ = np.int16(0x0000 | int(struct.unpack('B', BF.read(1))[0]) |
                                     (int(struct.unpack('B', BF.read(1))[0]) << 8))
                    tmp = np.int16(0x0000 | int(struct.unpack('B', BF.read(1))[0]) |
                                     (int(struct.unpack('B', BF.read(1))[0]) << 8))
                    m_GyX = np.int16(0x0000 | int(struct.unpack('B', BF.read(1))[0]) |
                                     (int(struct.unpack('B', BF.read(1))[0]) << 8))
                    m_GyY = np.int16(0x0000 | int(struct.unpack('B', BF.read(1))[0]) |
                                     (int(struct.unpack('B', BF.read(1))[0]) << 8))
                    m_GyZ = np.int16(0x0000 | int(struct.unpack('B', BF.read(1))[0]) |
                                     (int(struct.unpack('B', BF.read(1))[0]) << 8))
                    c_C = np.uint16(0x0000 | int(struct.unpack('B', BF.read(1))[0]) |
                                     (int(struct.unpack('B', BF.read(1))[0]) << 8))
                    c_R = np.uint16(0x0000 | int(struct.unpack('B', BF.read(1))[0]) |
                                     (int(struct.unpack('B', BF.read(1))[0]) << 8))
                    c_G = np.uint16(0x0000 | int(struct.unpack('B', BF.read(1))[0]) |
                                     (int(struct.unpack('B', BF.read(1))[0]) << 8))
                    c_B = np.uint16(0x0000 | int(struct.unpack('B', BF.read(1))[0]) |
                                     (int(struct.unpack('B', BF.read(1))[0]) << 8))
                    PPM = np.int(0x0000 | int(struct.unpack('B', BF.read(1))[0]) |
                                   (int(struct.unpack('B', BF.read(1))[0]) << 8) |
                                   (int(struct.unpack('B', BF.read(1))[0]) << 16) |
                                   (int(struct.unpack('B', BF.read(1))[0]) << 24))

                    rgb = getrgb888(c_R, c_G, c_B)
                    #create array for json
                    #bin_data[j] = {
                    #    "index": j,
                    #    "m_AcX": m_AcX,
                    #    "m_AcY": m_AcY,
                    #    "m_AcZ": m_AcZ,
                    #    "tmp": tmp,
                    #    "m_GyX": m_GyX,
                    #    "m_GyY": m_GyY,
                    #    "m_GyZ": m_GyZ,
                    #    "C": np.uint16(c_C),
                    #    "R": rgb[0],
                    #    "G": rgb[1],
                    #    "B": rgb[2],
                    #    "PPM": PPM
                    #    }
                    temp = BF.read(1)  # 0x03 (End of File)
                    
                    #write row for csv (excel)
                    wr.writerow([j, m_AcX, m_AcY, m_AcZ, tmp, m_GyX, m_GyY, m_GyZ, np.uint16(c_C), np.uint16(rgb[0]), np.uint16(rgb[1]), np.uint16(rgb[2]), PPM])
                    
                    #plot for color image (png)
                    temp = []
                    for i in range(100):
                        temp.append(rgb)
                    for i in range(50):
                        img.append(temp)
                    j += 1
                #create png file
                img2 = np.array(img)
                plt.imsave(pngname, img2)
                plt.cla()
                #create json and dump
                #TF_dump = json.dumps(bin_data, cls=NumpyEncoder)
                #json.dump(TF_dump, TF)
                CF.close()