r0 = 4
r2 = 1
r1 = 1
r2 = r2 * r0
r0 = r0 -r1
while r0 > r1:
    r1 = 1
    r2 = r2*r0
    r0=r0-r1
print(r2)