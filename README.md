# Application-multi-tiers
Une université souhaite disposer d’une application web pour gérer les sujets de projets  tutorés, les candidatures des étudiants et les décisions d’affectation. Le projet doit  démontrer une séparation rigoureuse entre présentation, logique métier et persistance des  données. 

## Compte administrateur local

Au premier démarrage, l’application crée un compte administrateur s’il n’existe pas déjà dans la base configurée : `admin@local.test` avec le mot de passe `Admin123!`. Ces valeurs peuvent être remplacées avec les variables d’environnement `ADMIN_EMAIL` et `ADMIN_PASSWORD`. Changez-les avant tout déploiement et ne réutilisez pas ces identifiants en production.
