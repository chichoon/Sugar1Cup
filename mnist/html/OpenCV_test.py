#!/usr/bin/env python
# coding: utf-8

# In[14]:


import cv2
from matplotlib import pyplot as plt
import numpy as np
img = cv2.imread('ku.png', 1)
cv2.imshow('Show Image', img)
cv2.waitKey(0)

img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
print(img.shape)
print(img[40, 20])
plt.imshow(img)
plt.show()


# In[4]:


for i in range(100):
    for j in range(100):
        


# In[12]:


sobel_x = cv2.Sobel(img, cv2.CV_64F, 1, 0, ksize=3) 
sobel_y = cv2.Sobel(img, cv2.CV_64F, 0, 1, ksize=3) 
sobel_x = cv2.convertScaleAbs(sobel_x) 
sobel_y = cv2.convertScaleAbs(sobel_y) 
img_sobel = cv2.addWeighted(sobel_x, 1, sobel_y, 1, 0)
plt.imshow(img_sobel)
plt.show()


# In[20]:


blur = cv2.GaussianBlur(img, (5,5), 10,10) 
plt.imshow(blur)
plt.show()


# In[21]:


blur2 = cv2.medianBlur(blur, ksize=11) 
plt.imshow(blur2)
plt.show()


# In[26]:


kernel = np.array([[1,1,1],[1,-8,1],[1,1,1]]) 
dst = cv2.filter2D(img, -1, kernel) 
dst2 = cv2.filter2D(dst, -1, kernel)
plt.imshow(dst)
plt.show()


# In[27]:


plt.imshow(dst2)
plt.show()


# In[ ]:




