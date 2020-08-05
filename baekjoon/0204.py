#3052
L = [int(input()) % 42 for i in range(0,10)]
cnt = 0
for i in range(0, 42):
    for j in range(0, 10):
        if(i == L[j]):
            cnt += 1
            break
print(cnt)

#1546
N = int(input())
L = [int(i)  for i in input().split(" ")]
print(sum(L)/N/max(L)*100)