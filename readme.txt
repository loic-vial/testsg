Fonctionnement : 
 - partie de Quentin : 
   - comment on utilise les contextes et quel est l'utilite
   - comment on enchaine plusieurs TU
   - comment marche la journalisation, etc
 - partie d'Arnaud : 
   - comment on enregistre/joue des evenements en local
   - comment on l'integre a une application
   - comment on l'integre a un test unitaire
 - partie de Loic : 
   - comment on gere plusieurs clients en meme temps


Modifs de tests : 
 - initialisation explicite des attributs + RAZ au End()
 - suppression de tous les System.exit ==> remplacement par des RuntimeException
   -> ca empechait d'en enchainer
 - Traitement
 - compteur de TU

Conception : 
 - modifications de Tests (prise en charge du contexte, jouer plusieurs TU, compteur de TU, comparer une valeur a un tableau de valeurs...)
   Quentin Salmeron
 - enregistrement/jeu d'evenements sur une machine locale
   Arnaud Le Bourblanc
 - gestion de plusieurs clients via le reseau
   Loic Vial
   
- parler du choix de pourquoi on utilise pas NoeudG (encombrant (au moins 15 classes lolz), pas obligatoire)
- faire la liste de tous les messages qu'on envoit/recoit a travers le reseau
- montrer toutes les facons de creer un contexte : 
  - la classe anonyme
  - la classe interne a un test unitaire
  - la classe externe
- faire la liste de tous les evenements qu'on enregistre et qu'on peut rejouer
