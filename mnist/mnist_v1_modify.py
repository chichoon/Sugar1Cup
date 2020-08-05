import tensorflow.compat.v1 as tf
tf.disable_v2_behavior()
import numpy as np
from matplotlib import pyplot as plt
#데이터셋 불러오기
mnist = tf.keras.datasets.mnist

#x_train - MNIST 이미지
#y_train - MNIST 레이블
(x_train, y_train), (x_test, y_test) = mnist.load_data()
x_train, x_test = x_train / 255.0, x_test / 255.0

#채널 차원 추가
#마지막 채널은 컬러 채널로, 이미지가 흑백이기 때문에 1
x_train = x_train[..., tf.newaxis]
x_test = x_test[..., tf.newaxis]

#train_dataset , test_dataset
#배치 형태로 저장됨 - BatchDataset
train_ds = tf.data.Dataset.from_tensor_slices((x_train, y_train)).shuffle(5000).batch(100)
test_ds = tf.data.Dataset.from_tensor_slices((x_test, y_test)).batch(32)

################### 변수 선언 ####################
L = 0.001 #Learninng rate
Epochs = 20 #Total Epoch - 총 학습 횟수
BS = 200 #Batch Size

#Neural Network Variables
input_size = 784 #input data size, image size = 28 * 28
layer1_size = 1024 #Layer 1 size
layer2_size = 512 #Layer 2 size
layer3_size = 256 #Layer 3 size
outlayer_size = 128 #Output layer size
output_size = 10 #Output data size

############## 데이터 사이즈, 모양 ###############
print("X_Train Size : {}".format(x_train.size))
print("Y_Train Size : {}".format(y_train.size))
print("X_Train Shape : {}".format(x_train.shape))
print("Y_Train Shape : {}".format(y_train.shape))
print("X_Test Size : {}".format(x_test.size))
print("Y_Test Size : {}".format(y_test.size))
print("X_Test Shape : {}".format(x_test.shape))
print("Y_Test Shape : {}".format(y_test.shape))

################### 샘플 출력 ####################
batch = x_train[590]
plt.imshow(batch.reshape(28, 28), cmap = 'gray')
plt.show()
print("{}".format(y_train[590]))

################# 모델 변수 설정 ##################
#데이터 들어갈 placeholder 설정
X = tf.placeholder(tf.float32, [None, input_size])
Y = tf.placeholder(tf.float32, [None, output_size])

#Input Layer 변수 설정
W_input = tf.Variable(tf.random_normal([input_size, layer1_size]), name='W_input')
B_input = tf.Variable(tf.random_normal([layer1_size]), name = 'B_input')

#Layer1 변수 설정
W_layer1 = tf.Variable(tf.random_normal([layer1_size, layer2_size]), name='W_layer1')
B_layer1 = tf.Variable(tf.random_normal([layer2_size]), name = 'B_layer1')

#Layer2 변수 설정
W_layer2 = tf.Variable(tf.random_normal([layer2_size, layer3_size]), name='W_layer2')
B_layer2 = tf.Variable(tf.random_normal([layer3_size]), name = 'B_layer2')

#Layer3 변수 설정
W_layer3 = tf.Variable(tf.random_normal([layer3_size, outlayer_size]), name='W_layer3')
B_layer3 = tf.Variable(tf.random_normal([outlayer_size]), name = 'B_layer3')

#Output Layer 변수 설정
W_output = tf.Variable(tf.random_normal([outlayer_size, output_size]), name='W_output')
B_output = tf.Variable(tf.random_normal([output_size]), name = 'B_output')

############### 학습 모델 구성 ################
#Hypothesis 그래프 선언
input_hyp = tf.nn.relu(tf.matmul(X, W_input)+B_input)
layer1_hyp = tf.nn.relu(tf.matmul(input_hyp, W_layer1)+B_layer1)
layer2_hyp = tf.nn.relu(tf.matmul(layer1_hyp, W_layer2)+B_layer2)
layer3_hyp = tf.nn.relu(tf.matmul(layer2_hyp, W_layer3)+B_layer3)
output_hyp = tf.matmul(layer3_hyp, W_output)+B_output

#Cost함수 선언 (오차/손실)
cost = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(logits = output_hyp, labels = Y))

#Optimizer 함수 선언 (Cost를 최소로 수렴하게 하는)
opt = tf.train.AdamOptimizer(L)
train = opt.minimize(cost)

#################### 실행 #####################
#세션 선언, Weight와 Bias 초기화
sess = tf.Session()
sess.run(tf.global_variables_initializer())

#예측값과 정확도 선언
predicted = tf.equal(tf.argmax(output_hyp, 1), tf.argmax(Y, 1))
accuracy = tf.reduce_mean(tf.cast(predicted, tf.float32))

train_accuracy = list()

print("------------------------------")
print("Train(Optimization) Start")
for epoch in range(Epochs):
    ave_costfunc = 0 #Average Cost Function
    for x, y in train_ds:
        cost_val, acc_val, _ = sess.run([cost, accuracy, train], feed_dict = {X : x, Y : y})
        train_accuracy.append(acc_val)
        ave_costfunc = cost_val / totalBatch
        
    print("epoch : {}, cost : {}".format(epoch, ave_costfunc))
    
plt.plot(range(len(train_accuracy)), train_accuracy, linewidth = 2, label = 'Training')
plt.legend()
plt.title("Accuracy Result")
plt.show()

print("Train(Optimization) Finished")   
print("------------------------------")
print("Test Result :")

h_val, p_val, a_val = sess.run([output_hyp, predicted, accuracy], feed_dict = {X : x_test_, Y : y_test_})
print("\nHypothesis : {} \nPrediction : {} \nAccuracy : {}".format(h_val, p_val, a_val))

fig = plt.figure(figsize = (8, 15))
for i in range(10):
    c = 1
    for(image, label, h) in zip(x_test, y_test, h_val):
        predict, actual = np.argmax(h), np.argmax(label)
        if predict != i:
            continue
        if (c < 4 and i == actual) or (c >= 4 and i != actual):
            subplot = fig.add_subplot(10,4,i*6+c)
            subplot.set_xsticks([])
            subplot.set_ysticks([])
            subplot.set_title('%d / %d' % (prediction, actual))
            subplot.imshow(image.reshape((28,28)), vmin = 0, vmax = 1, cmap = plt.cm.gray_r, interpolation = "nearest")
            c += 1