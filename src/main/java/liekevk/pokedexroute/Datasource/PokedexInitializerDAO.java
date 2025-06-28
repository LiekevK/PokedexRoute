package liekevk.pokedexroute.Datasource;

import liekevk.pokedexroute.Datasource.util.DatabaseProperties;
import liekevk.pokedexroute.Object.Pokemon;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PokedexInitializerDAO implements IPokedexInitializerDAO {
    private DatabaseProperties properties = new DatabaseProperties();

    @Override
    public List<Pokemon> initializePokedex(int generation) {
        List<Pokemon> lists = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(properties.connectionString());
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Pokedex");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int nationalDexNumber = resultSet.getInt("nationalDexNumber");
                int generationNumber = resultSet.getInt("generationNumber");
                int dexNumber = resultSet.getInt("dexNumber");
                lists.add(new Pokemon(nationalDexNumber, generationNumber, dexNumber));
            }
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return lists;
    }

    @Override
    public void addPokemonToPokedex(List<Pokemon> pokedex) {
        try {
            Connection connection = DriverManager.getConnection(properties.connectionString());
            String sql = "INSERT INTO Pokedex (nationalDexNumber, generationNumber, dexNumber) VALUES ";
            sql += pokedex.stream()
                    .map((p) -> "(?,?,?)")
                    .collect(Collectors.joining(","));

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i < pokedex.size(); i++) {
                Pokemon pokemon = pokedex.get(i);
                preparedStatement.setInt(i * 3 + 1, pokemon.getNationalDexNumber());
                preparedStatement.setInt(i * 3 + 2, pokemon.getGenerationNumber());
                preparedStatement.setInt(i * 3 + 3, pokemon.getDexNumber());
            }

            preparedStatement.executeUpdate();
            connection.close();
        } catch (Exception e) {
            System.out.println("Can't add pokemon to pokedex");
            e.printStackTrace();
        }
    }

    @Override
    public Pokemon getPokemonOnName(int generation, String name) {
        Pokemon pokemon = null;
        try {
            Connection connection = DriverManager.getConnection(properties.connectionString());
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Pokedex WHERE generationNumber = ? AND name = ?");
            preparedStatement.setInt(1, generation);
            preparedStatement.setString(2, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int nationalDexNumber = resultSet.getInt("nationalDexNumber");
                int generationNumber = resultSet.getInt("generationNumber");
                int dexNumber = resultSet.getInt("dexNumber");
                pokemon = new Pokemon(nationalDexNumber, generationNumber, dexNumber);
            }

            connection.close();
        } catch (Exception e) {
            System.out.println("Can't get pokemon on name");
            e.printStackTrace();
        }
        return pokemon;
    }
}
