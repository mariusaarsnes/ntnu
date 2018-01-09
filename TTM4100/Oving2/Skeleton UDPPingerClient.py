import time
from socket import *

# Get the server hostname and port as command line arguments                    
host = "localhost"
port = 8080
timeout = 1 # in seconds
 
# Create UDP client socket

# Note the second parameter is NOT SOCK_STREAM
# but the corresponding to UDP
clientSocket = socket(AF_INET, SOCK_DGRAM)

# Set socket timeout as 1 second
clientSocket.settimeout(1)

# Sequence number of the ping message
ptime = 0  

# Ping for 10 times
while ptime < 10: 
    ptime += 1
    
    # Record the "sent time"
    sTime = time.time() * 1000
    # Format the message to be sent as in the Lab description	
    data = "Ping " + str(ptime) + "_" + str(sTime)
    
    try:                    

        # Send the UDP packet with the ping message
        clientSocket.sendto(data, (host,port))
        
        # Receive the server response
        modifiedData = clientSocket.recv(1024)
        
        # Record the "received time"
        rTime = time.time() * 1000
        
        # Display the server response as an output
        print (modifiedData)
    
        # Round trip time is the difference between sent and received time
        RTT = rTime - sTime
        print("RTT:" + str(RTT))
        
    except Exception as e:
        print(e)
        # Server does not response
        # Assume the packet is lost
        print "Request timed out."
        continue
    
# Close the client socket
clientSocket.close()

