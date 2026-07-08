# Module 1 — Gestion des Produits / Stock

Responsable : **MEJBER Ahmed Amine**
Stack : Spring Boot 3 + Spring Data JPA (MySQL, schema.sql commun)

## 1. Contenu du module

```
com.usine.saas
├── entity/          Usine (partagée), Produit
├── repository/      ProduitRepository, UsineRepository (Spring Data JPA)
├── service/         ProduitService (logique métier), HistoriqueService (contrat)
├── security/        UserContext (contrat)
├── controller/      ProduitController (API REST), ProduitViewController (pages Thymeleaf)
├── dto/             ProduitRequestDTO, ProduitResponseDTO, DashboardDTO
└── exception/       Exceptions métier + GlobalExceptionHandler
```

## 2. Pourquoi Thymeleaf et pas PrimeFaces ?

Le cahier des charges d'origine prévoyait des pages PrimeFaces (JSF, `.xhtml`).
L'équipe ayant choisi **Spring Boot + Spring Data JPA**, JSF/PrimeFaces ne s'intègre pas
nativement (il faudrait ajouter un conteneur JSF comme MyFaces via JoinFaces, ce qui
complique beaucoup le projet pour un gain limité). Les pages ont donc été refaites en
**Thymeleaf + Bootstrap + Chart.js**, en gardant exactement le même découpage fonctionnel
demandé : `produits/liste`, formulaire d'ajout/modification (en modal), `produits/dashboard`.

**À discuter en équipe** : si les modules Commandes et Auth utilisent aussi Spring Boot,
gardez Thymeleaf partout pour rester cohérents.

## 3. Contrats d'interface avec les autres modules

### 3.1 Avec le module Commandes (membre 2)

Le module Commandes doit appeler ces méthodes de `ProduitService` lors de la création,
validation ou annulation d'une commande :

```java
void decrementerStock(Integer produitId, Integer quantite);
// Lève StockInsuffisantException si le stock est insuffisant.
// Lève ProduitNonTrouveException si le produit n'existe pas.

void incrementerStock(Integer produitId, Integer quantite);
// À utiliser lors de l'annulation d'une commande déjà validée, pour remettre le stock.
```

Ces deux méthodes sont aussi exposées en HTTP interne :
- `POST /api/produits/{id}/decrementer?quantite=N`
- `POST /api/produits/{id}/incrementer?quantite=N`

⚠️ Ces endpoints ne sont pas encore sécurisés pour un usage service-à-service : à protéger
(clé API interne, réseau interne au cluster, etc.) avant la mise en production.

### 3.2 Avec le module Auth/Users (membre 3)

Ce module attend une implémentation Spring du bean **`UserContext`**
(package `com.usine.saas.security`) :

```java
public interface UserContext {
    Integer getUserId();
    Integer getUsineId();
    boolean isAdmin();
    boolean peutAjouterProduit();
    boolean peutModifierProduit();
    boolean peutSupprimerProduit();
}
```

En attendant, une implémentation **stub** (`UserContextStub`, profil `dev-standalone`)
simule un administrateur de l'usine 1 avec tous les droits, pour pouvoir développer et
tester ce module de façon autonome.

### 3.3 Avec le module Auth/Logs (membre 3)

Ce module attend une implémentation Spring du bean **`HistoriqueService`**
(package `com.usine.saas.service`), persistant dans les tables `historique` et
`historique_stockage` :

```java
public interface HistoriqueService {
    void logStock(Integer usineId, Integer userId, Integer produitId,
                   Integer quantiteAvant, Integer quantiteApres);
    void log(Integer usineId, Integer userId, String description, String typeAction);
}
```

Un stub (`HistoriqueServiceStub`, profil `dev-standalone`) se contente de logger dans la
console.

**⚠️ Action requise avant l'intégration finale** : supprimer les stubs (ou désactiver le
profil `dev-standalone`) et retirer le `@SpringBootApplication` de ce module au profit de
la classe principale unique de l'application fusionnée.

## 4. Endpoints REST exposés

| Méthode | URL                                   | Description                              | Droit requis           |
|---------|----------------------------------------|-------------------------------------------|-------------------------|
| GET     | `/api/produits`                        | Liste des produits de l'usine courante    | -                        |
| GET     | `/api/produits/recherche?nom=...`      | Recherche paginée par nom                 | -                        |
| GET     | `/api/produits/{id}`                   | Détail d'un produit                       | -                        |
| GET     | `/api/produits/dashboard?seuilRupture=`| KPI stock (quantité totale, valeur, rupture) | -                     |
| POST    | `/api/produits`                        | Créer un produit                          | `peut_ajouter_produit`  |
| PUT     | `/api/produits/{id}`                   | Modifier un produit                       | `peut_modifier_produit` |
| DELETE  | `/api/produits/{id}`                   | Supprimer un produit                      | `peut_supprimer_produit`|
| POST    | `/api/produits/{id}/decrementer`       | Décrémente le stock (usage interne)       | -                        |
| POST    | `/api/produits/{id}/incrementer`       | Incrémente le stock (usage interne)       | -                        |

`is_admin = true` outrepasse toujours les permissions spécifiques (comme demandé dans le
cahier des charges).

## 5. Règles métier importantes

- **Isolation multi-tenant stricte** : toutes les requêtes filtrent par `usine_id`. Un
  produit d'une autre usine renvoie une `ProduitNonTrouveException` (404), jamais un 403,
  pour ne pas révéler l'existence de données d'un autre tenant.
- **Quantité jamais négative** : validée côté DTO (`@PositiveOrZero`) et recontrôlée dans
  `decrementerStock` (lève `StockInsuffisantException` si la quantité demandée dépasse le
  stock disponible).
- **BigDecimal partout** pour les montants, jamais `double`.
- **Suppression** : actuellement un `DELETE` classique. Si le produit est référencé par une
  commande (contrainte `fk_commande_produit`), une `ProduitReferenceParCommandeException`
  (409) est levée. **Point ouvert à discuter en équipe** : ajouter une colonne `actif` à la
  table `produit` pour un vrai soft-delete si ce cas devient fréquent (nécessite une
  modification du schema.sql commun, à valider avec tout le monde avant de le faire).

## 6. Comment lancer/tester le module seul

```bash
cd module-produit
mvn spring-boot:run
```

Le profil `dev-standalone` est actif par défaut (`application.yml`) : il simule un
administrateur pour pouvoir tester sans le module Auth. Pages disponibles :
- `http://localhost:8080/produits` (liste + CRUD)
- `http://localhost:8080/produits/dashboard`

Tests unitaires :
```bash
mvn test
```

## 7. Livrables de ce module

- ✅ Entité, DAO (repository), service, contrôleurs REST + MVC, pages Thymeleaf
- ✅ Tests JUnit 5 / Mockito pour `ProduitService` (`src/test/...`)
- ✅ Ce README
- ✅ Diagramme de séquence PlantUML (`diagramme_sequence_ajout_produit.puml`)
