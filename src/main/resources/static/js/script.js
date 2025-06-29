document.addEventListener("DOMContentLoaded", () => {
    const pokedexData = document.getElementById("pokedexData");

    document.getElementById("getPokedex").addEventListener("click", function(){
        pokedexData.innerHTML = "";
        fetch("http://localhost:8080/getPokedex")
            .then (response => response.json())
            .then(data => {
                for (let i = 0; i < data.length; i++) {
                    const img = document.createElement("img");
                    img.src = data[i].sprite;
                    pokedexData.appendChild(img);
                }
            })
            .catch((error) => console.error("Error:", error));
    });

    document.getElementById("getRoutePokemon").addEventListener("click", () => {
        pokedexData.innerHTML = "";
        const dropdown = document.getElementById("routeDropdown");
        const selectedRoute = dropdown.value;

        fetch(`http://localhost:8080/getRoutePokemon?route=${encodeURIComponent(selectedRoute)}`)
            .then(response => response.json())
            .then(data => {
                console.log(data);
                // Display images:
                data.forEach(pokemon => {
                    const img = document.createElement("img");
                    img.src = pokemon.sprite;
                    pokedexData.appendChild(img);
                });
            })
            .catch(error => console.error("Error:", error));
    });

    fetch("http://localhost:8080/getListOfRoutes")
        .then(response => response.json())
        .then(data => {
            const dropdown = document.getElementById("routeDropdown");
            data.forEach(item => {
                const option = document.createElement("option");
                option.value = item;
                option.text = item;
                dropdown.appendChild(option);
            });
        })
        .catch(error => console.error("Error:", error));
});
