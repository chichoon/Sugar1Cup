tc = int(input())

for j in range(tc):
    c = [input().split(' ') for k in range(3)]
    if(c[0][0] == c[1][0]):
        if(c[0][1] == c[2][1]):
            c.append([c[2][0], c[1][1]])
        elif(c[1][1] == c[2][1]):
            c.append([c[2][0], c[0][1]])
    elif(c[0][0] == c[2][0]):
        if(c[0][1] == c[1][1]):
            c.append([c[1][0], c[2][1]])
        elif(c[1][1] == c[2][1]):
            c.append([c[1][0], c[0][1]])
    elif(c[1][0] == c[2][0]):
        if(c[0][1] == c[1][1]):
            c.append([c[0][0], c[2][1]])
        elif(c[0][1] == c[2][1]):
            c.append([c[0][0], c[1][1]])
    print(' '.join(c[3]))