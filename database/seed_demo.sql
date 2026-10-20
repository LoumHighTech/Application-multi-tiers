-- Donnees de demonstration pour l'application Gestion des projets tutorés.
-- Prerequis : avoir demarre le backend au moins une fois afin qu'Hibernate
-- cree les tables ainsi que les comptes admin@local.test et responsable@local.test.
-- Ce script peut etre relance : il ne supprime aucune donnee.
-- Mot de passe des comptes enseignants/etudiants ci-dessous : Demo123!
-- Hash BCrypt genere avec le meme format que BCryptPasswordEncoder (Spring Security).

USE gestionDeProjetBD;

START TRANSACTION;

-- Comptes enseignants
INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, role)
SELECT 'Ndiaye', 'Awa', 'enseignant.demo@local.test',
       '$2a$10$wg44dYk.ptZWAQwj9rs9lO8u4a6otB6HcJYAg1ndrGfxec2IzdJn2', 'ENSEIGNANT'
WHERE NOT EXISTS (SELECT 1 FROM utilisateurs WHERE email = 'enseignant.demo@local.test');

INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, role)
SELECT 'Fall', 'Moussa', 'enseignant2.demo@local.test',
       '$2a$10$wg44dYk.ptZWAQwj9rs9lO8u4a6otB6HcJYAg1ndrGfxec2IzdJn2', 'ENSEIGNANT'
WHERE NOT EXISTS (SELECT 1 FROM utilisateurs WHERE email = 'enseignant2.demo@local.test');

INSERT INTO enseignant (id, departement)
SELECT u.id, 'Informatique'
FROM utilisateurs u
WHERE u.email = 'enseignant.demo@local.test'
  AND NOT EXISTS (SELECT 1 FROM enseignant e WHERE e.id = u.id);

INSERT INTO enseignant (id, departement)
SELECT u.id, 'Mathématiques et informatique'
FROM utilisateurs u
WHERE u.email = 'enseignant2.demo@local.test'
  AND NOT EXISTS (SELECT 1 FROM enseignant e WHERE e.id = u.id);

-- Comptes etudiants
INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, role)
SELECT 'Sarr', 'Aminata', 'aminata.demo@local.test',
       '$2a$10$wg44dYk.ptZWAQwj9rs9lO8u4a6otB6HcJYAg1ndrGfxec2IzdJn2', 'ETUDIANT'
WHERE NOT EXISTS (SELECT 1 FROM utilisateurs WHERE email = 'aminata.demo@local.test');

INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, role)
SELECT 'Diop', 'Mamadou', 'mamadou.demo@local.test',
       '$2a$10$wg44dYk.ptZWAQwj9rs9lO8u4a6otB6HcJYAg1ndrGfxec2IzdJn2', 'ETUDIANT'
WHERE NOT EXISTS (SELECT 1 FROM utilisateurs WHERE email = 'mamadou.demo@local.test');

INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, role)
SELECT 'Ba', 'Fatou', 'fatou.demo@local.test',
       '$2a$10$wg44dYk.ptZWAQwj9rs9lO8u4a6otB6HcJYAg1ndrGfxec2IzdJn2', 'ETUDIANT'
WHERE NOT EXISTS (SELECT 1 FROM utilisateurs WHERE email = 'fatou.demo@local.test');

INSERT INTO etudiant (id, numero_etudiant, filiere)
SELECT u.id, 'GDIL-2026-001', 'Master 2 GDIL'
FROM utilisateurs u
WHERE u.email = 'aminata.demo@local.test'
  AND NOT EXISTS (SELECT 1 FROM etudiant e WHERE e.id = u.id);

INSERT INTO etudiant (id, numero_etudiant, filiere)
SELECT u.id, 'GDIL-2026-002', 'Master 2 GDIL'
FROM utilisateurs u
WHERE u.email = 'mamadou.demo@local.test'
  AND NOT EXISTS (SELECT 1 FROM etudiant e WHERE e.id = u.id);

INSERT INTO etudiant (id, numero_etudiant, filiere)
SELECT u.id, 'GDIL-2026-003', 'Master 2 GDIL'
FROM utilisateurs u
WHERE u.email = 'fatou.demo@local.test'
  AND NOT EXISTS (SELECT 1 FROM etudiant e WHERE e.id = u.id);

-- Sujets de demonstration
INSERT INTO sujet (titre, description, capacite_max, disponible, date_creation, enseignant_id)
SELECT 'Application de suivi des stages',
       'Concevoir une application web pour suivre les stages, les conventions et les rapports.',
       2, TRUE, CURDATE(), u.id
FROM utilisateurs u
WHERE u.email = 'enseignant.demo@local.test'
  AND NOT EXISTS (
      SELECT 1 FROM sujet s
      WHERE s.titre = 'Application de suivi des stages' AND s.enseignant_id = u.id
  );

INSERT INTO sujet (titre, description, capacite_max, disponible, date_creation, enseignant_id)
SELECT 'Tableau de bord des projets tutorés',
       'Réaliser un tableau de bord pour suivre les sujets, les candidatures et les affectations.',
       1, FALSE, CURDATE(), u.id
FROM utilisateurs u
WHERE u.email = 'enseignant2.demo@local.test'
  AND NOT EXISTS (
      SELECT 1 FROM sujet s
      WHERE s.titre = 'Tableau de bord des projets tutorés' AND s.enseignant_id = u.id
  );

-- Candidatures : une en attente, une acceptee et une affectation a capacite atteinte.
INSERT INTO candidature (motivation, date_candidature, date_decision, statut, etudiant_id, sujet_id, responsable_id)
SELECT 'Je souhaite travailler sur une application utile aux étudiants.', CURDATE(), NULL,
       'EN_ATTENTE', etu.id, s.id, NULL
FROM utilisateurs etu
JOIN utilisateurs ens ON ens.email = 'enseignant.demo@local.test'
JOIN sujet s ON s.titre = 'Application de suivi des stages' AND s.enseignant_id = ens.id
WHERE etu.email = 'aminata.demo@local.test'
  AND NOT EXISTS (SELECT 1 FROM candidature c WHERE c.etudiant_id = etu.id AND c.sujet_id = s.id);

INSERT INTO candidature (motivation, date_candidature, date_decision, statut, etudiant_id, sujet_id, responsable_id)
SELECT 'Ce sujet correspond à mon intérêt pour le développement web.', CURDATE(), CURDATE(),
       'ACCEPTEE', etu.id, s.id,
       (SELECT r.id FROM utilisateurs r WHERE r.email = 'responsable@local.test' LIMIT 1)
FROM utilisateurs etu
JOIN utilisateurs ens ON ens.email = 'enseignant.demo@local.test'
JOIN sujet s ON s.titre = 'Application de suivi des stages' AND s.enseignant_id = ens.id
WHERE etu.email = 'mamadou.demo@local.test'
  AND NOT EXISTS (SELECT 1 FROM candidature c WHERE c.etudiant_id = etu.id AND c.sujet_id = s.id);

INSERT INTO candidature (motivation, date_candidature, date_decision, statut, etudiant_id, sujet_id, responsable_id)
SELECT 'Je suis intéressée par la visualisation des données de projet.', CURDATE(), CURDATE(),
       'ACCEPTEE', etu.id, s.id,
       (SELECT r.id FROM utilisateurs r WHERE r.email = 'responsable@local.test' LIMIT 1)
FROM utilisateurs etu
JOIN utilisateurs ens ON ens.email = 'enseignant2.demo@local.test'
JOIN sujet s ON s.titre = 'Tableau de bord des projets tutorés' AND s.enseignant_id = ens.id
WHERE etu.email = 'fatou.demo@local.test'
  AND NOT EXISTS (SELECT 1 FROM candidature c WHERE c.etudiant_id = etu.id AND c.sujet_id = s.id);

COMMIT;

-- Comptes crees :
-- enseignant.demo@local.test / Demo123!
-- enseignant2.demo@local.test / Demo123!
-- aminata.demo@local.test / Demo123!
-- mamadou.demo@local.test / Demo123!
-- fatou.demo@local.test / Demo123!
