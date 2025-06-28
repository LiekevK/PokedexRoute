package liekevk.pokedexroute.Datasource;

import liekevk.pokedexroute.Object.Pokemon;

import java.util.List;

public interface IPokedexInitializerDAO {
    List<Pokemon> initializePokedex(int generation);
    void addPokemonToPokedex(List<Pokemon> pokedex);

    Pokemon getPokemonOnName(int generation, String name);
}
