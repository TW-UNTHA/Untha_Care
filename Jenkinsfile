#!groovy

pipeline {
    agent any
    stages {
        stage('Running unit tests') {
            steps {
                sh 'fastlane unit'
            }
        }
    }
}