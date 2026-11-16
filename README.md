# To-Do App with Spring Boot

![Java CI with Maven](https://github.com/Behlul5253/todo-app-springboot/actions/workflows/ci.yml/badge.svg)
![AWS Deployment](https://github.com/Behlul5253/todo-app-springboot/actions/workflows/aws-deploy.yml/badge.svg)

A full-stack task and category management application built with Spring Boot, Spring Security, JPA, and Thymeleaf, deployed on AWS Elastic Beanstalk.

---

## 🚀 Deployment Pipeline & Architecture
* **Cloud Platform:** AWS Elastic Beanstalk (Java Runtime Environment).
* **Automation:** Managed via GitHub Actions (`aws-deploy.yml`), which builds the executable JAR via Maven and pushes updates on every push to `main`.
* **Environment Variables & Secrets:** Securely handled using GitHub Repository Secrets (`AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY`).

---

## 🛠️ Git Workflow & Collaboration
This project follows strict version control and collaboration standards:
* **Branching Strategy:** 
  * `main`: Production-ready, stable code branch.
  * Feature branches are used for developing specific roadmap modules before merging via Pull Requests.
* **Pull Requests (PRs):** All core features are merged into `main` following peer or self-review via GitHub PRs, ensuring automated CI pipeline checks pass successfully.
* **Commit Conventions:** Follows conventional commits (e.g., `feat: add user registration`, `fix: resolve entity mapping`, `ci: add GitHub Actions workflow`).