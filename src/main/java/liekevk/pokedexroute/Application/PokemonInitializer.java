package liekevk.pokedexroute.Application;

import liekevk.pokedexroute.Object.Pokemon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import skaro.pokeapi.client.PokeApiClient;

@Service
public class PokemonInitializer {
    private PokeApiClient pokeAPIClient;

    @Autowired
    public void setPokeAPIClient(PokeApiClient pokeAPIClient) {
        this.pokeAPIClient = pokeAPIClient;
    }

    public Mono<Pokemon> setNameAndSprite(Pokemon pokemon) {
        return pokeAPIClient.getResource(
                skaro.pokeapi.resource.pokemon.Pokemon.class, String.valueOf(pokemon.getNationalDexNumber()))
                .map(apiPokemon -> {
                    pokemon.setName(apiPokemon.getName());
                    pokemon.setSprite(apiPokemon.getSprites().getFrontDefault());
                    return pokemon;
                });
    }
}
