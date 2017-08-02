#!groovy

pipeline {
    agent any
    stages {
        stage ('Build') {
            steps {
                sh 'mvn clean install -U -Dsurefire.rerunFailingTestsCount=5' 
            }
        }
    }
}
