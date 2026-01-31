pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build & Test') {
            steps {
                sh 'mvn clean verify'
            }
        }
        stage('Build Docker Images') {
            steps {
                echo 'Docker build steps will be implemented here'
            }
        }
    }
}
