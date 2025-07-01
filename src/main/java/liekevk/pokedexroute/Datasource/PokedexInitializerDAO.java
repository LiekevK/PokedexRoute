package liekevk.pokedexroute.Datasource;

import liekevk.pokedexroute.Object.Pokemon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class PokedexInitializerDAO implements IPokedexInitializerDAO {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Pokemon> initializePokedex(int generation) {
        String sql = "SELECT * FROM Pokedex";
        try {
            return jdbcTemplate.query(sql,
                    (rs, rowNum) -> new Pokemon(
                            rs.getInt("nationalDexNumber"),
                            rs.getInt("generationNumber"),
                            rs.getInt("dexNumber"),
                            rs.getString("name")
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addPokemonToPokedex(List<Pokemon> pokedex) {
        String sql = "INSERT INTO Pokedex (nationalDexNumber, generationNumber, dexNumber, name) VALUES (?,?,?,?)";

        List<Object[]> batchArgs = pokedex.stream()
            .map(p -> new Object[]{ // Map each Pokemon object to an Object array
                    p.getNationalDexNumber(),
                    p.getGenerationNumber(),
                    p.getDexNumber(),
                    p.getName()
            })
            .toList();

        try {
            int[] rowsAffectedPerBatch = jdbcTemplate.batchUpdate(sql, batchArgs);
            int totalRowsAffected = Arrays.stream(rowsAffectedPerBatch).sum();

            System.out.println("Successfully added " + totalRowsAffected + " pokemon entries in batch.");
        } catch (Exception e) {
            System.out.println("Can't add pokemon to pokedex");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Pokemon getPokemonOnName(int generation, String name) {
        String sql = "SELECT * FROM Pokedex WHERE generationNumber = ? AND name = ?";
        try {
            return jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> new Pokemon(
                            rs.getInt("nationalDexNumber"),
                            generation,
                            rs.getInt("dexNumber"),
                            name
                    ),
                    generation,
                    name
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            System.out.println("Can't get pokemon on name");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
