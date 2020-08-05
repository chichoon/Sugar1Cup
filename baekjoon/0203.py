#10818
N = int(input())
I = [int(i)  for i in input().split(" ")]
min = 1000000
max = -1000000

for i in range(0,N):
    if min > I[i] :
        min = I[i]
    if max < I[i] :
        max = I[i]

print(min, max)

#2562
max = -100000
n = 0

for i in range(0, 9):
    I = int(input())
    if max < I :
        max = I
        n = i

print(max)
print(n+1)

#2577
I = 1
cnt = 0
for i in range(0, 3):
    I = I*int(input())
for j in range(0, 10):
    for o in str(I):
        if o == str(j):
            cnt = cnt + 1
    print(cnt)
    cnt = 0