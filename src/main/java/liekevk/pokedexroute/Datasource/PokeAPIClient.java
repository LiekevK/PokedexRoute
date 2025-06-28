package liekevk.pokedexroute.Datasource;

import liekevk.pokedexroute.Datasource.exception.PokeAPIClientException;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

@Component
public class PokeAPIClient {
    private static final String API_URL = "https://pokeapi.co/api/v2";

    public String get(String endpoint) throws PokeAPIClientException {
        try {
            InputStream is = request(endpoint, "GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (IOException e) {
            throw new PokeAPIClientException("Unable to convert response to string", e);
        }
    }

    private InputStream request(String endpoint, String method) throws PokeAPIClientException {
        try {
            HttpURLConnection conn = createConnection(endpoint, method);
            return conn.getInputStream();
        } catch (IOException e) {
            throw new PokeAPIClientException("Unable to receive response from PokeAPI", e);
        }
    }

    private HttpURLConnection createConnection(String endpoint, String method) throws PokeAPIClientException {
        try {
            URL url = URI.create(API_URL + endpoint).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            return conn;
        } catch (IOException e) {
            throw new PokeAPIClientException("Cannot connect to PokeAPI", e);
        }
    }

    public static class Routes {
        public static String pokemonSpecies(String name) {
            return "/pokemon-species/" + name;
        }
        public static String pokemon(int dexNumber) {
            return "/pokemon/" + dexNumber;
        }
        public static String pokedex(int dexNumber) {
            return "/pokedex/" + dexNumber;
        }
        public static String region(int regionId) {
            return "/region/" + regionId;
        }
        public static String location(int locationId) {
            return "/location/" + locationId;
        }
        public static String locationArea(int locationAreaId) {
            return "/location-area/" + locationAreaId;
        }
    }
}
