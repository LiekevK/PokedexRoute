package liekevk.pokedexroute.Presentation;


import liekevk.pokedexroute.Application.IPokedexInitializer;
import liekevk.pokedexroute.Object.Pokemon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class PokedexInitializerController {

    private IPokedexInitializer pokedexInitializer;

    @Autowired
    public void setPokedexInitializer(IPokedexInitializer pokedexInitializer) {
        this.pokedexInitializer = pokedexInitializer;
    }

    @GetMapping("/getPokedex")
    public Mono<List<Pokemon>> setPokedex() {
        return pokedexInitializer.receiveAllPokemon(26)
                .switchIfEmpty(
                        Flux.defer(() -> pokedexInitializer.fillDatabaseWithPokemon(26)
                                    .thenMany(pokedexInitializer.receiveAllPokemon(26))
                        )
                )
                .collectList();
    }

    @GetMapping("/getListOfRoutes")
    public Mono<List<String>> setRoutesNames() {
        return pokedexInitializer.receiveListOfRoutes(26).collectList();
    }

    @GetMapping("/getRoutePokemon")
    public Mono<List<Pokemon>> setRoutes(@RequestParam String route) {
        return pokedexInitializer.receiveRoutePokemon(route, 26)
                .collectList();
    }
}

