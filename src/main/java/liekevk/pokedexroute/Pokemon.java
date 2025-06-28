package liekevk.pokedexroute;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pokemon {
    private int dexNumber;
    private String name;
    private String imageUrl;

    public Pokemon(int dexNumber, String name, String imageUrl) {
        this.dexNumber = dexNumber;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public int getDexNumber() {
        return dexNumber;
    }
}
