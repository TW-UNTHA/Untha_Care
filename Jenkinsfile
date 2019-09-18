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
        stage('Running static code analysis') {
            steps {
                sh 'fastlane lint'
            }
        }
}