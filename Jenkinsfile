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
        // Use "Build" instead of "Build & Test" to avoid the '&' character issues
        sendGitHubStatus('PENDING', 'Build', 'Running Gradle build...')
        bat "gradlew.bat clean build --no-daemon"
        sendGitHubStatus('SUCCESS', 'Build', 'Build passed!')
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
    withCredentials([usernamePassword(credentialsId: 'github-app', passwordVariable: 'GITHUB_TOKEN', usernameVariable: 'UNUSED')]) {
        script {
            // Use a simple map to build the JSON to ensure no weird formatting
            def payloadMap = [
                state: state.toLowerCase(),
                target_url: env.BUILD_URL,
                description: description,
                context: "Jenkins / ${context}"
            ]
            // Convert to a clean, single-line string
            def payload = groovy.json.JsonOutput.toJson(payloadMap)

            def repoPath = env.GIT_URL.replace("https://github.com/", "").replace(".git", "")

            // We use the @ symbol in Windows to handle quotes more safely
            bat "curl -L -X POST -H \"Accept: application/vnd.github+json\" " +
                "-H \"Authorization: Bearer %GITHUB_TOKEN%\" " +
                "-H \"X-GitHub-Api-Version: 2022-11-28\" " +
                "https://api.github.com/repos/${repoPath}/statuses/${env.GIT_COMMIT} " +
                "-d \"${payload.replace('"', '\\"')}\""
        }
    }
}