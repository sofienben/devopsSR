pipeline {
    agent any

    stages {
        stage('GitHub') {
            steps {
                checkout scm
            }
        }

        stage('Maven') {
            steps {
                sh 'mvn clean compile'
            }
        }
        stage('Unit Test: Mockito') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('Unit Test: Mockito, Junit') {
            steps {
                sh 'mvn -X test'
            }
        }

        stage('SonarQube') {
            steps {
                withSonarQubeEnv(installationName :'sq1') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Pre-Deploy: Nexus') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                    sh 'mvn deploy -e -X -Dnexus.login=$NEXUS_USER -Dnexus.password=$NEXUS_PASS -DskipTests'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh 'mvn package -DskipTests'
                    sh 'docker build -t sofienben/sofien .'
                }
            }
        }

        stage('Push to  DockerHub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh 'docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD'
                        sh 'docker push sofienben/sofien'
                    }
                }
            }
        }

        stage('Docker Compose') {
            steps {
                script {
                    sh 'docker compose up -d'
                }
            }
        }
    }

    post {
        always {
            sh 'docker image prune -f'
            cleanWs()
         }
    }
}
