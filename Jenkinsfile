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
            steps {
                // This uses the 'publishChecks' step found in your logs
                publishChecks name: "Jenkins Build",
                              title: "Compiling Spring Boot App",
                              summary: "Running on Java 21",
                              status: 'IN_PROGRESS'

                // Using 'bat' for Windows since your logs show a Windows path
                bat "./gradlew clean build --no-daemon"
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
            publishChecks name: "Jenkins Build",
                          title: "Build Success",
                          summary: "All tests passed.",
                          conclusion: 'SUCCESS'
        }
        failure {
            publishChecks name: "Jenkins Build",
                          title: "Build Failed",
                          summary: "Check Jenkins logs for details.",
                          conclusion: 'FAILURE'
        }
        always {
            cleanWs()
        }
    }
}