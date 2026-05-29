# Vent - College Event Management App

![Android API](https://img.shields.io/badge/Android-9.0%2B-brightgreen.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-Native-blue.svg)
![Node.js](https://img.shields.io/badge/Node.js-Backend-success.svg)
![Docker](https://img.shields.io/badge/Docker-Containerized-blue)

Vent is a dedicated event management application developed specifically for **Vivek College of Commerce**. It streamlines the process for teachers and faculty to record, manage, and distribute event details seamlessly from their Android devices.

## 📖 Background & Journey
This project began as a teacher's request during my B.Sc.I.T. 2nd year. After initial prototyping with Cordova and Flutter, I transitioned to native Android development to ensure optimal performance and integration. Developed entirely solo over a two-year period, Vent represents a complete full-stack solution tailored for internal college administration.

## ✨ Key Features
* **Role-Based Access Control (RBAC):** Tailored permissions based on user roles:
  * **Admins:** Full control to approve new user registrations, assign user roles, and manage (view/add) all events.
  * **Teachers:** Authorized to create, submit, and view events.
  * **Students:** Read-only access to browse and view events.
* **User Approval Workflow:** To maintain a secure internal ecosystem, newly registered accounts are placed in a waiting queue and cannot access the app until explicitly approved and assigned a role by an Admin.
* **Universal PDF Export:** All approved users (Admins, Teachers, and Students) can download event details directly to their local device as formatted PDF reports.
* **Event Discovery:** Search and filter through the database of recorded events.
* **Secure Access:** Backend API is secured via JWT (JSON Web Tokens) authentication.

## 🛠️ Tech Stack & Architecture

### Frontend (Android App)
* **Languages:** Kotlin, Java, XML
* **UI toolkit:** Jetpack Compose & traditional XML layouts
* **Compatibility:** Android 9.0 (API Level 28) and onwards.

### Backend (Server)
* **Environment:** Node.js
* **Database:** MariaDB (SQL) via Docker
* **Architecture:** The backend utilizes a secure Docker bridge network (`vent-network`). The MariaDB database container is completely isolated from the host machine and is only accessible internally by the Node.js server container, ensuring robust internal security.

---

## 📸 Screenshots & Previews

*(Add your screenshots or GIFs here)*

| Home Screen | Event Search | PDF Export |
| :---: | :---: | :---: |
| <img width="250" alt="Home Screen Image" src="https://github.com/user-attachments/assets/7d26df8e-3449-4812-9516-d8eb685d1c27" /> | <img width="250" alt="Event Search Image" src="https://github.com/user-attachments/assets/ecae127d-9510-47d4-9538-7f962b6c78d5" /> | <img src="https://github.com/user-attachments/assets/5c2d9bc8-334d-4dcc-a2f3-e69ec99d7e88" width="250" alt="Exported PDF Image" /> |

---

## 🚀 Getting Started

The repository is split into two main directories: `/app` (Android frontend) and `/server` (Node.js backend).

### Prerequisites
* **Android Studio** (for running the `/app`)
* **Docker** (for running the MariaDB database and Node.js server)
* **Code Editor** (VS Code, Zed, Neovim, etc.)

**⚠️ IMPORTANT HANDOFF NOTE FOR MAINTAINERS ⚠️** 
Because this is an internal application, the public GitHub repository does **not** contain the necessary sensitive files to run the backend. To successfully run this project locally, you must request a separate package from the original author containing the following three files:
1. `Dockerfile` (for containerizing the Node.js server)
2. `.env` (contains the JWT secret, `DB_HOST=vent-db-container`, and database credentials)
3. `.sql` (the database dump to populate MariaDB)

### 1. Network & Database Setup (Docker)
First, create the shared internal network so the containers can communicate securely:
```bash
docker network create vent-network

```

Start the isolated MariaDB container (replace placeholders with credentials from your `.env` file). *Note: Port 3306 is not published to the host machine for security purposes.*

```bash
docker run --name vent-db-container --network vent-network -e MYSQL_ROOT_PASSWORD=<your_db_password> -e MYSQL_DATABASE=<your_db_name> -d mariadb:latest

```

Import the database tables from the provided `.sql` file into your running container:

```bash
docker exec -i vent-db-container mysql -u root -p<your_db_password> <your_db_name> < path/to/your/database_dump.sql

```

### 2. Backend Server Setup (/server)

1. Navigate to the server directory:

```bash
   cd server

```

2. Place the provided `.env` and `Dockerfile` into this directory.
3. Build the server image:

```bash
   docker build -t vent-app-backend:latest .

```

4. Run the server container on the shared network and expose port 3000 to the host:

```bash
   docker run --name vent-server-container --network vent-network -p 3000:3000 -d vent-app-backend:latest

```

### 3. Android App Setup (/app)

1. Open **Android Studio**.
2. Select **File > Open** and navigate to the cloned repository. Select the `app` directory.
3. Let Gradle sync the project dependencies.
4. Ensure your base URL in the app's network configuration points to your running Node.js server (`http://<your_machine_ip>:3000`).
5. Build and run on an emulator or physical device running Android 9+.

## 🤝 Contributing & Forking

While Vent was built as an internal project for Vivek College of Commerce, forks and usage of this codebase are highly encouraged! Feel free to fork the repository, explore the architecture, and adapt the code for your own event management needs.

## 👨‍💻 Author

**Deril Kurian Chundakal**
