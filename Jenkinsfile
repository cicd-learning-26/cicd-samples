pipeline {
    agent any

    tools {
        jdk 'jdk-21'
    }

    stages {
        stage('Checkout') {
            steps {
                sendGitHubStatus('PENDING', 'Checkout', 'Checking out code...')
                checkout scm
                sendGitHubStatus('SUCCESS', 'Checkout', 'Code checked out.')
            }
        }

        stage('Build & Test') {
            steps {
                sendGitHubStatus('PENDING', 'Build & Test', 'Running Gradle build...')
                bat "gradlew.bat clean build --no-daemon"
                sendGitHubStatus('SUCCESS', 'Build & Test', 'Build and tests passed!')
            }
        }

        stage('Archive') {
            steps {
                sendGitHubStatus('PENDING', 'Archive', 'Archiving artifacts...')
                archiveArtifacts artifacts: 'build/libs/*.jar', allowEmptyArchive: true
                sendGitHubStatus('SUCCESS', 'Archive', 'Artifacts stored.')
            }
        }
    }

    post {
        failure {
            sendGitHubStatus('FAILURE', 'Overall Pipeline', 'Build failed. Check Jenkins logs.')
        }
        always {
            cleanWs()
        }
    }
}

def sendGitHubStatus(String state, String context, String description) {
    // This helper uses standard Jenkins environment variables to find the PR/Commit
    // It uses 'withCredentials' to get your GitHub App token safely
    withCredentials([usernamePassword(credentialsId: 'github-app', passwordVariable: 'GITHUB_TOKEN', usernameVariable: 'UNUSED')]) {
        script {
            def payload = """
            {
              "state": "${state.toLowerCase()}",
              "target_url": "${env.BUILD_URL}",
              "description": "${description}",
              "context": "Jenkins / ${context}"
            }
            """
            // Since you are on Windows, we use 'bat' to call curl
            // We strip 'https://github.com/' to get the 'owner/repo' path
            def repoPath = env.GIT_URL.replace("https://github.com/", "").replace(".git", "")

            bat """
            curl -L -X POST ^
            -H "Accept: application/vnd.github+json" ^
            -H "Authorization: Bearer %GITHUB_TOKEN%" ^
            -H "X-GitHub-Api-Version: 2022-11-28" ^
            https://api.github.com/repos/${repoPath}/statuses/${env.GIT_COMMIT} ^
            -d "${payload.replaceAll('\n', '').replaceAll('"', '\"')}"
            """
        }
    }
}