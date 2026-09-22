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

- [ ] Décider d'une licence de distribution (actuellement `All Rights Reserved` par défaut dans `gradle.properties`, choix à confirmer si publication prévue).
- [ ] Publier une première version (CurseForge/Modrinth) si l'intention est de distribuer le mod au-delà d'un usage personnel/serveur privé.

## Notes

- Pas de tests unitaires dans le projet ; la CI (`.github/workflows/build.yml`) ne fait que compiler.
- `TEMPLATE_LICENSE.txt` est la licence MIT du template NeoForge MDK lui-même, pas celle du mod — normal, à laisser tel quel.
