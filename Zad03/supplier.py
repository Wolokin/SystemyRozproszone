import sys
import threading

from common import *


class Supplier:

    def __init__(self, name, items):
        self.name = name

        self.accepted_items = items.split(',')
        for item in self.accepted_items:
            if item not in SUPPLIES:
                raise ValueError(f'Unsupported item: {item}')

        self.connection, self.channel = initialize()
        self.order_id = 1
        self.lock = threading.Lock()

        for item in self.accepted_items:
            self.queue = self.channel.queue_declare(queue=item).method.queue
            self.channel.queue_bind(
                exchange=EXCHANGE_NAME, queue=self.queue, routing_key=f'supplies.order.{item.lower()}')
            self.channel.basic_consume(
                queue=item, auto_ack=False, on_message_callback=self.msg_handler)

        self.admin_queue = self.channel.queue_declare(queue=self.name, exclusive=True).method.queue
        self.channel.queue_bind(exchange=EXCHANGE_NAME,
                                queue=self.admin_queue, routing_key='suppliers')
        self.channel.queue_bind(
            exchange=EXCHANGE_NAME, queue=self.admin_queue, routing_key='all')
        self.channel.basic_consume(
            queue=self.admin_queue, auto_ack=True, on_message_callback=self.admin_msg_handler)

        self.channel.basic_qos(prefetch_count=1)

    def start(self):

        print('Waiting for orders...')
        self.channel.start_consuming()

    def msg_handler(self, ch, method, _properties, body):
        team = body.decode()
        item = method.routing_key.split('.')[2]

        self.lock.acquire()
        i = self.order_id
        self.order_id += 1
        self.lock.release()

        print(f'New order (id: {i}): {item} for {team}.')
        print(f'Delivered {item} to {team}. Order id: {i}.')
        self.channel.basic_publish(
            exchange=EXCHANGE_NAME, routing_key=f'supplies.conf.{team}', body=f'{self.name},{item},{i}')
        ch.basic_ack(delivery_tag=method.delivery_tag)

    @staticmethod
    def admin_msg_handler(_ch, _method, _properties, body):
        message = body.decode()
        print("ADMIN:", message)


if __name__ == '__main__':
    if len(sys.argv) != 3:
        print(
            'Expected two arguments: <name> <list,of,supplies>')
        exit(1)

    supplier = Supplier(sys.argv[1], sys.argv[2])
    supplier.start()
