# Application-multi-tiers
Une université souhaite disposer d’une application web pour gérer les sujets de projets  tutorés, les candidatures des étudiants et les décisions d’affectation. Le projet doit  démontrer une séparation rigoureuse entre présentation, logique métier et persistance des  données. 

## Compte administrateur local

Au premier démarrage, l’application crée un compte administrateur s’il n’existe pas déjà dans la base configurée : `admin@local.test` avec le mot de passe `Admin123!`. Ces valeurs peuvent être remplacées avec les variables d’environnement `ADMIN_EMAIL` et `ADMIN_PASSWORD`. Changez-les avant tout déploiement et ne réutilisez pas ces identifiants en production.

## Frontend Angular

### Premier lancement

Prérequis : Java 21, Node.js 20.11 ou supérieur, npm et un serveur MySQL actif sur le port 3306.

1. Vérifiez les identifiants MySQL dans `backend/src/main/resources/application.properties`. Par défaut, l’utilisateur est `root` sans mot de passe. La base `gestionDeProjetBD` est créée automatiquement au démarrage du backend si elle n’existe pas et si l’utilisateur MySQL a les droits nécessaires.
2. Dans un premier terminal, depuis la racine du dépôt :

   ```powershell
   cd backend
   .\gradlew.bat bootRun
   ```

   Le backend écoute sur `http://localhost:8089`.
3. Dans un second terminal, depuis la racine du dépôt :

   ```powershell
   cd frontend
   npm install
   npm start
   ```

4. Ouvrez `http://localhost:4200`. Le proxy Angular transmet les requêtes `/api` au backend sur le port 8089.

Au premier démarrage, l’application crée les comptes `admin@local.test` / `Admin123!` et `responsable@local.test` / `Resp123!`. Connectez-vous comme administrateur pour créer les comptes enseignants et étudiants.

Le script `database/creation_tables.sql` n’est pas requis pour ce lancement : il cible la base `gestion_sujets`, différente de celle configurée dans le backend, et commence par supprimer des tables si elles existent. Les instructions Angular détaillées sont dans [frontend/README.md](frontend/README.md).
