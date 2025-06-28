package liekevk.pokedexroute.Datasource;

import liekevk.pokedexroute.Datasource.util.DatabaseProperties;
import liekevk.pokedexroute.Pokemon;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
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
                int dexNumber = resultSet.getInt("dexNumber");
                lists.add(new Pokemon(dexNumber));
            }
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return lists;
    }

    public void addPokemonToPokedex(int dexNumber) {
        try {
            Connection connection = DriverManager.getConnection(properties.connectionString());
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Pokedex (dexNumber) VALUES (?)");
            preparedStatement.setInt(1, dexNumber);
            preparedStatement.executeUpdate();
            connection.close();
        } catch (Exception e) {
            System.out.println("Can't add pokemon to pokedex");
            e.printStackTrace();
        }
    }
}
