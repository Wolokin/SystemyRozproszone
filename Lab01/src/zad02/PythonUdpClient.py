import socket

serverIP = "127.0.0.1"
serverPort = 9008
# msg = "Ping Python Udp!"
msg = "źółta gęś"

print('PYTHON UDP CLIENT')
client = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
client.sendto(bytes(msg, 'UTF-8'), (serverIP, serverPort))

