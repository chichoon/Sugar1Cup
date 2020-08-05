import math

N, R, D, eX, eY = input().split()
N = int(N)
R = int(R)
D = int(D)
eX = int(eX)
eY = int(eY)

L = []
Damage = 0 #나중에 적에게 쏘아올릴 총 데미지 계산용

for i in range(0, N):
    L.append([])
    iX, iY = input().split()
    iX = int(iX)
    iY = int(iY)
    L[i].append(iX)
    L[i].append(iY)
    L[i].append(0)
    L[i].append(D) #i번째 포탑의 X좌표, Y좌표, 적 포탑의 사정거리 존재여부, 에너지 양
    if math.sqrt(pow(L[i][0] - eX, 2) + pow(L[i][1] - eY, 2)) <= R: #적 포탑이 사정거리 내부에 존재할 때
        L[i][2] = 1 #플래그를 1로
        print(L[i][2])
    else: #아니면
        L[i][2] = 0 #플래그를 0으로
        print(L[i][2])

for i in range(0, N):
    for j in range(0, N):
        if i == j:
            continue
        elif math.sqrt(pow(L[i][0] - L[j][0], 2) + pow(L[i][1] - L[j][1], 2)) <= R: #i와 j의 거리가 R보다 작을 때
            if L[i][2] == 1: #i번째 포탑 사정거리가 R보다 작음
                if L[j][2] == 1: #j번째 포탑 사정거리가 R보다 작음
                    continue #SKIP
                elif L[j][2] == 0: #j번째 포탑 사정거리가 R보다 큼
                    L[i][3] = L[i][3] + (L[j][3]/2) #j가 i에게 에너지 넘겨줌
                    L[j][3] = 0
            elif L[i][2] == 0: #i번째 포탑 사정거리가 R보다 큼
                if L[j][2] == 1: #j번째 포탑 사정거리가 R보다 작음
                    L[j][3] = L[j][3] + (L[i][3]/2) #i가 j에게 에너지 넘겨줌
                    L[i][3] = 0 
                elif L[j][2] == 0:#j번째 포탑 사정거리가 R보다 큼
                    A = math.sqrt(pow(L[i][0] - eX, 2) + pow(L[i][1] - eY, 2))
                    B = math.sqrt(pow(L[j][0] - eX, 2) + pow(L[j][1] - eY, 2))
                    if A >= B:
                        L[j][3] = L[j][3] + (L[i][3]/2) #i가 j에게 에너지 넘겨줌
                        L[i][3] = 0 
                    elif A < B:
                        L[i][3] = L[i][3] + (L[j][3]/2) #j가 i에게 에너지 넘겨줌
                        L[j][3] = 0
for i in range(0, N):
    print(L[i][3])
    if L[i][2] == 1:
        Damage = Damage + L[i][3]
print(Damage)
        