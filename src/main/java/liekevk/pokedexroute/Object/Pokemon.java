package liekevk.pokedexroute.Object;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pokemon {
    private int nationalDexNumber;
    private int generationNumber;
    private int dexNumber;
    private String name;
    private String sprite;

    public Pokemon(int nationalDexNumber, int generationNumber, int dexNumber, String name) {
        this.nationalDexNumber = nationalDexNumber;
        this.generationNumber = generationNumber;
        this.dexNumber = dexNumber;
        this.name = name;
    }

    public int getDexNumber() {
        return dexNumber;
    }

    public int getNationalDexNumber() {
        return nationalDexNumber;
    }

    public int getGenerationNumber() {
        return generationNumber;
    }
    public String getName() {
        return name;
    }
    public String getSprite() {
        return sprite;
    }

    public void setSprite(String sprite) {
        this.sprite = sprite;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pokemon pokemon)) return false;
        return nationalDexNumber == pokemon.nationalDexNumber && generationNumber == pokemon.generationNumber &&
                dexNumber == pokemon.dexNumber && Objects.equals(name, pokemon.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nationalDexNumber, generationNumber, dexNumber, name);
    }
}
