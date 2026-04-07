pipeline {
    agent any

    tools {
        jdk 'jdk-21'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            when {
                changeRequest()
            }
            steps {
                // 1. Built-in command to set GitHub status to PENDING
                githubNotify context: 'Jenkins CI/Build', description: 'Building Spring Boot App...', status: 'PENDING'

                sh "./gradlew clean build --no-daemon"
            }
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'build/libs/*.jar', allowEmptyArchive: true
            }
        }
    }

    post {
        success {
            // 2. Set status to SUCCESS
            githubNotify context: 'Jenkins CI/Build', description: 'Build passed!', status: 'SUCCESS'
        }
        failure {
            // 3. Set status to FAILURE
            githubNotify context: 'Jenkins CI/Build', description: 'Build failed. Check logs.', status: 'FAILURE'
        }
        always {
            cleanWs()
        }
    }
}