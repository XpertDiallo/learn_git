# TextAura Web

Application web progressive, privée et hors ligne pour préparer des publications stylisées sur les réseaux sociaux.

## Fonctions

- Français, anglais, espagnol, allemand et arabe avec RTL.
- Styles Unicode, casse, conventions de nommage et nettoyage.
- Couleurs, surlignage, soulignement, alignements et justification.
- Listes numériques, alphabétiques, romaines et symboliques.
- Bibliothèque d’emojis en neuf catégories avec signalement des emojis utilisés.
- Sélection persistante sur mobile, historique Annuler/Rétablir de 100 étapes.
- Copie, partage natif et export PNG.
- Brouillon local, PWA installable et cache hors ligne.

## Sécurité et confidentialité

Aucun serveur applicatif, compte, cookie, analytics ou appel réseau de données. Le contenu utilisateur est rendu avec `textContent`, jamais avec `innerHTML`. Une CSP restrictive bloque les connexions, objets, formulaires et contenus tiers. Les données restent dans `localStorage` sur l’appareil.

## Vérification

```bash
npm test
npm run check
npm run serve
```

Ouvrir ensuite `http://localhost:4173`.

## Déploiement Lovable

Importer ce dépôt et la branche `textaura-web` dans Lovable, puis choisir le dossier `textaura-web` comme racine du projet. L’application est statique et ne nécessite ni variable secrète ni base de données.
