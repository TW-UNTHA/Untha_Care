#!groovy

pipeline {
    agent any
    stages {
        stage('Running unit tests') {
            steps {
                sh 'fastlane unit'
            }
        }

        stage('Running lint') {
             steps {
                sh 'fastlane lint'
             }
        }

        stage('Running static code analysis') {
              steps {
                 sh 'fastlane detekt'
              }
         }

         stage('Running instrumented tests') {
              steps {
                 sh 'fastlane functional'
              }
         }
    }
}