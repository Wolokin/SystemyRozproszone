import pika

SUPPLIES = {
    'oxygen',
    'boots',
    'backpack'
}

EXCHANGE_NAME = 'expedition'
ADDRESS = 'localhost'


def initialize():
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(host=ADDRESS, heartbeat=600)
    )
    channel = connection.channel()

    channel.exchange_declare(
        exchange=EXCHANGE_NAME, exchange_type='topic')
    return connection, channel
