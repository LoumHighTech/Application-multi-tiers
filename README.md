# Application multi-tiers — Gestion des projets tutorés

Application web destinée à gérer les sujets de projets tutorés, les candidatures des étudiants et les décisions d’affectation. Le projet sépare la présentation Angular, la logique métier Spring Boot et la persistance MySQL.

## Prérequis

- Java 21
- Node.js 20.11 ou supérieur et npm
- MySQL actif sur le port 3306 (WAMP convient)

## Première mise en route

### 1. Démarrer MySQL

Dans WAMP, démarrez le service MySQL. Le backend crée la base `gestionDeProjetBD` au démarrage si elle n’existe pas et si l’utilisateur MySQL dispose des droits nécessaires. Vérifiez les identifiants de connexion dans `backend/src/main/resources/application.properties` (par défaut : utilisateur `root`, sans mot de passe).

### 2. Démarrer le backend

Depuis la racine du dépôt, dans un terminal :

```powershell
cd backend
.\gradlew.bat bootRun
```

Le backend écoute sur `http://localhost:8089`. Au premier démarrage, il crée les comptes `admin@local.test` / `Admin123!` et `responsable@local.test` / `Resp123!`, s’ils n’existent pas. Les identifiants administrateur peuvent être personnalisés avec `ADMIN_EMAIL` et `ADMIN_PASSWORD`. Changez les identifiants par défaut avant tout déploiement.

### 3. Démarrer le frontend

Dans un second terminal, depuis la racine du dépôt :

```powershell
cd frontend
npm install
npm start
```

Ouvrez `http://localhost:4200`. En développement, `frontend/src/proxy.conf.json` transmet les appels `/api` au backend sur le port 8089. Si le port du backend change, adaptez ce fichier. Pour créer la version de déploiement, lancez `npm run build` depuis `frontend/`.

## Comptes et données de démonstration

Le backend initialise les comptes administrateur et responsable indiqués ci-dessus. Les autres comptes peuvent être créés depuis l’application avec un compte administrateur.

Pour peupler la base avec des comptes, sujets et candidatures de démonstration :

1. Démarrez le backend au moins une fois afin que la base `gestionDeProjetBD` et ses tables soient créées.
2. Dans WAMP, ouvrez phpMyAdmin, sélectionnez `gestionDeProjetBD`, puis l’onglet **Importer**.
3. Importez `database/seed_demo.sql`.
4. Utilisez les identifiants indiqués à la fin du script. Le mot de passe des comptes de démonstration créés par ce script est `Demo123!`.

`seed_demo.sql` ajoute les données de démonstration sans supprimer les données existantes et peut être importé plusieurs fois. Il ne crée ni la base ni les tables.

Le fichier `database/creation_tables.sql` n’est pas requis pour le lancement décrit ici : il cible une autre base (`gestion_sujets`) et contient des commandes qui suppriment des tables existantes.

## Fonctionnalités par profil

- **Administrateur** : création, modification, recherche et suppression des comptes étudiants et enseignants; consultation des projets.
- **Enseignant** : gestion de ses sujets et consultation des candidatures reçues.
- **Étudiant** : consultation des sujets disponibles, dépôt d’une candidature et suivi de son statut.
- **Responsable pédagogique** : consultation et filtrage des candidatures, acceptation ou rejet, consultation des affectations.

Les listes proposent recherche, tri et pagination. Les formulaires valident les données saisies. Le frontend ajoute automatiquement le jeton de connexion aux appels API; les autorisations sont vérifiées par Spring Security. Le frontend communique avec l’API REST et ne se connecte pas directement à MySQL.

## Organisation du frontend

- `frontend/src/app/auth/` : écran de connexion.
- `frontend/src/app/portal/` : espace utilisateur et vues métier.
- `frontend/src/app/core/` : authentification, guards, intercepteur, modèles et services HTTP.
- `frontend/src/environments/` : configuration de l’URL API.
