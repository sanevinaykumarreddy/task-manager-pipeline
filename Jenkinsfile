pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    environment {
        // Docker Hub credentials (configure in Jenkins)
        DOCKER_HUB_CREDENTIALS = 'docker-hub-credentials'
        DOCKER_IMAGE_NAME = 'sanevinay9/taskmanager-app'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        SONARQUBE_SCANNER_HOME = tool 'SonarQubeScanner'
    }

    stages {
        // Stage 1: Git / GitHub - Checkout source code
        stage('Git / GitHub') {
            steps {
                echo '=========================================='
                echo 'Stage 1: Git / GitHub - Store source code'
                echo '=========================================='
                checkout scm
                script {
                    env.GIT_COMMIT = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
                    env.GIT_SHORT_COMMIT = sh(script: 'git log -1 --pretty=%h', returnStdout: true).trim()
                }
                echo "Git commit: ${env.GIT_COMMIT}"
                echo "Git short commit: ${env.GIT_SHORT_COMMIT}"
            }
        }

        // Stage 2: Jenkins - Initialize workflow
        stage('Jenkins') {
            steps {
                echo '=========================================='
                echo 'Stage 2: Jenkins - Automate workflow'
                echo '=========================================='
                script {
                    env.BUILD_TIMESTAMP = sh(script: 'date +%Y%m%d-%H%M%S', returnStdout: true).trim()
                }
                echo "Build Number: ${env.BUILD_NUMBER}"
                echo "Build Timestamp: ${env.BUILD_TIMESTAMP}"
                echo "Jenkins URL: ${env.JENKINS_URL}"
            }
        }

        // Stage 3: Maven Build - Compile & package
        // Run Maven on the Jenkins agent (do not use docker.image().inside).
        // Jenkins-in-Docker typically has no docker CLI, so docker pull fails with "docker: not found".
        stage('Maven Build') {
            steps {
                echo '=========================================='
                echo 'Stage 3: Maven Build - Compile & package'
                echo '=========================================='
                sh 'mvn -B clean compile'
                sh 'mvn -B package -DskipTests'
                echo 'Maven build completed successfully'
            }
        }

        // Stage 4: JUnit Tests - Verify functions
        stage('JUnit Tests') {
            steps {
                echo '=========================================='
                echo 'Stage 4: JUnit Tests - Verify functions'
                echo '=========================================='
                sh 'mvn -B test'
                junit 'target/surefire-reports/*.xml'
                echo 'JUnit tests passed'
            }
        }

        // Stage 5: SonarQube - Analyze code quality
        stage('SonarQube') {
            steps {
                echo '=========================================='
                echo 'Stage 5: SonarQube - Analyze code quality'
                echo '=========================================='
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    sh 'mvn -B sonar:sonar ' +
                       '-Dsonar.host.url=http://172.17.0.3:9000 ' +
                       '-Dsonar.login=$SONAR_TOKEN ' +
                       '-Dsonar.projectKey=task-Manager ' +
                       '-Dsonar.projectName=Task-Manager ' +
                       '-Dsonar.sources=src/main/java ' +
                       '-Dsonar.tests=src/test/java ' +
                       '-Dsonar.java.binaries=target/classes ' +
                       '-Dsonar.junit.reportPaths=target/surefire-reports ' +
                       '-Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml'
                }
            }
        }

        // Stage 6: Docker - Containerize app
        stage('Docker') {
            steps {
                echo '=========================================='
                echo 'Stage 6: Docker - Containerize app'
                echo '=========================================='
                script {
                    // Check if Docker is available in the Jenkins container
                    def dockerAvailable = sh(script: 'docker --version', returnStatus: true) == 0
                    if (dockerAvailable) {
                        docker.build("${DOCKER_IMAGE_NAME}:${DOCKER_TAG}")
                        echo "Docker image built: ${DOCKER_IMAGE_NAME}:${DOCKER_TAG}"
                    } else {
                        echo 'Docker not available in Jenkins container. Skipping Docker build.'
                        echo 'To enable Docker builds, mount Docker socket to Jenkins container.'
                    }
                }
            }
        }

        // Stage 7: Release - Distribute version
        stage('Release') {
            steps {
                echo '=========================================='
                echo 'Stage 7: Release - Distribute version'
                echo '=========================================='
                script {
                    docker.withRegistry('https://registry.hub.docker.com', DOCKER_HUB_CREDENTIALS) {
                        docker.image("${DOCKER_IMAGE_NAME}:${DOCKER_TAG}").push()
                        docker.image("${DOCKER_IMAGE_NAME}:${DOCKER_TAG}").push('latest')
                    }
                    echo "Docker image pushed to registry: ${DOCKER_IMAGE_NAME}:${DOCKER_TAG}"
                }
            }
        }
    }

    post {
        success {
            echo '=========================================='
            echo 'Pipeline executed successfully!'
            echo '=========================================='
            echo "All stages completed: Git, Jenkins, Maven Build, JUnit Tests, SonarQube, Docker, Release"
        }
        failure {
            echo '=========================================='
            echo 'Pipeline failed!'
            echo '=========================================='
            echo 'Check the logs for details on which stage failed'
        }
        always {
            echo 'Cleaning up workspace...'
            cleanWs()
        }
    }
}
