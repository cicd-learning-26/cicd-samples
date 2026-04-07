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
                // Signal GitHub start
                setGitHubPullRequestStatus('PENDING', 'Building Spring Boot App with Java 21...')

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
            setGitHubPullRequestStatus('SUCCESS', 'Build passed & JAR archived!')
        }
        failure {
            setGitHubPullRequestStatus('FAILURE', 'Build failed. Check Jenkins logs.')
        }
        always {
            cleanWs()
        }
    }
}

// Simplified Helper Function
def setGitHubPullRequestStatus(String state, String message) {
    step([
        $class: 'GitHubCommitStatusSetter',
        reposSource: [$class: 'AnyDefinedRepositorySource'],
        contextSource: [$class: 'DefaultStatusContextSource', context: 'Jenkins CI/Build'],
        statusResultSource: [
            $class: 'ConditionalStatusResultSource',
            results: [[$class: 'AnyBuildResult', message: message, state: state]]
        ]
    ])
}