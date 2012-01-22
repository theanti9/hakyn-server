CREATE DATABASE IF NOT EXISTS hakyn;

USE hakyn;

CREATE TABLE IF NOT EXISTS accounts (
	AccountID int PRIMARY KEY AUTO_INCREMENT,
	AccountName VARCHAR(30) NOT NULL,
	AccountPassword VARCHAR(60) NOT NULL,
	AccountRegisterDate DATETIME NOT NULL,
	AccountLastLogin DATETIME NULL
);

CREATE TABLE IF NOT EXISTS monsters (
	MonsterID INT PRIMARY KEY,
	MonsterName VARCHAR(30) NOT NULL,
	MonsterHP INT NOT NULL,
	MonsterLevel INT NOT NULL,
	MonsterStr INT NOT NULL,
	MonsterDef INT NOT NULL,
	MonsterAgi INT NOT NULL,
	MonsterSpeed INT NOT NULL,
	MonsterDamageMultiplier FLOAT NOT NULL,
	MonsterResistance1Type TINYINT NULL,
	MonsterResistance1Percent FLOAT NULL,
	MonsterResistance2Type TINYINT NULL,
	MonsterResistance2Percent FLOAT NULL,
	MonsterSkill1 INT NULL,
	MonsterSkill1Percent FLOAT NULL,
	MonsterSkill2 INT NULL,
	MonsterSkill2Percent FLOAT NULL,
	MonsterSkill3 INT NULL,
	MonsterSkill3Percent FLOAT NULL
);

CREATE TABLE IF NOT EXISTS skills (
	SkillID INT PRIMARY KEY,
	SkillName VARCHAR(30) NOT NULL,
	SkillType TINYINT NOT NULL,
	SkillMultiplier FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS zones (
	ZoneID INT PRIMARY KEY,
	ZoneName VARCHAR(30) NOT NULL,
	ZoneIP VARCHAR(40) NOT NULL,
	ZoneType TINYINT NOT NULL,
	ZoneMaxMonsters INT NOT NULL
);

CREATE TABLE IF NOT EXISTS monster_spawns (
	SpawnID INT PRIMARY KEY,
	ZoneID INT NOT NULL,
	MonsterID INT NOT NULL,
	MonsterSpawnChance FLOAT NOT NULL,
	MaxSpawn INT NOT NULL
);

CREATE TABLE IF NOT EXISTS zone_portals (
	PortalID INT PRIMARY KEY,
	PortalZoneID INT NOT NULL,
	PortalXCoord FLOAT NOT NULL,
	PortalYCoord FLOAT NOT NULL,
	LinkedZoneID INT NOT NULL,
	LinkedSpawnX FLOAT NOT NULL,
	LinkedSpawnY FLOAT NOT NULL
); 

INSERT INTO monsters VALUES(1, "Monster 1", 40, 1, 5, 5, 5, 1, 1.0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO monsters VALUES(2, "Monster 2", 45, 1, 3, 7, 5, 1, 1.0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO monsters VALUES(3, "Monster 3", 50, 2, 7, 7, 3, 1, 1.0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO monsters VALUES(4, "Monster 4", 55, 2, 9, 7, 5, 1, 1.0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO zones VALUES(1, "Zone 1", "2001:0:4137:9e76:452:2a58:b95b:e16d", 1, 20);
INSERT INTO monster_spawns VALUES(1, 1, 1, 0.75, 15);
INSERT INTO monster_spawns VALUES(2, 1, 2, 0.55, 10);
INSERT INTO monster_spawns VALUES(3, 1, 3, 0.35, 5);
INSERT INTO monster_spawns VALUES(4, 1, 4, 0.05, 1);