# avenirs-portfolio-api

## 🧹 Code Formatting – Google Java Format (via Spotless)

This project enforces code formatting using [google-java-format](https://github.com/google/google-java-format) via the [Spotless Maven plugin](https://github.com/diffplug/spotless).

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