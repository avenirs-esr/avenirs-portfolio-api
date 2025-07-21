# 📦 Changelog

This file tracks all notable changes to this repository, following
the [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) format
and [Conventional Commits](https://www.conventionalcommits.org/) standard.

---
## [v1.3.1] - 2025-07-21

- 🛠️ **Introduction of `AvenirsBaseModel`**
  - Added a new abstract class `AvenirsBaseModel` to centralize common behavior across domain objects.
  - Provides a shared implementation of:
    - `id` property management.
    - `equals` and `hashCode` methods based on `id`.
    - A consistent `toString` format for all domain models.
  - This change improves code consistency and reduces duplication across the domain layer.

## [v1.3.0] - 2025-07-18

- 🔄 **Refactored ProgramProgress / SkillLevel**
  - Introduced `StudentProgress` as the main entity representing a student’s progress within a `TrainingPath`.
  - Introduced `SkillLevelProgress` to track a student’s progress on a specific `SkillLevel`.
  - Redesigned the relationships between `ProgramProgress`, `StudentProgress`, and `SkillLevelProgress` for improved data consistency and clarity.

- ⚠️ **Breaking Change**
  - This version requires a **full database reset**.
  - Run the following command from `srv-dev`:
    ```bash
    npm run api:reset-db
    ```

## [v1.2.0] - 2025-07-08

- 🛠️ **Added Liquibase Changelog Generation Feature**
    - Introduced the capability to generate and apply Liquibase changelog files.
    - Use `npm run api:reset-db` to initialize the database with all changelogs applied.
    - Generate new changelog files with `npm run api:generate-changelog`.
    - Apply new changelogs either by resetting the database or updating it with `mvn liquibase:update`.
    - **Note:** Always review generated changelog files for accuracy and completeness.

## [v1.1.1] - 2025-07-03

- ✨ Refactor Seeder System
- 🛠️ Introduced `SeederConfig` to centralize and group all seeding constants
- 📦 Split seeders: one seeder per entity
- 🔄 Fake entities now depend solely on persistence (JPA) entities instead of domain classes


## [v1.1.0] - 2025-06-30

- Add CORS configuration.

## [v1.0.4] - 2025-06-30

- Added language management using a RequestContext.

## [v1.0.3] - 2025-06-26

- Fix missing programProgressId in /me/ams/view/ end point.
- Fix incorrect handling of pagination default values

## [v1.0.2] - 2025-06-20

- /me/ams/view/ end point.
- Start of refactoring for the seeding process.

## [v1.0.1] - 2025-06-11

🏁 Initial version : this marks the beginning of the changelog tracking for the project.

- Exclusion routes for SpringSecurity based on a property.
- Changelog added.
- Unit tests for the package security.
