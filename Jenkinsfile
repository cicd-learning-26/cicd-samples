pipeline {
    agent any

    stages {
        stage('Cloning repo') {
            steps {
                echo 'Cloning'
                git branch: 'develop', credentialsId: 'github-credentials', url: 'https://github.com/cicd-learning-26/cicd-samples.git'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying'
            }
        }
    }
}
