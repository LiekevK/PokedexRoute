package liekevk.pokedexroute.Application;

import liekevk.pokedexroute.Object.Pokemon;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPokedexInitializer {

    Flux<Pokemon> receiveAllPokemon(int generation);

    Mono<Void> fillDatabaseWithPokemon(int generation);

    Flux<String> receiveListOfRoutes(int generation);

    Flux<Pokemon> receiveRoutePokemon(String routeName, int generation);
}
