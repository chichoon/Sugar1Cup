tc = int(input())

for j in range(tc):
    n = int(input())
    a = n & 0xFF
    b = (n >> 8) & 0xFF
    c = (n >> 16) & 0xFF
    d = (n >> 24) & 0xFF
    
    nn = 0x00000000 | d | (c<<8) | (b<<16) | (a<<24)
    print(nn)

    4