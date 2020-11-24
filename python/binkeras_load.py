import os
import glob
import numpy as np
import struct
from sklearn.model_selection import train_test_split
import tensorflow as tf
from tensorflow.keras import models
from tensorflow.keras import layers
from matplotlib import pyplot as plt
import collections
import socket 

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
#raw값을 rgb값으로 변환

def func_keras_test():
    json_file = open("C:/Users/jiyoo/Documents/code/python/model/model.json", "r") 
    loaded_model_json = json_file.read() 
    json_file.close()
    model = tf.keras.models.model_from_json(loaded_model_json) #load json to actual model
    model.load_weights('C:/Users/jiyoo/Documents/code/python/model/model.h5') #load weights saved before
    read_dict = np.load('dict.npy', allow_pickle = 'TRUE').item() #load dictionary correspond to onehot encoding
    
    direction = 'C:/Users/jiyoo/Documents/code/binfiles/temp.bin'
    dataX = [] #data list for actual estimation
    
    with open(direction, 'rb') as BF:
        while True:
            temp = BF.read(1)  # 0x02 (Start of File)
            if not temp: break
            #bin to actual integer
            c_R = np.uint16(0x0000 | int(struct.unpack('B', BF.read(1))[0]) |
                             (int(struct.unpack('B', BF.read(1))[0]) << 8))
            c_G = np.uint16(0x0000 | int(struct.unpack('B', BF.read(1))[0]) |
                             (int(struct.unpack('B', BF.read(1))[0]) << 8))
            c_B = np.uint16(0x0000 | int(struct.unpack('B', BF.read(1))[0]) |
                             (int(struct.unpack('B', BF.read(1))[0]) << 8))
            irs = np.uint16(0x0000 | int(struct.unpack('B', BF.read(1))[0]) |
                             (int(struct.unpack('B', BF.read(1))[0]) << 8))

            rgb = getrgb888(c_R, c_G, c_B)
            dataX.append(rgb)
            temp = int(struct.unpack('B', BF.read(1))[0])  # 0x03 (End of File)
            if temp != 3:
                temp = BF.read(1)
    
        dataX = np.array(dataX)
    
    xhat_idx = np.random.choice(dataX.shape[0], 100)
    xhat = dataX[xhat_idx]
    y = model.predict_classes(xhat)
    temp = []
    for i in range(100):
        temp.append(read_dict[y[i]])
    
    counter = collections.Counter(temp)
    
    print(counter.most_common(2)[0][0], ' : ', counter.most_common(2)[0][1], '%')
    #send string data (bev name) + percentage to android & android will do the rest
    return counter.most_common(3)

def send_server(list1):
    host = '192.168.0.11'  # 호스트 ip를 적어주세요 
    port = 9999            # 포트번호를 임의로 설정해주세요
    data2 = ""

    if len(list1) == 0:
        data2 = "null (0%) / null (0%) / null (0%)"
    elif len(list1) == 1:
        data2 = list1[0][0] + ' (' + str(list1[0][1]) + '%) / null (0%) / null (0%)'
    elif len(list1) == 2:
        data2 = list1[0][0] + ' (' + str(list1[0][1]) + '%) / ' + str(list1[1][0]) + ' (' + str(list1[1][1]) + '%) / null (0%)'
    elif len(list1) == 3:
        data2 = list1[0][0] + ' (' + str(list1[0][1]) + '%) / ' + str(list1[1][0]) + ' (' + str(list1[1][1]) + '%) / ' + str(list1[2][0]) + ' (' + str(list1[2][1]) + '%)'
    else:
        data2 = "null (0%) / null (0%) / null (0%)"

    server_sock = socket.socket(socket.AF_INET) 
    server_sock.bind((host, port)) 
    server_sock.listen(1) 

    print("기다리는 중") 
    client_sock, addr = server_sock.accept() 

    print('Connected by', addr) 
    data = client_sock.recv(1024) 
    print(data.decode("utf-8"), len(data)) 
    #print(data2.encode()) 
    
    client_sock.send(data2.encode()) 
    client_sock.close() 
    server_sock.close()
