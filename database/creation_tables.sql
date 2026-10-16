CREATE DATABASE IF NOT EXISTS gestion_sujets
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gestion_sujets;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS Candidature;
DROP TABLE IF EXISTS Sujet;
DROP TABLE IF EXISTS Administrateur;
DROP TABLE IF EXISTS Enseignant;
DROP TABLE IF EXISTS Etudiant;
DROP TABLE IF EXISTS ResponsablePedagogique;
DROP TABLE IF EXISTS Utilisateurs;
SET FOREIGN_KEY_CHECKS = 1;

-- Classe abstraite Utilisateurs (enum Role -> VARCHAR + CHECK)
CREATE TABLE Utilisateurs (
    id          BIGINT NOT NULL AUTO_INCREMENT,
    nom         VARCHAR(100) NOT NULL,
    prenom      VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL,
    motDePasse  VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_utilisateurs_email UNIQUE (email),
    CONSTRAINT ck_utilisateurs_role
        CHECK (role IN ('ADMIN','ENSEIGNANT','ETUDIANT','RESPONSABLE'))
) ENGINE=InnoDB;

-- Sous-classes : id = clé primaire ET clé étrangère vers Utilisateurs
CREATE TABLE Administrateur (
    id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_Administrateur_Utilisateurs FOREIGN KEY (id)
        REFERENCES Utilisateurs (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE ResponsablePedagogique (
    id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_Responsable_Utilisateurs FOREIGN KEY (id)
        REFERENCES Utilisateurs (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Etudiant (
    id              BIGINT NOT NULL,
    numeroEtudiant  VARCHAR(30)  NOT NULL,
    filiere         VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_etudiant_numero UNIQUE (numeroEtudiant),
    CONSTRAINT FK_Etudiant_Utilisateurs FOREIGN KEY (id)
        REFERENCES Utilisateurs (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Enseignant (
    id           BIGINT NOT NULL,
    departement  VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_Enseignant_Utilisateurs FOREIGN KEY (id)
        REFERENCES Utilisateurs (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Sujet : un enseignant propose 0..* sujets
CREATE TABLE Sujet (
    id            BIGINT NOT NULL AUTO_INCREMENT,
    Ens_id        BIGINT NOT NULL,
    titre         VARCHAR(200) NOT NULL,
    description   TEXT,
    capaciteMax   INT NOT NULL,
    disponible    BOOLEAN NOT NULL DEFAULT TRUE,
    dateCreation  DATE NOT NULL DEFAULT (CURRENT_DATE),
    PRIMARY KEY (id),
    CONSTRAINT ck_sujet_capacite CHECK (capaciteMax > 0),
    CONSTRAINT FK_proposer FOREIGN KEY (Ens_id)
        REFERENCES Enseignant (id)
) ENGINE=InnoDB;

-- Candidature : étudiant 1..1, sujet 1..1, responsable 0..1
CREATE TABLE Candidature (
    id               BIGINT NOT NULL AUTO_INCREMENT,
    Etu_id           BIGINT NOT NULL,
    Suj_id           BIGINT NOT NULL,
    Res_id           BIGINT NULL,
    motivation       TEXT,
    dateCandidature  DATE NOT NULL DEFAULT (CURRENT_DATE),
    dateDecision     DATE NULL,
    statut           VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    PRIMARY KEY (id),
    CONSTRAINT uq_candidature UNIQUE (Etu_id, Suj_id),
    CONSTRAINT ck_candidature_statut
        CHECK (statut IN ('EN_ATTENTE','ACCEPTEE','REJETEE')),
    CONSTRAINT FK_deposer  FOREIGN KEY (Etu_id) REFERENCES Etudiant (id),
    CONSTRAINT FK_recevoir FOREIGN KEY (Suj_id) REFERENCES Sujet (id),
    CONSTRAINT FK_traiter  FOREIGN KEY (Res_id)
        REFERENCES ResponsablePedagogique (id) ON DELETE SET NULL
) ENGINE=InnoDB;
