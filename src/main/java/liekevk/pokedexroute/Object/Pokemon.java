package liekevk.pokedexroute.Object;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pokemon {
    private int nationalDexNumber;
    private int generationNumber;
    private int dexNumber;
    private String name;
    private String sprite;

    public Pokemon(int nationalDexNumber, int generationNumber, int dexNumber) {
        this.nationalDexNumber = nationalDexNumber;
        this.generationNumber = generationNumber;
        this.dexNumber = dexNumber;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setSprite(String sprite) {
        this.sprite = sprite;
    }
}
