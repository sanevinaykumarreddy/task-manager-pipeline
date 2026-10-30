# Task Manager - DevOps CI/CD Pipeline Implementation

This project implements a complete **7-stage DevOps pipeline** as shown in the reference image:

```
Git / GitHub → Jenkins → Maven Build → JUnit Tests → SonarQube → Docker → Release
```

## Pipeline Overview

Each stage validates specific quality metrics. Failures halt progression to prevent faulty deployments.

### Stages

1. **Git / GitHub** - Store source code
2. **Jenkins** - Automate workflow
3. **Maven Build** - Compile & package
4. **JUnit Tests** - Verify functions
5. **SonarQube** - Analyze code quality
6. **Docker** - Containerize app
7. **Release** - Distribute version

## Project Structure

```
task-manager-pipeline/
├── src/
│   ├── main/
│   │   ├── java/com/taskmanager/
│   │   │   ├── Task.java
│   │   │   ├── TaskRepository.java
│   │   │   ├── TaskService.java
│   │   │   ├── TaskController.java
│   │   │   ├── HealthController.java
│   │   │   └── TaskManagerApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/taskmanager/
│           ├── TaskServiceTest.java
│           ├── TaskControllerTest.java
│           └── TaskManagerApplicationTest.java
├── pom.xml
├── Dockerfile
├── Jenkinsfile
├── sonar-project.properties
└── README.md
```

## Technology Stack

- **Language**: Java 17
- **Framework**: Spring Boot 3.2.0
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito
- **Database**: H2 (in-memory)
- **Containerization**: Docker
- **CI/CD**: Jenkins
- **Code Quality**: SonarQube
- **Code Coverage**: JaCoCo

## Application Features

- REST API for task management
- CRUD operations (Create, Read, Update, Delete)
- Filter tasks by status (PENDING, IN_PROGRESS, COMPLETED)
- Filter tasks by priority (LOW, MEDIUM, HIGH, URGENT)
- Health check endpoint at `/health`
- H2 Console for database inspection at `/h2-console`

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/tasks` | Get all tasks |
| GET | `/api/tasks/{id}` | Get task by ID |
| POST | `/api/tasks` | Create new task |
| PUT | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |
| GET | `/api/tasks/status/{status}` | Get tasks by status |
| GET | `/api/tasks/priority/{priority}` | Get tasks by priority |
| GET | `/health` | Health check |

## Local Development

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- Docker (optional)

### Build and Run

```bash
# Clone the repository
git clone <repository-url>
cd task-manager-pipeline

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Run tests
mvn test

# Generate code coverage report
mvn jacoco:report
```

The application will start on `http://localhost:8080`

### Build Docker Image

```bash
# Build the Docker image
docker build -t taskmanager-app:latest .

# Run the container
docker run -p 8080:8080 taskmanager-app:latest
```

## CI/CD Pipeline Setup

### 1. GitHub Repository Setup

```bash
# Initialize git repository
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit: Task Manager with CI/CD pipeline"

# Add remote
git remote add origin <your-github-repository-url>

# Push to main branch
git push -u origin main
```

### 2. Jenkins Setup

#### Prerequisites
- Jenkins installed and running
- Docker installed on Jenkins server
- SonarQube server running

#### Jenkins Configuration

1. **Install Required Plugins:**
   - Docker Pipeline
   - SonarQube Scanner for Jenkins
   - Git

2. **Configure Credentials:**
   - Navigate to: Manage Jenkins → Credentials
   - Add Docker Hub credentials (ID: `docker-hub-credentials`)
   - Add GitHub credentials (if needed)

3. **Configure SonarQube:**
   - Navigate to: Manage Jenkins → Configure System
   - Add SonarQube server (Name: `SonarQube`)
   - Configure server URL and authentication token

4. **Configure SonarQube Scanner:**
   - Navigate to: Manage Jenkins → Global Tool Configuration
   - Add SonarQube Scanner installation (Name: `SonarQubeScanner`)

5. **Create Pipeline Job:**
   - New Item → Pipeline
   - Configure: Pipeline script from SCM
   - SCM: Git
   - Repository URL: Your GitHub repository
   - Script Path: `Jenkinsfile`

6. **Run the Pipeline:**
   - Click "Build Now"
   - Monitor the 7 stages:
     - Git / GitHub
     - Jenkins
     - Maven Build
     - JUnit Tests
     - SonarQube
     - Docker
     - Release

### 3. SonarQube Setup

1. **Install SonarQube:**
   ```bash
   docker run -d --name sonarqube -p 9000:9000 sonarqube:lts-community
   ```

2. **Access SonarQube:**
   - URL: `http://localhost:9000`
   - Default credentials: admin / admin

3. **Create Project:**
   - Login to SonarQube
   - Create new project with key: `task-manager`
   - Generate authentication token
   - Update Jenkins with the token

### 4. Docker Hub Setup

1. **Create Docker Hub Account:**
   - Sign up at https://hub.docker.com

2. **Create Access Token:**
   - Account Settings → Security → New Access Token
   - Use this token in Jenkins credentials

3. **Update Docker Image Name:**
   - In `Jenkinsfile`, update `DOCKER_IMAGE_NAME` to your Docker Hub username:
     ```
     DOCKER_IMAGE_NAME = 'yourusername/taskmanager-app'
     ```

## Pipeline Stages Explained

### Stage 1: Git / GitHub
- Checks out source code from repository
- Captures git commit information
- Ensures code is in sync with remote

### Stage 2: Jenkins
- Initializes pipeline execution
- Sets build metadata (number, timestamp)
- Tracks build information

### Stage 3: Maven Build
- Compiles Java source code
- Packages application into JAR
- Validates build configuration

### Stage 4: JUnit Tests
- Runs all unit tests
- Generates test reports
- Validates functionality

### Stage 5: SonarQube
- Analyzes code quality
- Checks for bugs, vulnerabilities, code smells
- Generates coverage reports
- Enforces quality gates

### Stage 6: Docker
- Builds Docker image from Dockerfile
- Tags image with build number
- Validates container build

### Stage 7: Release
- Pushes Docker image to registry
- Tags as latest version
- Makes image available for deployment

## Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=TaskServiceTest
```

### View Test Coverage
```bash
mvn jacoco:report
# Open target/site/jacoco/index.html in browser
```

## Troubleshooting

### Maven Build Fails
- Check Java version: `java -version` (should be 17+)
- Check Maven version: `mvn -version`
- Clean and rebuild: `mvn clean install`

### Docker Build Fails
- Check Docker is running: `docker ps`
- Verify Dockerfile syntax
- Check for port conflicts

### Jenkins Pipeline Fails
- Check Jenkins logs
- Verify all plugins are installed
- Check credentials configuration
- Verify tool configurations (SonarQube Scanner)

### SonarQube Analysis Fails
- Verify SonarQube server is running
- Check authentication token
- Verify project key matches

## Security Considerations

- Never commit credentials to source code
- Use environment variables for sensitive data
- Implement proper authentication in production
- Enable HTTPS for production deployments
- Regularly update dependencies

## Future Enhancements

- Add integration tests
- Implement Kubernetes deployment
- Add monitoring with Prometheus/Grafana
- Implement blue-green deployments
- Add security scanning (Trivy)
- Implement canary releases

## Contributing

1. Fork the repository
2. Create feature branch
3. Make changes
4. Run tests: `mvn test`
5. Submit pull request

## License

This project is for educational purposes.

## Contact

For questions or issues, please open an issue in the repository.
