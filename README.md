# Système de Gestion d'Usine — Plateforme SaaS

> Projet universitaire — Application web de gestion industrielle multi-tenant
> Equipe de Projet: ENNAJAH Malek, SBAYI Douae, ELHOUDAIGUI Ilyas, MEJBER Ahmed Amine
---

## 📋 Description

Application SaaS de gestion d'usine permettant à chaque client (usine) de gérer de manière indépendante ses **utilisateurs**, son **stock de produits** et ses **commandes**, le tout avec un système complet de **traçabilité et de logs**.

---

## 🏭 Fonctionnalités principales

### 👥 Gestion des Utilisateurs & Rôles
- Création et gestion des comptes utilisateurs (nom, contact, email, mot de passe)
- Système de rôles granulaire par utilisateur :
  - Ajout / modification / annulation de commandes
  - Ajout / modification / suppression de produits
  - Accès administrateur
- L'administrateur dispose de tous les droits par défaut

### 📦 Gestion du Stock (Produits)
- Ajout, modification et suppression de produits
- Suivi des quantités, prix unitaires et descriptions
- Consultation des dashboards de ressources

### 🛒 Gestion des Commandes
- Création, modification et annulation de commandes
- Suivi de l'état des commandes et du prix total
- Consultation des dashboards de commandes

### 📜 Logs & Historique
- Historique complet de toutes les actions utilisateurs (avec horodatage)
- `Historique_Commandes` : traçabilité des commandes
- `Historique_Stockage` : traçabilité des mouvements de stock
- Accessible uniquement à l'administrateur

---

## 🔐 Acteurs du système

| Acteur | Accès |
|---|---|
| **Super Administrateur** | Gestion de la plateforme globale, ajout de services/plateformes |
| **Administrateur (Usine)** | Gestion des utilisateurs, rôles, commandes, produits, logs |
| **Utilisateur (Usine)** | Selon les rôles attribués |
| **Analyste de Sécurité** | Gestion des logs, déclaration d'intrusions, blocage d'IP |

---

## 🧱 Architecture & Déploiement SaaS

L'application est déployée en mode **multi-tenant** sur :

- **OpenShift** ou **Kubernetes** — orchestration des conteneurs, isolation par namespace par usine cliente

### ⚙️ Pipeline CI/CD

```
Code Push
   │
   ▼
Build (Docker)
   │
   ▼
Analyse Statique ──► SonarQube (qualité du code, bugs, code smells)
   │                ► SAST (analyse de vulnérabilités)
   │                ► Dependency Check (CVEs des dépendances)
   ▼
Tests Automatisés
   │
   ▼
Build Image & Push (Registry)
   │
   ▼
Déploiement (OpenShift / Kubernetes)
```

### 🛡️ Sécurité & Analyse de Code
- **SonarQube** — qualité et couverture de code
- **OWASP Dependency-Check** — vulnérabilités dans les dépendances
- **SAST** (ex: Semgrep, Trivy) — analyse statique de sécurité
- **Authentification** obligatoire pour l'accès plateforme et serveur

---

## 🗄️ Modèle de données (résumé)

| Entité | Champs clés |
|---|---|
| `User` | id, username, password, name, contact, email |
| `Roles` | permissions par action (Bool), lien user |
| `Produit` | id, nom, quantité, prix unitaire, description |
| `Commande` | id, produit_id, quantité, prix total, état |
| `Historique` | id, user_id, description, timestamp, date |
| `Historique_Commandes` | lien historique ↔ commande |
| `Historique_Stockage` | lien historique ↔ produit |

---

## 🛠️ Stack technique (envisagée)

- **Backend** : REST API (Spring Boot / Node.js / Django)
- **Frontend** : React / Angular
- **Base de données** : PostgreSQL / MySQL
- **Conteneurisation** : Docker
- **Orchestration** : OpenShift ou Kubernetes
- **CI/CD** : GitLab CI / GitHub Actions / Jenkins
- **Qualité & Sécurité** : SonarQube, OWASP, Semgrep/Trivy

---

*Projet réalisé dans le cadre du cursus universitaire — Département Informatique*
