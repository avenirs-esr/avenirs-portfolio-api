# 📦 Changelog

This file tracks all notable changes to this repository, following
the [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) format
and [Conventional Commits](https://www.conventionalcommits.org/) standard.

---

## [v1.3.5] - 2025-07-28

- ✨ **Added endpoint for uploading attachments to traces**
  - Introduced a feature to associate an attachment with an existing trace, in a two-step process: creating the trace and retrieving its ID, then uploading the associated file via a new dedicated endpoint.
  - Added a new `feature/file` folder containing abstract classes to facilitate future externalization of file upload handling into a dedicated microservice, reusable for both trace attachments and profile photos.
  - Defined the list of supported file types based on [MimeTypes.txt](https://github.com/GIP-RECIA/esup-publisher/blob/main/src/main/resources/MimeTypes.txt).
  - Configured upload size limit to 50MB via Spring, adjustable as needed.
  - Remaining points to address in future versions: duplicate attachment handling and automatic deletion of trace if attachment upload fails.

## [v1.3.4] - 2025-07-23

- 🛠️ **Enhancements to `StudentProgress`**
  - Added `LocalDate startDate` and `LocalDate endDate` fields to determine if a training is ongoing or completed.

- ✨ **Enhancement of `SkillDTO`**
  - Added a new field `boolean isProgramFinished` indicating whether the associated training program is finished.

- ✨ **New Endpoint `/me/skill-level-progress`**
  - Returns all `SkillLevelProgress` of the connected student for the **Life Project** tab.


## [v1.3.3] - 2025-07-23

- ✨ **Introduction of `PageCriteria`**
  - Added a new `PageCriteria` class to manage pagination defaults.
  - Services now require a `PageCriteria` object instead of separate `Integer page` and `Integer pageSize` parameters.
  - Provides better handling of default pagination values and ensures consistency across the API.

- ✨ **Enhancements to `SortCriteria`**
  - `SortCriteria` now includes built-in default values.
  - It can no longer be `null` and defaults to alphabetical ordering if no criteria is provided.

## [v1.3.2] - 2025-07-21

- ✨ **Enhancement of `SkillView` Payload**
  - Added a new optional field `lastAchievedSkillLevel` to the `SkillView` payload.
  - This field represents the **last skill level completed by the student**.
  - The field may be `null` if no skill level has been completed yet.
  
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
