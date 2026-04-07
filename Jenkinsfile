pipeline {
    agent any

    // This ensures Java 21 is in the PATH for this specific build
    tools {
        jdk 'jdk-21' // 1. Must match the name defined in Global Tool Configuration
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        skipDefaultCheckout()
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
                script {
                    setGitHubPullRequestStatus(status: 'PENDING', message: 'Building Spring Boot App with Java 21...')

                    // --no-daemon is best for CI to avoid memory leaks
                    sh "./gradlew clean build --no-daemon"
                }
            }
        }

        stage('Archive Artifacts') {
            steps {
                // 2. This keeps the JAR file available in the Jenkins UI
                archiveArtifacts artifacts: 'build/libs/*.jar', followSymlinks: false
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            setGitHubPullRequestStatus(status: 'SUCCESS', message: 'Build passed & JAR archived!')
        }
        failure {
            setGitHubPullRequestStatus(status: 'FAILURE', message: 'Build failed. Check Java 21 compatibility logs.')
        }
    }
}

def setGitHubPullRequestStatus(Map params) {
    step([
        $class: 'GitHubCommitStatusSetter',
        reposSource: [$class: 'AnyDefinedRepositorySource'],
        contextSource: [$class: 'StaticStatusContextSource', context: 'Jenkins CI/Build'],
        statusResultSource: [$class: 'ConditionalStatusResultSource', results: [
            [$class: 'AnyBuildResult', message: params.message, state: params.status]
        ]]
    ])
}