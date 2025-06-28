package liekevk.pokedexroute.Application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import liekevk.pokedexroute.Datasource.IPokedexInitializerDAO;
import liekevk.pokedexroute.Datasource.PokeAPIClient;
import liekevk.pokedexroute.Datasource.exception.PokeAPIClientException;
import liekevk.pokedexroute.Object.Pokemon;
import liekevk.pokedexroute.Object.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static liekevk.pokedexroute.Datasource.PokeAPIClient.Routes;

@Service
public class PokedexInitializer implements IPokedexInitializer{

    private IPokedexInitializerDAO pokedexInitializerDAO;
    private PokeAPIClient pokeAPIClient;
    private PokemonInitializer pokemonInitializer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public void setPokedexInitializerDAO(IPokedexInitializerDAO pokedexInitializerDAO) {
        this.pokedexInitializerDAO = pokedexInitializerDAO;
    }

    @Autowired
    public void setPokeAPIClient(PokeAPIClient pokeAPIClient) {
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
        String json = requestDex(generation);
        JsonNode jsonNode = objectMapper.readTree(json);

        ArrayList<Pokemon> pokedex = new ArrayList<>();

        for (JsonNode node : jsonNode.get("pokemon_entries")) {
            int dexNumber = node.get("entry_number").asInt();
            pokedex.add(new Pokemon(requestNationalNumber(json, dexNumber), generation, dexNumber));
        }

        pokedexInitializerDAO.addPokemonToPokedex(pokedex);
    }

    private int requestNationalNumber(String json, int dexNumber) throws JsonProcessingException {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            String newJson = null;

            for (JsonNode node : jsonNode.get("pokemon_entries")) {
                if (node.get("entry_number").asInt() == dexNumber) {
                    newJson = pokeAPIClient.get(Routes.pokemonSpecies(node.get("pokemon_species").get("name").asText()));
                }
            }
            if (newJson != null && !newJson.isEmpty() ) {
                JsonNode jsonNode2 = objectMapper.readTree(newJson);
                return jsonNode2.get("id").asInt();
            }
        } catch (PokeAPIClientException e) {
            e.printStackTrace();
        }
        return -1;
    }


    private String requestDex(int generation) {
        try {
            return pokeAPIClient.get(Routes.pokedex(generation));
        } catch (PokeAPIClientException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> receiveListOfRoutes(int generation) throws JsonProcessingException, PokeAPIClientException {
        List<Route> routes = requestAllRoutes(generation);
        ArrayList<String> routeNames = new ArrayList<>();
        for (Route route : routes) {
            routeNames.add(route.getName());
        }
        return routeNames;
    }

    private List<Route> requestAllRoutes(int generation) throws JsonProcessingException, PokeAPIClientException {
        String json = requestDex(generation);
        JsonNode dex = objectMapper.readTree(json);
        List<Route> routes = new ArrayList<>();

        JsonNode region = objectMapper.readTree(pokeAPIClient.get(dex.get("region").get("url").asText()));

        List<JsonNode> areas = region.get("locations").valueStream()
                .map((location) -> {
                    try {
                        return objectMapper.readTree(pokeAPIClient.get(location.get("url").asText()));
                    } catch (PokeAPIClientException | JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .flatMap((location) -> location.get("areas").valueStream())
                .toList();

        for (JsonNode area : areas) {
            ArrayList<Pokemon> pokemons = new ArrayList<>();
            for (JsonNode pokemon : area.get("pokemon_encounters")) {
                String name = area.get("pokemon_encounters").get("pokemon").get("name").asText();
                Pokemon pokemon1 = pokedexInitializerDAO.getPokemonOnName(generation, name);
                pokemonInitializer.setNameAndSprite(pokemon1);
                pokemons.add(pokemon1);
            }


            Route route = new Route(area.get("location").get("name").asText(), new ArrayList<>());
            routes.add(route);
        }
        System.out.println(routes);
        return routes;
    }

    @Override
    public List<Pokemon> receiveRoutePokemon(String routeName, int generation) throws JsonProcessingException, PokeAPIClientException {
        List<Pokemon> pokemon = new ArrayList<>();
        List<Route> routes = requestAllRoutes(generation);
        for (Route route : routes) {
            if (route.getName().equals(routeName)) {
                pokemon = route.getPokemons();
            }
        }
        return pokemon;
    }
}
