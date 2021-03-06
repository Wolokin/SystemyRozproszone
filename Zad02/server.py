import os.path
from threading import Thread

import numpy as np
import requests
from flask import Flask, abort, request, send_from_directory
import pokeapi

app = Flask("pokeserver")
url = "http://127.0.0.1:5000/"


@app.route('/', methods=['GET'])
def main_page():
    try:
        return send_from_directory('.', 'pokeinfo.html')
    except TypeError:
        return "Unable to fetch default page, please check server's working directory"


def generic_request(func, args):
    try:
        return func(*args)
    except requests.exceptions.HTTPError as errh:  # 4xx and 5xx
        return "This request couldn't be completed. Please check your spelling and pokeapi.co status.<br><br>" + str(
            errh)
    except requests.exceptions.ConnectionError as errc:  # 3xx
        return "This request couldn't be completed. Please check your connection.<br><br>" + str(errc)
    except requests.exceptions.Timeout as errt:
        return "This request couldn't be completed. The API server didn't respond in time.<br><br>" + str(errt)
    except requests.exceptions.RequestException as err:
        return "This request couldn't be completed.<br><br>" + str(err)
    except KeyError as e:
        return "Something went very wrong inside the server: KeyError[" + str(e) + "]"
    except Exception as e:
        return "Unexpected error: " + str(e)


@app.route('/api/pokemon/info', methods=['GET'])
def pokemon_info_request():
    pokemon = request.args.get('pokemon')
    if pokemon is None:
        abort(400, "The url is incomplete - the 'pokemon' parameter is missing")

    return generic_request(pokeapi.get_pokemon_page, (pokemon,))


@app.route('/api/ability/info', methods=['GET'])
def ability_info_request():
    ability = request.args.get('ability')
    if ability is None:
        abort(400, "The url is incomplete - the 'ability' parameter is missing")

    return generic_request(pokeapi.get_ability_page, (ability,))


@app.route('/api/type/info', methods=['GET'])
def type_info_request():
    typing = request.args.get('type')
    if typing is None:
        abort(400, "The url is incomplete - the 'ability' parameter is missing")

    return generic_request(pokeapi.get_type_page, (typing,))


if __name__ == '__main__':
    app.run()
