# Frontend Angular — Gestion des projets tutorés

Interface web pour les étudiants, enseignants, responsables pédagogiques et administrateurs.

## Prérequis

- Node.js 20.11 ou supérieur
- npm
- Le backend Spring Boot démarré sur `http://localhost:8089` (port configuré dans `backend/src/main/resources/application.properties`)

## Installation et lancement

Depuis le dossier `frontend/` :

```bash
npm install
npm start
```

Ouvrir ensuite `http://localhost:4200`. Pour produire la version de déploiement : `npm run build`.

En développement, `src/proxy.conf.json` transmet les appels `/api` vers le backend `http://localhost:8089`. Ce proxy évite les blocages CORS du navigateur. Si le backend change de port, mettez à jour le fichier proxy. En production, configurez le serveur web pour transmettre `/api` à la même adresse que le backend.

## Fonctions par profil

- **Administrateur** : création, modification, recherche et suppression des comptes étudiants et enseignants ; consultation des projets.
- **Enseignant** : gestion de ses sujets et consultation des candidatures par sujet.
- **Étudiant** : consultation des sujets disponibles, dépôt d’une candidature et suivi de son statut.
- **Responsable pédagogique** : consultation et filtrage des candidatures, décisions d’acceptation ou de rejet, consultation des affectations.

Les formulaires utilisent la validation réactive. Les listes offrent recherche, tri et pagination. Le token de connexion est ajouté automatiquement aux appels API ; les droits sont vérifiés côté serveur par Spring Security.

## Organisation

- `src/app/auth/` : écran de connexion.
- `src/app/portal/` : espace utilisateur et écrans métier par rôle.
- `src/app/core/` : authentification, guards, intercepteur, modèles et services HTTP par ressource.
- `src/environments/` : configuration de l’URL API.

Le frontend communique uniquement avec les API REST. Il ne se connecte pas directement à la base de données.
