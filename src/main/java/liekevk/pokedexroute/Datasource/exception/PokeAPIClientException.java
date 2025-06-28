package liekevk.pokedexroute.Datasource.exception;

public class PokeAPIClientException extends Exception {
    public PokeAPIClientException() {
        super();
    }

    public PokeAPIClientException(String message) {
        super(message);
    }

    public PokeAPIClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
