import os
import numpy as np
import struct
from sklearn.model_selection import train_test_split
import tensorflow as tf
from tensorflow.keras import models
from tensorflow.keras import layers
from keras.utils.np_utils import to_categorical
from tensorflow.keras.preprocessing.text import Tokenizer
from matplotlib import pyplot as plt

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


def createdata(direction):

    curr_path = direction
    txt_path = os.path.join(os.path.abspath(os.getcwd()), 'tmp_filelist.txt')
    dataX, dataY = [], []

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
        j = 0;

        for line in lines:
            bfname = ''.join([os.path.splitext(line)[0], '.bin'])
            with open(bfname, 'rb') as BF:
                while True:
                    temp = BF.read(1)  # 0x02 (Start of File)
                    if not temp: break
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
                    dataX.append(rgb)
                    dataY.append(os.path.splitext(os.path.basename(line))[0])
                    temp = BF.read(1)  # 0x03 (End of File)
            j += 1
    dataX = np.array(dataX)

    t = Tokenizer()
    t.fit_on_texts(dataY)
    dataY_=t.texts_to_sequences(dataY)
    dataY = to_categorical(dataY_)
    #카테고리에 대한 one-hot encoding

    return dataX, dataY


def func_keras(dataX, dataY):

    x_train, x_test, y_train, y_test = train_test_split(dataX, dataY, test_size=0.2, shuffle=True, stratify=dataY, random_state=34)
    
    model = models.Sequential()
    model.add(layers.Dense(64, input_dim=3, activation='relu'))
    model.add(layers.Dense(64, activation='relu'))
    model.add(layers.Dense(32, activation='relu'))
    model.add(layers.Dense(np.shape(y_train)[1], activation='softmax'))

    model.compile(optimizer='rmsprop',
                  loss='categorical_crossentropy',
                  metrics=['accuracy']
                  )

    hist = model.fit(x_train,
                    y_train,
                    epochs=500,
                    batch_size=32
                    )

    loss = hist.history['loss'][499]
    acc = hist.history['accuracy'][499]
    model.save("C:/Users/jiyoo/Documents/code/python/model/model.h5")
    tf.keras.backend.clear_session()
    return loss, acc