package liekevk.pokedexroute.Application;

import liekevk.pokedexroute.Datasource.IPokedexInitializerDAO;
import liekevk.pokedexroute.Object.Pokemon;
import liekevk.pokedexroute.Object.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.location.Location;
import skaro.pokeapi.resource.locationarea.LocationArea;
import skaro.pokeapi.resource.pokedex.Pokedex;
import skaro.pokeapi.resource.pokemonspecies.PokemonSpecies;
import skaro.pokeapi.resource.region.Region;

@Service
public class PokedexInitializer implements IPokedexInitializer {
    private IPokedexInitializerDAO pokedexInitializerDAO;
    private PokeApiClient pokeAPIClient;
    private PokemonInitializer pokemonInitializer;

    @Autowired
    public void setPokedexInitializerDAO(IPokedexInitializerDAO pokedexInitializerDAO) {
        this.pokedexInitializerDAO = pokedexInitializerDAO;
    }

    @Autowired
    public void setPokeAPIClient(PokeApiClient pokeAPIClient) {
        this.pokeAPIClient = pokeAPIClient;
    }

    @Autowired
    public void setPokemonInitializer(PokemonInitializer pokemonInitializer) {
        this.pokemonInitializer = pokemonInitializer;
    }

    @Override
    public Flux<Pokemon> receiveAllPokemon(int generation) {
        return Mono.fromCallable(() -> pokedexInitializerDAO.initializePokedex(generation))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .flatMap(pokemon -> pokemonInitializer.setSprite(pokemon)); /// CHANGE TO ONLY SPRITE
    }

    @Override
    public Mono<Void> fillDatabaseWithPokemon(int generation) {
        return requestDex(generation)
                .flatMapIterable(Pokedex::getPokemonEntries)
                .flatMap(entry -> {
                    int dexNumber = entry.getEntryNumber();
                    String name = entry.getPokemonSpecies().getName();
                    return pokeAPIClient.followResource(entry::getPokemonSpecies, PokemonSpecies.class)
                            .map(PokemonSpecies::getId)
                            .map(nationalNumber -> new Pokemon(nationalNumber, generation, dexNumber, name));
                })
                .collectList()
                .flatMap(pokemons -> Mono.fromRunnable(() ->
                        pokedexInitializerDAO.addPokemonToPokedex(pokemons)
                ).subscribeOn(Schedulers.boundedElastic()))
                .then(); // returns Mono<Void>
    }

    private Mono<Pokedex> requestDex(int generation) {
        return pokeAPIClient.getResource(Pokedex.class, String.valueOf(generation));
    }

    @Override
    public Flux<String> receiveListOfRoutes(int generation) {
        return requestAllRoutes(generation)
                .map(Route::getName);
    }

    private Flux<Route> requestAllRoutes(int generation) {
        return requestDex(generation)
                .flatMap((pokedex) -> pokeAPIClient.followResource(pokedex::getRegion, Region.class))
                .flatMapMany((region) -> pokeAPIClient.followResources(region::getLocations, Location.class))
                .flatMap((location) -> pokeAPIClient.followResources(location::getAreas, LocationArea.class))
                .flatMap((area) -> {
                    String locationName = area.getLocation().getName();

                    return Flux.fromIterable(area.getPokemonEncounters())
                            .flatMap(encounter -> {
                                String name = encounter.getPokemon().getName();

                                return Mono.fromCallable(() ->
                                    pokedexInitializerDAO.getPokemonOnName(generation, name)
                                )
                                .subscribeOn(Schedulers.boundedElastic())
                                .flatMap(pokemon -> pokemonInitializer.setSprite(pokemon));
                            })
                            .collectList()
                            .map(pokemons -> new Route(locationName, pokemons));

                });
    }

    @Override
    public Flux<Pokemon> receiveRoutePokemon(String routeName, int generation) {
        return requestAllRoutes(generation)
                .filter((route) -> route.getName().equals(routeName))
                .flatMapIterable(Route::getPokemons)
                .distinct(Pokemon::getNationalDexNumber);
    }
}
