package liekevk.pokedexroute.Presentation;


import com.fasterxml.jackson.core.JsonProcessingException;
import liekevk.pokedexroute.Application.IPokedexInitializer;
import liekevk.pokedexroute.Pokemon;
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
    public List<Pokemon> setPokedex() throws JsonProcessingException {

        List<Pokemon> pokemon = pokedexInitializer.receiveAllPokemon(26);

        if (pokemon.isEmpty()) {
            pokedexInitializer.fillDatabaseWithPokemon(26);
            pokemon = pokedexInitializer.receiveAllPokemon(26);
        }

        return pokemon;
    }

}
