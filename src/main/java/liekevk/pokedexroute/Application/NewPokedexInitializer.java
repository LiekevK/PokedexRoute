package liekevk.pokedexroute.Application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import liekevk.pokedexroute.Datasource.IPokedexInitializerDAO;
import liekevk.pokedexroute.Datasource.exception.PokeAPIClientException;
import liekevk.pokedexroute.Object.Pokemon;
import liekevk.pokedexroute.Object.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.location.Location;
import skaro.pokeapi.resource.locationarea.LocationArea;
import skaro.pokeapi.resource.locationarea.PokemonEncounter;
import skaro.pokeapi.resource.pokedex.Pokedex;
import skaro.pokeapi.resource.region.Region;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewPokedexInitializer implements IPokedexInitializer {
    private IPokedexInitializerDAO pokedexInitializerDAO;
    private PokeApiClient pokeAPIClient;
    private PokemonInitializer pokemonInitializer;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
    public List<Pokemon> receiveAllPokemon(int generation) throws JsonProcessingException, PokeAPIClientException {
        List<Pokemon> pokemon = pokedexInitializerDAO.initializePokedex(generation);
        for (Pokemon p : pokemon) {
            pokemonInitializer.setNameAndSprite(p);
        }

        return pokemon;
    }

    public void fillDatabaseWithPokemon(int generation) throws JsonProcessingException {
//        String json = requestDex(generation);
//        JsonNode jsonNode = objectMapper.readTree(json);
//
//        ArrayList<Pokemon> pokedex = new ArrayList<>();
//
//        for (JsonNode node : jsonNode.get("pokemon_entries")) {
//            int dexNumber = node.get("entry_number").asInt();
//            pokedex.add(new Pokemon(requestNationalNumber(json, dexNumber), generation, dexNumber));
//        }
//
//        pokedexInitializerDAO.addPokemonToPokedex(pokedex);
    }

    private int requestNationalNumber(String json, int dexNumber) throws JsonProcessingException {
//        try {
//            JsonNode jsonNode = objectMapper.readTree(json);
//            String newJson = null;
//
//            for (JsonNode node : jsonNode.get("pokemon_entries")) {
//                if (node.get("entry_number").asInt() == dexNumber) {
//                    newJson = pokeAPIClient.get(PokeAPIClient.Routes.pokemonSpecies(node.get("pokemon_species").get("name").asText()));
//                }
//            }
//            if (newJson != null && !newJson.isEmpty() ) {
//                JsonNode jsonNode2 = objectMapper.readTree(newJson);
//                return jsonNode2.get("id").asInt();
//            }
//        } catch (PokeAPIClientException e) {
//            e.printStackTrace();
//        }
        return -1;
    }


    private Mono<Pokedex> requestDex(int generation) {
        return pokeAPIClient.getResource(Pokedex.class, String.valueOf(generation));
    }

    public ArrayList<String> receiveListOfRoutes(int generation) throws JsonProcessingException, PokeAPIClientException {
        List<Route> routes = requestAllRoutes(generation);
        ArrayList<String> routeNames = new ArrayList<>();
        for (Route route : routes) {
            routeNames.add(route.getName());
        }
        return routeNames;
    }

    @Override
    public List<Pokemon> receiveRoutePokemon(String routeName, int generation) throws JsonProcessingException, PokeAPIClientException {
        return List.of();
    }

    private List<Route> requestAllRoutes(int generation) throws JsonProcessingException, PokeAPIClientException {
        Flux<PokemonEncounter> encounters = requestDex(generation)
                .flatMap((pokedex) -> pokeAPIClient.followResource(pokedex::getRegion, Region.class))
                .flatMapMany((region) -> pokeAPIClient.followResources(region::getLocations, Location.class))
                .flatMap((location) -> pokeAPIClient.followResources(location::getAreas, LocationArea.class))
                .flatMapIterable(LocationArea::getPokemonEncounters)
                .flatMap((encounter) -> encounter.getPokemon().getName());

        return List.of();

//        for (JsonNode area : areas) {
//            ArrayList<Pokemon> pokemons = new ArrayList<>();
//            for (JsonNode pokemon : area.get("pokemon_encounters")) {
//                String name = area.get("pokemon_encounters").get("pokemon").get("name").asText();
//                Pokemon pokemon1 = pokedexInitializerDAO.getPokemonOnName(generation, name);
//                pokemonInitializer.setNameAndSprite(pokemon1);
//                pokemons.add(pokemon1);
//            }
//
//
//            Route route = new Route(area.get("location").get("name").asText(), new ArrayList<>());
//            routes.add(route);
//        }
//
//        return routes;
    }
}
