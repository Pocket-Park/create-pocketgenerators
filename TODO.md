# Roadmap

État au 2026-09-22. Le mod compile et produit un jar fonctionnel (`./gradlew build` passe hors ligne), les 5 tiers sont implémentés côté code, recettes et données. Ce qui suit n'a pas de confirmation qu'il a été vérifié en jeu.

## À faire avant une première version jouable

- [ ] **Test en jeu complet** (`./gradlew runClient`) : placer chaque tier, vérifier la cascade de recettes Basic→Brass, vérifier que Netherite et End sont bien des silos (pas de cascade croisée), vérifier le cadran de batch (tier Brass), la clé à molette (cycle des 6 faces), la poussée vers belt/inventaire.
- [ ] Vérifier le rendu (item posé sur le dessus + arbre tournant) sur les 5 tiers, y compris en orientation X/Y/Z.
- [ ] Vérifier l'intégration JEI (catégorie affichée, tier requis lisible) avec JEI présent, et confirmer l'absence de crash sans JEI.

## Amélioration / polish

- [ ] Textures custom pour les 5 tiers (actuellement réutilisation de textures Create/vanilla — fonctionnel mais peu distinctif visuellement).
- [ ] Scène Ponder (dépendance déjà présente dans `build.gradle`, jamais utilisée dans le code — le tutoriel intégré à Create n'a aucune entrée pour ce mod).
- [ ] Icônes/description pour une éventuelle fiche CurseForge/Modrinth.

## Publication

- [x] Décider d'une licence de distribution — MIT retenu (`LICENSE`, `gradle.properties: mod_license=MIT`, mentionné dans le README).
- [x] Structure de branches `main`/`dev` + workflows de release/snapshot mis en place (`.github/workflows/release.yml`, `.github/workflows/snapshot.yml`).
- [x] Créer le projet sur Modrinth et récupérer son slug/ID.
- [x] Créer un token API Modrinth et ajouter les secrets GitHub `MODRINTH_TOKEN` / `MODRINTH_PROJECT_ID`.
- [x] Pousser la branche `dev` sur `origin` (fait, `snapshot.yml` actif).
- [ ] **Premier tag `v0.1.0` (release) : bloqué volontairement** tant que le mod n'a pas été validé en jeu avec le groupe de joueurs. On reste sur des snapshots `dev` en attendant leur retour.

## Notes

- Pas de tests unitaires dans le projet ; `build.yml` ne fait que compiler sur push/PR.
- `release.yml`/`snapshot.yml` utilisent l'action `Kir-Antipov/mc-publish` — pas de plugin Gradle Modrinth, le jar buildé est publié tel quel.
- `TEMPLATE_LICENSE.txt` est la licence MIT du template NeoForge MDK lui-même, pas celle du mod — normal, à laisser tel quel.
