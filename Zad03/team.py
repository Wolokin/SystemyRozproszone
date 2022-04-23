import sys
import threading

from common import *


class TeamMessage(threading.Thread):
    def __init__(self, name):
        super().__init__()
        self.name = name
        self.connection, self.channel = initialize()

    def close(self):
        self.connection.close()


class TeamMessageReceiver(TeamMessage):

    def __init__(self, name):
        super().__init__(name)
        self.queue = self.channel.queue_declare(queue=name).method.queue
        for key in [f'supplies.conf.{self.name}', 'teams', 'all']:
            self.channel.queue_bind(
                exchange=EXCHANGE_NAME, queue=self.queue, routing_key=key)
        self.channel.basic_consume(
            queue=self.queue, auto_ack=True, on_message_callback=self.msg_handler)

    def run(self):
        self.channel.start_consuming()

    @staticmethod
    def msg_handler(_ch, method, _properties, body):
        print("> ", end="")
        t = method.routing_key.split('.')[0]
        if t == 'supplies':
            msg = body.decode().split(',')
            supplier = msg[0]
            item = msg[1]
            i = msg[2]
            print(f'Received {item} from {supplier}. Order id: {i}.')
        elif t == 'teams' or t == 'all':
            print("ADMIN:", body.decode())


class TeamMessageSender(TeamMessage):
    def __init__(self, name):
        super().__init__(name)

    def run(self):
        print('What would you like to order?')

        while True:
            item = input().lower()
            if item not in SUPPLIES:
                print(f'Unsupported item: {item}.')
                continue

            self.channel.basic_publish(
                exchange=EXCHANGE_NAME, routing_key=f'supplies.order.{item}', body=self.name.encode('utf-8'))
            print(f'< Ordered: {item}. What would you like to order next?')


class Team:
    def __init__(self, name):
        self.name = name

    def start(self):
        sender = TeamMessageSender(self.name)
        receiver = TeamMessageReceiver(self.name)
        try:
            sender.start()
            receiver.start()
            sender.join()
            receiver.join()
        except KeyboardInterrupt:
            sender.close()
            receiver.close()


if __name__ == '__main__':
    if len(sys.argv) != 2:
        print('Wrong number of arguments. Expected one')
        exit(1)
    team = Team(sys.argv[1])
    team.start()
