package liekevk.pokedexroute.Application;

import liekevk.pokedexroute.Object.Pokemon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import skaro.pokeapi.client.PokeApiClient;

@Service
public class PokemonInitializer {
    private PokeApiClient pokeAPIClient;

    @Autowired
    public void setPokeAPIClient(PokeApiClient pokeAPIClient) {
        this.pokeAPIClient = pokeAPIClient;
    }

    public void setNameAndSprite(Pokemon pokemon) {
        skaro.pokeapi.resource.pokemon.Pokemon apiPokemon = pokeAPIClient.getResource(
                skaro.pokeapi.resource.pokemon.Pokemon.class, String.valueOf(pokemon.getNationalDexNumber()))
                        .block();

        pokemon.setName(apiPokemon.getName());
        pokemon.setSprite(apiPokemon.getSprites().getFrontDefault());
    }
}
