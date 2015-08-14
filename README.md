This project simply monitors the traffic on an internet device and will play a midi note for each packet it sees, the note it plays is 100% dependant on the source ip address of the packet.

You will need to include JNetPcap 1.4 or higher for this to work, instructions for getting this working with eclipse can be seen here: http://jnetpcap.com/?q=eclipse

Also, this will need root access on linux due to how linux handles permissions for these devices.

Note: this is 100% untested on windows, if you have any success on windows let me know so I can update this
