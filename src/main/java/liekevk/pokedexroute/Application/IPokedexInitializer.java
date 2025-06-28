package liekevk.pokedexroute.Application;

import com.fasterxml.jackson.core.JsonProcessingException;
import liekevk.pokedexroute.Datasource.exception.PokeAPIClientException;
import liekevk.pokedexroute.Object.Pokemon;

import java.util.ArrayList;
import java.util.List;

public interface IPokedexInitializer {

    List<Pokemon> receiveAllPokemon(int generation) throws JsonProcessingException, PokeAPIClientException;

    void fillDatabaseWithPokemon(int generation) throws JsonProcessingException;

    ArrayList<String> receiveListOfRoutes(int generation) throws JsonProcessingException, PokeAPIClientException;

    List<Pokemon> receiveRoutePokemon(String routeName, int generation) throws JsonProcessingException, PokeAPIClientException;
}
