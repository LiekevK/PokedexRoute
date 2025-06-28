package liekevk.pokedexroute.Application;

import com.fasterxml.jackson.core.JsonProcessingException;
import liekevk.pokedexroute.Pokemon;

import java.util.List;

public interface IPokedexInitializer {

    public List<Pokemon> receiveAllPokemon(int generation);

    void fillDatabaseWithPokemon(int generation) throws JsonProcessingException;
}
