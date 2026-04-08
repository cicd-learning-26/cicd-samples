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
    stage('Code Quality') {
        steps {
            sendGitHubStatus('PENDING', 'SonarQube', 'Analyzing code...')
            // your sonar command here
            sendGitHubStatus('SUCCESS', 'SonarQube', 'Analysis complete.')
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
        success {
            script {
                // Call the status helper for the green tick
                sendGitHubStatus('SUCCESS', 'Overall Pipeline', 'Build passed!')

                // Call the comment helper for the custom message
                def prMessage = """CI pipeline is successful.

    For deploying the project to sandbox before merging use the following comment:
    `/deploy-to-sbox`"""

                addGitHubComment(prMessage)
            }
        }
        failure {
            sendGitHubStatus('FAILURE', 'Overall Pipeline', 'Build failed. Check Jenkins logs.')
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

def addGitHubComment(String message) {
    withCredentials([usernamePassword(credentialsId: 'github-app', passwordVariable: 'GITHUB_TOKEN', usernameVariable: 'UNUSED')]) {
        script {
            // Encode the message to handle newlines and special characters for JSON
            def jsonMessage = groovy.json.JsonOutput.toJson([body: message])

            def repoPath = env.GIT_URL.replace("https://github.com/", "").replace(".git", "")

            // For PRs, the ID is available via CHANGE_ID environment variable
            bat """
            curl -L -X POST ^
            -H "Accept: application/vnd.github+json" ^
            -H "Authorization: Bearer %GITHUB_TOKEN%" ^
            -H "X-GitHub-Api-Version: 2022-11-28" ^
            https://api.github.com/repos/${repoPath}/issues/${env.CHANGE_ID}/comments ^
            -d "${jsonMessage.replace('"', '\\"')}"
            """
        }
    }
}