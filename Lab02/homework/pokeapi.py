from collections import defaultdict
from multiprocessing import Pool

import numpy as np
import requests
from threading import Thread

import server

api_address = f"https://pokeapi.co/api/v2/"


def wrap_html(title: str, body: str):
    return f"""
        <html lang="en-US">
        <head>
            <meta charset="utf-8">
            <title>{title}</title>
            
            <style>
            table {{
              font-family: arial, sans-serif;
              border-collapse: collapse;
            }}
            
            td, th {{
              border: 1px solid #dddddd;
              text-align: left;
              padding: 8px;
            }}
            
            tr:nth-child(even) {{
              background-color: #dddddd;
            }}
            
            .wrapper{{
              display: flex;
              flex-wrap: wrap;
            }}
            
            .div-1 {{
              background-color: #EBEBEB;
              width: 100px;
              margin: 5;
              border: 3px solid #111111;
              text-align: center;
            }}
            .div-2 {{
              margin-left: 20;
              margin-right: 20;
            }}
            </style>
        </head>
        <body>
        
        {body}

        </body>
        </html>
    """


def single_request(url):
    return requests.get(url)


def parallel_request(urls):
    with Pool(12) as p:
        results = p.map(single_request, urls)
    for r in results:
        r.raise_for_status()
    return results


def create_sprite_block(json):
    img_url = json['sprites']['front_default']
    title = json['name'].capitalize()
    pkmn_url = server.url + f"api/pokemon/info?pokemon={title}"
    return f"<div class=\"div-1\"><a href = {pkmn_url}><img src={img_url} alt={title} width=100 height=100 " \
           f"align=middle></a><h5>{title}</h5></div> "


def create_table_block(d: dict):
    content = ""
    for k, v in d.items():
        content += f"<tr><th>{k}</th><td>{v}</td></tr>\n"
    return f"<table>{content}</table>"


def get_pokemon_page(pokemon_name: str):
    def create_pokemon_block(json):
        def retrieve_stats(json):
            res = dict()
            for stat in json['stats']:
                res[stat['stat']['name'].capitalize()] = stat['base_stat']
            return res

        return f"""
        <h1>{json['name'].capitalize()}</h1>
        <div>    
            {create_sprite_block(json)}
            <h3>Information</h3>
            {create_table_block({"Pokedex no.": json['id'], "Type": ', '.join(list(map(lambda x: x['type']['name'], json['types'])))}
                                | {"Abilities": ', '.join(list(map(lambda x: x['ability']['name'].capitalize(), json['abilities'])))})} 
            <br>
            <h3>Statistics</h3>
            {create_table_block(retrieve_stats(json))}
        </div>
        """

    endpoint = api_address + "pokemon/" + pokemon_name.lower() + "/"
    response = requests.get(endpoint)
    response.raise_for_status()
    return wrap_html(pokemon_name.capitalize(), create_pokemon_block(response.json()))


def get_ability_page(ability_name: str):
    def create_ability_block(json):
        urls = []
        for pokemon in json['pokemon']:
            urls.append(pokemon['pokemon']['url'])
        responses = parallel_request(urls[:3])
        result = f"""<h3>Ability: {json['name'].capitalize()}</h3>
        <div>
               {create_table_block({"Description": json['effect_entries'][0]['effect']})}
        </div>
        <h3>Pokemons with this ability</h3>
        <div class=\"wrapper\">"""
        for response in responses:
            json = response.json()
            result += create_sprite_block(json)
        result += "</div>"
        return result

    endpoint = api_address + "ability/" + ability_name.lower() + "/"
    reply = requests.get(endpoint)
    reply.raise_for_status()

    return wrap_html(ability_name.capitalize(), create_ability_block(reply.json()))


def get_type_page(type_name: str):
    def create_type_block(json):
        def list_types(L):
            L = list(map(lambda x: x['name'], L))
            if not L:
                return "None"
            return ', '.join(L)

        def get_all_stats(responses):
            res = defaultdict(list)
            for response in responses:
                for stat in response.json()['stats']:
                    res[stat['stat']['name']].append(stat['base_stat'])
            return res

        def get_stat_div(stats, stat_name):
            return f"""
                <div class=\"div-2\">
                <h4>{stat_name.capitalize()}</h4>
                       {create_table_block({"Minimum": min(stats[stat_name.lower()]),
                                            "Maximum": max(stats[stat_name.lower()]),
                                            "Average": int(np.average(stats[stat_name.lower()]))})}
                </div>"""

        urls = []
        for pokemon in json['pokemon']:
            urls.append(pokemon['pokemon']['url'])
        responses = parallel_request(urls[:3])
        dmg_rel = json['damage_relations']
        stats = get_all_stats(responses)
        result = f"""<h1>Type: {json['name'].capitalize()}</h1>
                <h2>Effectivity</h2>
                <div>
                   {create_table_block({"No damage to": list_types(dmg_rel['no_damage_to']),
                                        "Half damage to": list_types(dmg_rel['half_damage_to']),
                                        "Double damage to": list_types(dmg_rel['double_damage_to']),
                                        "No damage from": list_types(dmg_rel['no_damage_from']),
                                        "Half damage from": list_types(dmg_rel['half_damage_from']),
                                        "Double damage from": list_types(dmg_rel['double_damage_from']), })}
                </div>
                <h2>Statistics</h2>
                <div class=\"wrapper\">
                {get_stat_div(stats, 'hp')}
                {get_stat_div(stats, 'attack')}
                {get_stat_div(stats, 'defense')}
                {get_stat_div(stats, 'special-attack')}
                {get_stat_div(stats, 'special-defense')}
                {get_stat_div(stats, 'speed')}
                </div>
                <h2>Pokemons with this type</h2>
                <div class=\"wrapper\">"""
        for response in responses:
            json = response.json()
            result += create_sprite_block(json)
        result += "</div>"
        return result

    endpoint = api_address + "type/" + type_name.lower() + "/"
    reply = requests.get(endpoint)
    reply.raise_for_status()
    return wrap_html(type_name.capitalize(), create_type_block(reply.json()))
