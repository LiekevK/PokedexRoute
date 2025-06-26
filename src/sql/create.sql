CREATE DATABASE PokedexRoute
GO

USE PokedexRoute
GO

CREATE TABLE Pokedex
(
	dexNumber INT NOT NULL PRIMARY KEY, 
	[status] BIT NOT NULL
)