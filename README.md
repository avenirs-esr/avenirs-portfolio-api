# avenirs-portfolio-api

## 🚀 Prérequis

- **Java** 25
- **Maven** 4.0.0
- **PostgreSQL**
- **Docker** pour lancer la BDD en conteneur

---

## 🏗️ Installation

Clone le projet :

```bash
git clone https://github.com/avenirs-esr/avenirs-portfolio-api.git
cd avenirs-portfolio-api
```

---

## 🛠️ Lancer l’application

### 🚀 Mode développement

```bash
mvn spring-boot:run
```

L’API sera accessible sur : [http://localhost:10000](http://localhost:10000)

---

## 📦 Génération et application des changelogs Liquibase

Pour initialiser la base de données avec tous les changelogs à jour, utilise la commande suivante **à partir du
dossier `srv-dev`** :

**Commande :**

```bash
npm run api:reset-db
```

Cette commande :

- Supprime la base de données existante
- Recrée la base, les rôles et les schémas
- Applique **tous les changelogs Liquibase** pour créer les tables

---

### 🛠️ Générer un nouveau changelog

Toujours depuis `srv-dev`, exécute :

```bash
npm run api:generate-changelog
```

Un fichier sera généré dans :  
`src/main/resources/db/changelog/generated/`

**⚠️ Attention** :

- Même si aucun changement n’est détecté, **un fichier vide est quand même généré** → pense à le **supprimer
  manuellement**
- Liquibase ne détecte **pas tous les changements** (nullable, renommages, contraintes, etc.)  
  → Toujours **relire le changelog généré** et corriger manuellement si besoin.

---

### ✅ Appliquer les nouveaux changelogs

Deux options s’offrent à toi :

- **Réinitialiser complètement** la base : `npm run api:reset-db`

- **Appliquer les différences** sans supprimer les données : `mvn liquibase:update` _(à lancer depuis l’API)_

---

## 🧪 Lancer les tests

```bash
 mvn clean compile javadoc:javadoc verify
```

Les tests unitaires, d’intégration et les rapports de couverture sont générés.

---

## 🌱 Remplir la base avec des données de test

Pour initialiser la base avec des **données fictives** (faker), active le seeder:

1️⃣ Dans ton fichier `env.properties`:

```properties
seeder.enabled=true
```

2️⃣ Relance l’application:

```bash
mvn spring-boot:run
```

👉 Le `SeederRunner` créera les utilisateurs, compétences, programmes, etc.

✅ Les logs afficheront:

```
✓ 10 users created
✓ 3 institutions created
✓ 1 trace created
✓ 1 ams created
Seeding successfully finished
```

---

## 🧹 Code Formatting – Google Java Format (via Spotless)

This project enforces code formatting using [google-java-format](https://github.com/google/google-java-format) via
the [Spotless Maven plugin](https://github.com/diffplug/spotless).

---

### ✅ Check code formatting

Before committing or pushing code, you can verify that your Java files follow the required format:

```bash
mvn spotless:check
```

### ✨ Format code

If you find any files that are not formatted correctly, you can automatically format them using:

```bash
mvn spotless:apply
```

---

## 📚 Documentation API

L’API est documentée via **Swagger** :

👉 [http://localhost:10000/avenirs-portfolio-api/swagger-ui](http://localhost:10000/avenirs-portfolio-api/swagger-ui) (en
local)

---

## 🔍 Surveillance et santé

Le projet inclut **Spring Boot Actuator**. Pour accéder aux endpoints:

👉 [http://localhost:10000/actuator](http://localhost:10000/actuator)

---

## ⚡ CI/CD et sécurité

Le projet inclut:

✅ Lint automatique  
✅ Couverture de tests et rapports  
✅ Analyse de sécurité (Trivy)  
✅ Publication automatique des rapports sur **GitHub Pages**
