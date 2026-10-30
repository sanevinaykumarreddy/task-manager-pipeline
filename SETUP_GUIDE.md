# Complete Setup Guide - Task Manager DevOps Pipeline

This guide provides step-by-step instructions to set up the complete 7-stage DevOps pipeline.

## Prerequisites Checklist

- [ ] Java 17+ installed
- [ ] Maven 3.8+ installed
- [ ] Git installed
- [ ] Docker installed and running
- [ ] GitHub account
- [ ] Docker Hub account
- [ ] Jenkins server (or access to Jenkins)
- [ ] SonarQube server (or access to SonarQube)

## Part 1: Local Setup

### 1.1 Install Java 17

**Windows:**
```bash
# Download from https://adoptium.net/
# Install and verify
java -version
```

**Linux:**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
java -version
```

**macOS:**
```bash
brew install openjdk@17
java -version
```

### 1.2 Install Maven

**Windows:**
```bash
# Download from https://maven.apache.org/download.cgi
# Extract and add to PATH
mvn -version
```

**Linux:**
```bash
sudo apt install maven
mvn -version
```

**macOS:**
```bash
brew install maven
mvn -version
```

### 1.3 Install Docker

**Windows:**
- Download Docker Desktop from https://www.docker.com/products/docker-desktop
- Install and start Docker Desktop

**Linux:**
```bash
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER
```

**macOS:**
```bash
brew install --cask docker
```

Verify Docker:
```bash
docker --version
docker ps
```

### 1.4 Clone and Build Project

```bash
# Navigate to project directory
cd task-manager-pipeline

# Clean build
mvn clean install

# Run tests
mvn test

# Generate coverage report
mvn jacoco:report

# Run application locally
mvn spring-boot:run
```

Test the application:
```bash
# Health check
curl http://localhost:8081/health

# Create a task
curl -X POST http://localhost:8081/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Task","description":"Test Description","status":"PENDING","priority":"HIGH"}'

# Get all tasks
curl http://localhost:8081/api/tasks
```

## Part 2: GitHub Setup

### 2.1 Create GitHub Repository

1. Go to https://github.com/new
2. Repository name: `task-manager-pipeline`
3. Description: `Task Manager with 7-stage DevOps CI/CD Pipeline`
4. Choose Public or Private
5. Click "Create repository"

### 2.2 Push Code to GitHub

```bash
# Initialize git
git init

# Add files
git add .

# Commit
git commit -m "Initial commit: Task Manager with CI/CD pipeline"

# Add remote (replace with your URL)
git remote add origin https://github.com/YOUR_USERNAME/task-manager-pipeline.git

# Push to main
git branch -M main
git push -u origin main
```

### 2.3 Configure GitHub Repository Settings

1. Go to repository Settings
2. Branches → Add rule
   - Branch name pattern: `main`
   - Require pull request reviews before merging
   - Require status checks to pass before merging
   - Require branches to be up to date before merging

## Part 3: Docker Hub Setup

### 3.1 Create Docker Hub Account

1. Go to https://hub.docker.com/signup
2. Sign up for free account
3. Verify email

### 3.2 Create Access Token

1. Login to Docker Hub
2. Click on your profile → Account Settings
3. Security → New Access Token
4. Description: `Jenkins CI/CD`
5. Access permissions: Read, Write, Delete
6. Generate and copy the token
7. Save the token securely (you won't see it again)

### 3.3 Update Jenkinsfile

Edit `Jenkinsfile` and update:
```groovy
DOCKER_IMAGE_NAME = 'YOUR_DOCKERHUB_USERNAME/taskmanager-app'
```

## Part 4: SonarQube Setup

### 4.1 Install SonarQube with Docker

```bash
# Pull SonarQube image
docker pull sonarqube:lts-community

# Run SonarQube
docker run -d \
  --name sonarqube \
  -p 9000:9000 \
  -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true \
  sonarqube:lts-community
```

### 4.2 Access SonarQube

1. Open browser: http://localhost:9000
2. Default credentials:
   - Username: `admin`
   - Password: `admin`
3. Change password when prompted

### 4.3 Create SonarQube Project

1. Click "Create Project"
2. Project key: `task-manager`
3. Display name: `Task-Manager`
4. Select "Locally"
5. Generate a token
6. Copy the token for Jenkins configuration

### 4.4 Configure Quality Gate

1. Go to Quality Gates
2. Edit "Sonar way"
3. Set thresholds:
   - Coverage: > 80%
   - Bugs: 0
   - Vulnerabilities: 0
   - Code Smells: < 10

## Part 5: Jenkins Setup

### 5.1 Install Jenkins

**Linux:**
```bash
# Install Jenkins
wget -q -O - https://pkg.jenkins.io/debian/jenkins.io.key | sudo apt-key add -
sudo sh -c 'echo deb http://pkg.jenkins.io/debian-stable binary/ > /etc/apt/sources.list.d/jenkins.list'
sudo apt update
sudo apt install jenkins

# Start Jenkins
sudo systemctl start jenkins
sudo systemctl enable jenkins
```

**Docker:**
```bash
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  jenkins/jenkins:lts
```

### 5.2 Access Jenkins

1. Open browser: http://localhost:8080
2. Get initial password:
   ```bash
   # For Docker
   docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword

   # For Linux
   sudo cat /var/lib/jenkins/secrets/initialAdminPassword
   ```
3. Install suggested plugins
4. Create admin user

### 5.3 Install Required Plugins

1. Navigate to: Manage Jenkins → Plugins
2. Available plugins → Search and install:
   - Docker Pipeline
   - SonarQube Scanner for Jenkins
   - Git
   - GitHub
   - Pipeline Maven Integration

### 5.4 Configure Credentials

1. Navigate to: Manage Jenkins → Credentials → System → Global credentials
2. Add Docker Hub credentials:
   - Kind: Username with password
   - Username: Your Docker Hub username
   - Password: Docker Hub access token
   - ID: `docker-hub-credentials`
   - Description: Docker Hub Access Token

3. Add GitHub credentials (if using private repo):
   - Kind: Username with password
   - Username: GitHub username
   - Password: GitHub personal access token
   - ID: `github-credentials`

### 5.5 Configure SonarQube in Jenkins

1. Navigate to: Manage Jenkins → Configure System
2. Scroll to SonarQube servers
3. Add SonarQube:
   - Name: `SonarQube`
   - Server URL: `http://localhost:9000` (or your SonarQube URL)
   - Server authentication token: Paste SonarQube token
   - Click "Apply" and "Save"

### 5.6 Configure SonarQube Scanner

1. Navigate to: Manage Jenkins → Global Tool Configuration
2. Scroll to SonarQube Scanner
3. Add SonarQube Scanner:
   - Name: `SonarQubeScanner`
   - Choose: Install automatically
   - Version: Latest
   - Click "Apply" and "Save"

### 5.7 Configure Maven

1. Navigate to: Manage Jenkins → Global Tool Configuration
2. Scroll to Maven
3. Add Maven:
   - Name: `Maven`
   - Choose: Install automatically
   - Version: 3.9.5
   - Click "Apply" and "Save"

### 5.8 Configure JDK

1. Navigate to: Manage Jenkins → Global Tool Configuration
2. Scroll to JDK
3. Add JDK:
   - Name: `JDK17`
   - JAVA_HOME: Path to Java 17 installation
   - Click "Apply" and "Save"

### 5.9 Create Pipeline Job

1. Click "New Item"
2. Name: `task-manager-pipeline`
3. Type: Pipeline
4. Click OK

5. Configure Pipeline:
   - General:
     - Description: `Task Manager CI/CD Pipeline`
   - Build Triggers:
     - ✓ Poll SCM
     - Schedule: `H/5 * * * *` (poll every 5 minutes)
   - Pipeline:
     - Definition: Pipeline script from SCM
     - SCM: Git
     - Repository URL: Your GitHub repository URL
     - Credentials: Select GitHub credentials (if private)
     - Branch: `*/main`
     - Script Path: `Jenkinsfile`

6. Click "Apply" and "Save"

### 5.10 Run the Pipeline

1. Go to the job page
2. Click "Build Now"
3. Click on the build number to view progress
4. Monitor each stage:
   - Git / GitHub
   - Jenkins
   - Maven Build
   - JUnit Tests
   - SonarQube
   - Docker
   - Release

## Part 6: Verify Deployment

### 6.1 Check Docker Hub

1. Login to Docker Hub
2. Navigate to your repositories
3. Verify `taskmanager-app` image exists
4. Check tags (build number and latest)

### 6.2 Pull and Run Image

```bash
# Pull from Docker Hub
docker pull YOUR_USERNAME/taskmanager-app:latest

# Run container
docker run -d -p 8080:8080 --name task-manager YOUR_USERNAME/taskmanager-app:latest

# Check logs
docker logs task-manager

# Test health endpoint
curl http://localhost:8080/health
```

### 6.3 Check SonarQube

1. Open SonarQube: http://localhost:9000
2. Navigate to `task-manager` project
3. Review:
   - Code quality metrics
   - Coverage percentage
   - Bugs, vulnerabilities, code smells
   - Quality gate status

## Part 7: Monitoring and Maintenance

### 7.1 View Jenkins Build History

- Navigate to job page
- View build history
- Click on each build for details
- Check console output for errors

### 7.2 Monitor SonarQube Quality

- Regularly check SonarQube dashboard
- Address critical issues immediately
- Monitor coverage trends
- Review code smells

### 7.3 Update Dependencies

```bash
# Check for updates
mvn versions:display-dependency-updates

# Update to latest versions
mvn versions:use-latest-versions

# Test after updates
mvn clean test
```

### 7.4 Backup Configuration

- Export Jenkins configuration
- Backup SonarQube database
- Document credential locations
- Version control Jenkinsfile changes

## Troubleshooting

### Jenkins Build Fails at Maven Stage

**Problem:** Maven not found
**Solution:**
- Check Maven installation in Global Tool Configuration
- Verify Maven home path
- Reinstall Maven if needed

### Docker Build Fails

**Problem:** Docker not available to Jenkins
**Solution:**
- Add Jenkins user to docker group: `sudo usermod -aG docker jenkins`
- Restart Jenkins
- Verify: `docker ps` (should work as Jenkins user)

### SonarQube Analysis Fails

**Problem:** Connection refused
**Solution:**
- Verify SonarQube is running: `docker ps | grep sonarqube`
- Check SonarQube URL in Jenkins configuration
- Verify token is correct
- Check firewall settings

### Tests Fail in Jenkins but Pass Locally

**Problem:** Environment differences
**Solution:**
- Check Java version in Jenkins
- Verify Maven version matches
- Review environment variables
- Check resource constraints

### Docker Push Fails

**Problem:** Authentication failed
**Solution:**
- Verify Docker Hub credentials in Jenkins
- Regenerate access token if needed
- Check username is correct
- Verify image name format: `username/repository`

## Alternative: GitHub Actions (Without Jenkins)

If you prefer GitHub Actions instead of Jenkins, create `.github/workflows/ci-cd.yml`:

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Build with Maven
      run: mvn clean package

    - name: Run Tests
      run: mvn test

    - name: Generate Coverage Report
      run: mvn jacoco:report

    - name: SonarQube Scan
      env:
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
      run: mvn sonar:sonar

    - name: Build Docker Image
      run: docker build -t taskmanager-app:${{ github.run_number }} .

    - name: Login to Docker Hub
      uses: docker/login-action@v2
      with:
        username: ${{ secrets.DOCKER_USERNAME }}
        password: ${{ secrets.DOCKER_PASSWORD }}

    - name: Push to Docker Hub
      run: |
        docker tag taskmanager-app:${{ github.run_number }} ${{ secrets.DOCKER_USERNAME }}/taskmanager-app:${{ github.run_number }}
        docker tag taskmanager-app:${{ github.run_number }} ${{ secrets.DOCKER_USERNAME }}/taskmanager-app:latest
        docker push ${{ secrets.DOCKER_USERNAME }}/taskmanager-app:${{ github.run_number }}
        docker push ${{ secrets.DOCKER_USERNAME }}/taskmanager-app:latest
```

Add secrets in GitHub repository settings:
- `SONAR_TOKEN`
- `DOCKER_USERNAME`
- `DOCKER_PASSWORD`

## Summary

You now have a complete 7-stage DevOps pipeline:
1. ✅ Git / GitHub - Source code management
2. ✅ Jenkins - Workflow automation
3. ✅ Maven Build - Compilation and packaging
4. ✅ JUnit Tests - Function verification
5. ✅ SonarQube - Code quality analysis
6. ✅ Docker - Containerization
7. ✅ Release - Distribution to Docker Hub

Each stage validates specific quality metrics, and failures halt progression to prevent faulty deployments.
