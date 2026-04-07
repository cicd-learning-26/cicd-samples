pipeline {
    agent any

    tools {
        jdk 'jdk-21'
    }

    stages {
        stage('CI Pipeline') {
            steps {
                // withChecks automatically maps Pipeline stages to GitHub Check steps
                withChecks('Jenkins CI Pipeline') {
                    script {
                        stage('Checkout') {
                            checkout scm
                        }

                        stage('Build & Test') {
                            // Using bat for your Windows environment
                            bat "gradlew.bat clean build --no-daemon"
                        }

                        stage('Archive') {
                            archiveArtifacts artifacts: 'build/libs/*.jar', allowEmptyArchive: true
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}