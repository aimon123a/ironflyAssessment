#!/usr/bin/env python

import socket, ssl
import os

def main():

    subscribeList = []

    context = ssl.create_default_context(ssl.Purpose.CLIENT_AUTH)
    context.load_cert_chain("datafeedserver.crt", "datafeedserver.key", "password")
    
    soc = socket.socket()
    host = "0.0.0.0"
    port = 2004
    print("Server running at: " + host, port)
    soc.bind((host, port))
    soc.listen(5)
    
    '''
        msg coming in would be the subscribe or unsubscribe info
    '''

    while True:
        newsocket, addr = soc.accept()
        conn = context.wrap_socket(newsocket, server_side=True)
        print("Got connection from",addr)
        length_of_message = int.from_bytes(conn.recv(2), byteorder='big')
        msg = conn.recv(length_of_message).decode("UTF-8")
        # get request header
        print(msg)

        if msg[0] == 's':
            ''' subscribe the company source'''
            stocksplit = msg[1:].split(";")
            for stock in stocksplit:
                subscribeList.append(stock)

        elif msg[0] == 'u':
            ''' unsubscribe the company source'''
            stocksplit = msg[1:].split(";")
            for stock in stocksplit:
                subscribeList.remove(stock)

        '''simulate the data feed source sending to java microservices server'''
        loaded_json = json.loads('tickData.json')
        for row in loaded_json:
            ''' further implement JSON parsing to indicate which company is this ticker feed belongs to, e.g. vodapone, but ran out of time'''
            ''' format as "lastPrice": 192, "time": "26/07/2019 10:00:18.105", "volume": 31605"'''
            msgToSend = ""
        	sprintf(msgToSend, "%s: %d %s: %s %s: %d" % (row, loaded_json[0][row]))
        	''' sleep for 5 secs meaning, to send a ticker feed every 5 seconds'''
        	sleep(5)
        	''' send feed to java server'''
            message_to_send = "right".encode("UTF-8")
            conn.send(len(message_to_send).to_bytes(2, byteorder='big'))
            conn.send(message_to_send)

        '''conn.shutdown(socket.SHUT_RDWR)
        conn.close()
        print("Connection with ", addr, " closed")'''
        
if __name__ == '__main__':
    main()
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
