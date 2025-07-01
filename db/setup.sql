IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'PokedexRoute')
BEGIN
    CREATE DATABASE PokedexRoute;
END;
GO

USE PokedexRoute;
GO

DROP TABLE IF EXISTS Pokedex
GO

CREATE TABLE Pokedex
(
    nationalDexNumber INT NOT NULL PRIMARY KEY,
    generationNumber INT NOT NULL,
    dexNumber INT NOT NULL,
    [name] VARCHAR(255) NOT NULL,
    [status] BIT NOT NULL DEFAULT 0
)
GO