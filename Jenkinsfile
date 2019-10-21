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

      stage('Uploading QA Version') {
         steps {
            sh "curl -o ${WORKSPACE}/app/google-services.json ${BUILD_JSON_QA}"
            sh 'fastlane distribute group:thoughtworks'
         }
      }
   }
   post {
       always {
          echo 'I will always say Hello again!'
               emailext body: "${currentBuild.currentResult}: Job ${env.JOB_NAME} build ${env.BUILD_NUMBER}\n More info at: ${env.BUILD_URL}",
               recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']],
               subject: "Jenkins Build ${currentBuild.currentResult}: Job ${env.JOB_NAME}"
       }
   }
}