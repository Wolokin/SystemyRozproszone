import socket

def main():
    serverIP = "127.0.0.1"
    serverPort = 9008
    msg_bytes = (300).to_bytes(4, byteorder='little')

    print('PYTHON UDP CLIENT')
    connectionHandler = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    connectionHandler.sendto(msg_bytes, (serverIP, serverPort))
    buff, address = connectionHandler.recvfrom(1024)
    print(int.from_bytes(buff, byteorder='big'))

if __name__ == '__main__':
    main()