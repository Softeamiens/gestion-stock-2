# Gestion de stock d'entrepôt
Langage : Java · Framework : Spring Boot · Difficulté : intermediate · Durée estimée : 180 min
 
# Contexte
Vous êtes chargé de développer le backend d'un système de gestion de stock pour un entrepôt logistique. L'entrepôt stocke des produits identifiés par référence, avec des quantités et emplacements. Les opérations quotidiennes incluent l'ajout de produits, les mouvements de stock (entrées/sorties) et la consultation des niveaux. Le système doit garantir l'intégrité des données et alerter en cas de stock insuffisant.
 
Objectifs pédagogiques
Concevoir une API REST respectant les conventions RESTful
Implémenter la persistance avec JPA/Hibernate et gérer les relations
Appliquer la validation des données avec Bean Validation
Écrire des tests unitaires avec JUnit et Mockito
Gérer les erreurs métier et techniques avec des réponses HTTP appropriées
Fonctionnalités à implémenter
Créer un produit avec référence unique, nom, description, quantité initiale et emplacement
Lister tous les produits avec pagination et filtrage par emplacement
Consulter le détail d'un produit par sa référence
Enregistrer un mouvement de stock (entrée ou sortie) avec quantité et motif
Empêcher les sorties qui rendraient la quantité négative
Calculer et exposer le stock disponible pour chaque produit
Retourner une erreur 404 si le produit n'existe pas lors d'une opération

Contraintes techniques
Utiliser Spring Boot 3.x avec Java 17 minimum
Persister les données avec Spring Data JPA et une base H2 en mémoire
Valider les entrées avec les annotations Jakarta Bean Validation
Respecter les codes HTTP standards (200, 201, 400, 404, 409)
Interdire l'usage de @Transactional dans les contrôleurs
Critères d'acceptation
Toutes les routes API répondent avec le bon code HTTP et format JSON
Les contraintes de validation rejettent les données invalides avec un message explicite
Impossible de créer deux produits avec la même référence
Les mouvements de stock mettent à jour correctement la quantité du produit
Les tests unitaires couvrent les services et contrôleurs avec au moins 80 % de couverture
L'application démarre sans erreur et expose une documentation Swagger/OpenAPI
 

Grille d'évaluation
Critère	Poids	Détail
Architecture & conventions REST	25 %	Structure en couches (controller/service/repository), nommage des endpoints, verbes HTTP et codes de statut
Persistance & modèle de données	25 %	Entités JPA mappées, relations gérées, requêtes optimisées, transactions
Validation & gestion des erreurs	20 %	Annotations de validation, exceptions métier capturées, messages clairs
Tests unitaires	20 %	Cas nominaux et d'erreur, mocks, assertions pertinentes, indépendance
Qualité du code	10 %	Lisibilité, conventions Java, pas de duplication, documentation minimale
 
Pistes de démarrage
Commencer par modéliser les entités Produit et MouvementStock
Utiliser @RestControllerAdvice pour centraliser la gestion des exceptions
Penser à indexer la colonne référence pour les performances
 
Bonus (optionnel)
Endpoint de recherche par nom de produit (correspondance partielle)
Historique complet des mouvements par produit
Rapport de stock bas (produits sous un seuil configurable)