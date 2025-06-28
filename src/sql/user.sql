use master
go

IF EXISTS (
    SELECT 1
    FROM sys.server_principals
    WHERE name = 'someone'
)
DROP LOGIN someone
GO

CREATE LOGIN someone WITH PASSWORD = 'SOMEONES_PC_PASSWORD'
    MUST_CHANGE, CHECK_EXPIRATION = ON, DEFAULT_DATABASE = PokedexRoute;
GO

USE PokedexRoute
GO

DROP USER IF EXISTS someone
GO

CREATE USER someone
    FROM LOGIN someone
GO

GRANT SELECT, INSERT, UPDATE, DELETE ON Pokedex TO someone
GO



