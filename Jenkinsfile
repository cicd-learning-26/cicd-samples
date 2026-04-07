pipeline {
    agent any

    tools {
        jdk 'jdk-21'
    }

    stages {
        stage('Checkout') {
            steps {
                updateGitHubStatus('Checkout', 'PENDING', 'Checking out code...')
                checkout scm
                updateGitHubStatus('Checkout', 'SUCCESS', 'Code checked out.')
            }
        }

        stage('Build & Test') {
            steps {
                updateGitHubStatus('Build & Test', 'PENDING', 'Running Gradle build...')
                bat "gradlew.bat clean build --no-daemon"
                updateGitHubStatus('Build & Test', 'SUCCESS', 'Build and tests passed!')
            }
        }

        stage('Archive') {
            steps {
                updateGitHubStatus('Archive', 'PENDING', 'Archiving artifacts...')
                archiveArtifacts artifacts: 'build/libs/*.jar', allowEmptyArchive: true
                updateGitHubStatus('Archive', 'SUCCESS', 'Artifacts stored.')
            }
        }
    }

    post {
        failure {
            // This catches any stage that failed and marks the overall build failed
            updateGitHubStatus('Jenkins Pipeline', 'FAILURE', 'Pipeline failed. Check logs.')
        }
        always {
            cleanWs()
        }
    }
}

// Helper function using the base 'step' command to avoid 'NoSuchMethod' errors
def updateGitHubStatus(String contextName, String state, String msg) {
    step([
        $class: 'GitHubCommitStatusSetter',
        reposSource: [$class: 'AnyDefinedRepositorySource'],
        contextSource: [$class: 'StaticStatusContextSource', context: "Jenkins/${contextName}"],
        statusResultSource: [
            $class: 'ConditionalStatusResultSource',
            results: [[$class: 'AnyBuildResult', message: msg, state: state]]
        ]
    ])
}