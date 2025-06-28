package liekevk.pokedexroute.Application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import liekevk.pokedexroute.Datasource.IPokedexInitializerDAO;
import liekevk.pokedexroute.Pokemon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class PokedexInitializer implements IPokedexInitializer{

    private IPokedexInitializerDAO pokedexInitializerDAO;

    @Autowired
    public void setPokedexInitializerDAO(IPokedexInitializerDAO pokedexInitializerDAO) {
        this.pokedexInitializerDAO = pokedexInitializerDAO;
    }

    @Override
    public List<Pokemon> receiveAllPokemon(int generation) {
        return pokedexInitializerDAO.initializePokedex(generation);
    }

    public void fillDatabaseWithPokemon(int generation) throws JsonProcessingException {
        String json = requestDex(generation);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);

        ArrayList<Integer> pokedex = new ArrayList<>();

        for (JsonNode node : jsonNode.get("pokemon_entries")) {
            pokedex.add(node.get("entry_number").asInt());
        }

        for (int dexNumber : pokedex) {
            pokedexInitializerDAO.addPokemonToPokedex(dexNumber);
        }
    }

    private String requestDex(int generation) {
        try {
            String apiUrl = "https://pokeapi.co/api/v2/pokedex/" + generation + "/";

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
