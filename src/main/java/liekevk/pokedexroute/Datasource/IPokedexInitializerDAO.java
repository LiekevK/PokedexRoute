package liekevk.pokedexroute.Datasource;

import liekevk.pokedexroute.Pokemon;

import java.util.List;

public interface IPokedexInitializerDAO {
    public List<Pokemon> initializePokedex(int generation);
    public void addPokemonToPokedex(int dexNumber);
}
