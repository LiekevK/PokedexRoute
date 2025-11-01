import PokeAPI from "pokedex-promise-v2";
import { writeFile } from "node:fs/promises";

const CHUNK_SIZE = 2;
const api = new PokeAPI();

console.log("Fetching type data...");

const types = await api.getTypesList({ limit: 10000 })
    .then((res) => res.results)
    .then((types) => Promise.all(types.map(
        (type) => api.getTypeByName(type.name))))
    .then((types) => types.map((type) =>
        type.names.find((name) => name.language.name === "en")?.name || type.name));

console.log(`${types.length} types fetched`);
console.log("Fetching game version data...");

const games = await api.getVersionsList({ limit: 10000 })
    .then((res) => res.results)
    .then((versions) => Promise.all(versions.map(
        (version) => api.getVersionByName(version.name))))
    .then((versions) => versions.map((version) =>
        version.names.find((name) => name.language.name === "en")?.name || version.name));

console.log(`${games.length} game versions fetched.`);
console.log("Fetching pokemon species data...");

const pokemon = [];
const pokemonChunks = await api.getPokedexByName("national")
    .then((pokedex) => pokedex.pokemon_entries)
    .then((entries) => {
        const chunks = [];
        for (let i = 0; i < entries.length; i += CHUNK_SIZE) {
            chunks.push(entries.slice(i, i + CHUNK_SIZE));
        }
        return chunks;
    });

for (const chunk of pokemonChunks) {
    pokemon.push(...await Promise.all(chunk.map(async (entry) => ({
        entry_number: entry.entry_number,
        pokemon_species: await api.getPokemonSpeciesByName(entry.pokemon_species.name)
    })))
        .then((entries) => entries.map((entry) => ({
            dex_number: entry.entry_number,
            name: entry.pokemon_species.names.find(
                (name) => name.language.name === "en")?.name || entry.pokemon_species.name,
        }))));
}

console.log(`${pokemon.length} pokemon species fetched.`);
console.log("Fetching pokemon with types data...");

const pokemonWithTypes = [];

for (const chunk of pokemonChunks) {
    pokemonWithTypes.push(...await Promise.all(chunk.map(async (entry) => ({
        entry_number: entry.entry_number,
        pokemon_species: await api.getPokemonSpeciesByName(entry.pokemon_species.name)
    })))
        .then((entries) => Promise.all(entries.map(
            async (entry) => ({
                entry_number: entry.entry_number,
                pokemon: await api.getPokemonByName(
                    entry.pokemon_species.varieties.find((variety) => variety.is_default).pokemon.name
                )
            }))))
        .then((entries) => Promise.all(entries.map(async (entry) => ({
            entry_number: entry.entry_number,
            types: await Promise.all(entry.pokemon.types.map(async (type) =>
                await api.getTypeByName(type.type.name)))
        }))))
        .then((entries) => entries.map((entry) => ({
            entry_number: entry.entry_number,
            types: entry.types.map((type) => type.names.find((name) =>
                name.language.name === "en")?.name || type.name)
        })))
        .then((entries) =>
            entries.map((entry) => entry.types.map((type) => ({
                dex_number: entry.entry_number,
                typing: type
            }))).flat()
        ));
}

console.log(`${pokemonWithTypes.length} pokemon with types fetched.`);
console.log("Fetching route data...");

const routes = [];
const routeChunks = await api.getLocationsList({ limit: 10000 })
    .then((res) => res.results)
    .then((locations) => {
        const chunks = [];
        for (let i = 0; i < locations.length; i += CHUNK_SIZE) {
            chunks.push(locations.slice(i, i + CHUNK_SIZE));
        }
        return chunks;
    });

for (const chunk of routeChunks) {
    routes.push(...await Promise.all(chunk.map((location) => api.getLocationByName(
            // Temporary workaround for location 610
            /^[A-Za-z0-9_-]+$/.test(location.name) ? location.name : location.url.replace(/.*?\/(\d+)\/.*/, "$1")
        )))
        .then((locations) => Promise.all(
            locations.map(async (location) => ({
                name: location.name,
                names: location.names,
                areas: await Promise.all(location.areas.map((area) =>
                    api.getLocationAreaByName(area.name)))
            }))))
        .then((locations) => locations.flatMap((location) => ({
            name: location.name,
            names: location.names,
            versions: new Set(location.areas.flatMap((area) => area.pokemon_encounters.flatMap(
                (encounter) => encounter.version_details.flatMap((detail) => detail.version.name)
            ))).values()
        })))
        .then((locations) => Promise.all(locations.map(async (location) => ({
            name: location.name,
            names: location.names,
            versions: await Promise.all(location.versions.map((version) => api.getVersionByName(version)).toArray())
        }))))
        .then((locations) => locations.map((location) => ({
            name: location.names.find((name) => name.language.name === "en")?.name || location.name,
            versions: location.versions.map((version) => version.names.find(
                (name) => name.language.name === "en")?.name || version.name
            )
        })))
        .then((locations) => locations.flatMap((location) => location.versions.map((version) => ({
            routeName: location.name,
            gameName: version
        }))))
    );
}

console.log(`${routes.length} routes fetched.`);
console.log("Generating SQL");

function escaped(str) {
    return str.replaceAll("'", "''");
}

const sql = [
    types.map((type) => `INSERT INTO [Type] ([typing]) VALUES ('${escaped(type)}')\nGO`).join("\n"),
    games.map((game) => `INSERT INTO [Game] ([gameName]) VALUES ('${escaped(game)}')\nGO`).join("\n"),
    pokemon.map((p) => `INSERT INTO [Pokemon] ([dexNumber], [Name]) VALUES (${p.dex_number}, '${escaped(p.name)}')\nGO`).join("\n"),
    pokemonWithTypes.map((pType) =>
        `INSERT INTO [pokemon_has_type] ([typing], [dexNumber]) VALUES ('${escaped(pType.typing)}', ${pType.dex_number})\nGO`).join("\n"),
    routes.map((route) => `INSERT INTO [Route] ([gameName], [routeName]) VALUES ('${escaped(route.gameName)}', '${escaped(route.routeName)}')\nGO`).join("\n")
].join("\n\n");

const baseDir = new URL(import.meta.url);
await writeFile(new URL("./insert.sql", baseDir), sql, { encoding: "utf-8" });

console.log("SQL generated.");