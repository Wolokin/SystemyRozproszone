import requests

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
            </style>
        </head>
        <body>
        
        {body}

        </body>
        </html>
    """


def error_400(context):
    return wrap_html("Client error", f"The information for the {context} could't be found. Please double check that "
                                     f"the information you've requested exists and doesn't contain a spelling mistake")


def error_500():
    return wrap_html("Server error", f"The pokeapi server is unavailable at this moment, please try again later.")


def handle_response_code(code, context):
    if 400 <= code < 500:
        return error_400(context)
    elif 500 <= code:
        return error_500()
    else:
        return wrap_html("Unknown reponse code", f"The external server returned code {code}")


def wrap_sprite(url: str, alt: str = ""):
    return f"<img src={url} alt={alt} width=100 height=100 align=middle background-color=#dddddd>"


def tablify(d):
    content = ""
    for k, v in d.items():
        content += f"<tr><th>{k}</th><td>{v}</td></tr>\n"
    return f"<table>{content}</table>"


def parse_pokemon_json(json):
    return f"""<h3>{json['name'].capitalize()}</h3>
    <div>
                     {wrap_sprite(json['sprites']['front_default'])}
                     {tablify({ "pokedex no.":json['id'],
                                "":0})}
    </div>"""


def get_pokemon_info(pokemon_name: str):
    endpoint = api_address + "pokemon/" + pokemon_name.lower() + "/"
    response = requests.get(endpoint)
    if response.status_code != 200:
        return handle_response_code(response.status_code, f"pokemon {pokemon_name}")
    json = response.json()
    return wrap_html(pokemon_name.capitalize(), parse_pokemon_json(json))

#
# def single_request(subdir):
#     endpoint = api_address + subdir
#     response = requests.get()
