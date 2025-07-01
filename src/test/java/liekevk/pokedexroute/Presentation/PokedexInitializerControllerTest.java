package liekevk.pokedexroute.Presentation;

import liekevk.pokedexroute.Application.IPokedexInitializer;
import liekevk.pokedexroute.Object.Pokemon;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.*;

@WebFluxTest(controllers = PokedexInitializerController.class)
public class PokedexInitializerControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private IPokedexInitializer pokedexInitializer;

    @Test
    public void testGetPokedexReturnsPokemonList() {
        // Arrange
        final int generationNumber = 26;
        List<Pokemon> expectedPokemonList = List.of(
                new Pokemon(1, generationNumber, 1, "Bulbasaur"),
                new Pokemon(4, generationNumber, 4, "Charmander"),
                new Pokemon(7, generationNumber, 7, "Squirtle")
        );

        when(pokedexInitializer.receiveAllPokemon(generationNumber))
                .thenReturn(Flux.fromIterable(expectedPokemonList));

        when(pokedexInitializer.fillDatabaseWithPokemon(26))
                .thenReturn(Mono.empty());

        // Act, Assert
        webTestClient.get()
                .uri("/getPokedex")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Pokemon.class)
                .isEqualTo(expectedPokemonList);

        verify(pokedexInitializer).receiveAllPokemon(26);
        verify(pokedexInitializer, never()).fillDatabaseWithPokemon(26);
    }
}
