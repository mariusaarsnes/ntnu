__author__ = 'Marius'

li = [x for x in range(1,7)]

print (li)

for i in range(0,len(li)):
    if li[i] %2 != 0:
        li[i] *= -1

li.sort(reverse = True)
print(li)
