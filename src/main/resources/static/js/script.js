document.getElementById("getPokedex").addEventListener("click", function(){
    fetch("http://localhost:8080/getPokedex")
        .then (response => response.text())
        .then(data => {
            document.getElementById("pokedexData").textContent = data;

        })
        .catch(console.error("Error:", error));
})


