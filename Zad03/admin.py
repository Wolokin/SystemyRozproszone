import threading

from common import *


class AdminMessage(threading.Thread):
    def __init__(self):
        super().__init__()
        self.name = 'admin'
        self.connection, self.channel = initialize()

    def close(self):
        self.connection.close()


class AdminMessageReceiver(AdminMessage):

    def __init__(self):
        super().__init__()
        self.queue = self.channel.queue_declare(queue=self.name).method.queue
        self.channel.queue_bind(
            exchange=EXCHANGE_NAME, queue=self.queue, routing_key='supplies.#')
        self.channel.basic_consume(
            queue=self.queue, auto_ack=True, on_message_callback=self.msg_handler)

    def run(self):
        self.channel.start_consuming()

    @staticmethod
    def msg_handler(_ch, method, _properties, body):
        segments = method.routing_key.split('.')
        seg1, seg2 = segments[1], segments[2]

        if seg1 == 'order':
            team = body.decode()
            item = seg2
            print(f'{team} ordered {item}.')
        elif seg1 == 'conf':
            team = seg2
            msg = body.decode().split(',')
            supplier = msg[0]
            item = msg[1]
            i = msg[2]
            print(
                f'{supplier} confirmed the delivery of {item} to {team}. Order id: {i}.')


class AdminMessageSender(AdminMessage):
    def __init__(self):
        super().__init__()

    def run(self):
        print('Admin console.')
        print(
            'Types of messages:\n  T - teams\n  S - suppliers\n  A - all')

        keys = {
            'T': 'teams',
            'S': 'suppliers',
            'A': 'all'
        }

        while True:
            print('Type <T|S|A>:<message>')
            msg = input()
            t, msg = msg.split(':')[:2]
            if t not in keys:
                print('Wrong message type. Use T, S or A.')
                continue
            self.channel.basic_publish(
                exchange=EXCHANGE_NAME, routing_key=keys[t], body=msg.encode('utf-8'))
            print(f"Sent '{msg}' to {keys[t]}.")


class Admin:
    @staticmethod
    def start():
        sender = AdminMessageSender()
        receiver = AdminMessageReceiver()
        try:
            sender.start()
            receiver.start()
            sender.join()
            receiver.join()
        except KeyboardInterrupt:
            sender.close()
            receiver.close()


if __name__ == '__main__':
    admin = Admin()
    admin.start()
