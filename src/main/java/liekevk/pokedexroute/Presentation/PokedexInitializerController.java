package liekevk.pokedexroute.Presentation;


import com.fasterxml.jackson.core.JsonProcessingException;
import liekevk.pokedexroute.Application.IPokedexInitializer;
import liekevk.pokedexroute.Datasource.exception.PokeAPIClientException;
import liekevk.pokedexroute.Object.Pokemon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PokedexInitializerController {

    private IPokedexInitializer pokedexInitializer;

    @Autowired
    public void setPokedexInitializer(IPokedexInitializer pokedexInitializer) {
        this.pokedexInitializer = pokedexInitializer;
    }

    @GetMapping("/getPokedex")
    public List<Pokemon> setPokedex() throws JsonProcessingException, PokeAPIClientException {

        List<Pokemon> pokemon = pokedexInitializer.receiveAllPokemon(26);

        if (pokemon.isEmpty()) {
            pokedexInitializer.fillDatabaseWithPokemon(26);
            pokemon = pokedexInitializer.receiveAllPokemon(26);
        }

        return pokemon;
    }

    @GetMapping("/getRoutePokemon")
    public List<Pokemon> setRoutes() throws JsonProcessingException, PokeAPIClientException {
        List<Pokemon> pokemon = pokedexInitializer.receiveRoutePokemon("cerulean-city-area", 26);
        return pokemon;
    }

    @GetMapping("/getListOfRoutes")
    public List<String> setRoutesNames() throws JsonProcessingException, PokeAPIClientException {
        List<String> routes = pokedexInitializer.receiveListOfRoutes(26);
        return routes;
    }

}
