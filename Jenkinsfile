pipeline {
  agent any
  tools { maven 'Maven-3.9' }
  environment {
      PROJECT_DIR = 'student-management2'
  }
  stages {
    stage('Checkout') { steps { git branch: 'main', url: 'https://github.com/THEasmatamarni/student-management2.git' } }
    stage('Build')    { steps {dir('student-management2'){ sh 'mvn clean package -DskipTests' } }}
    stage('Tests')    { steps {dir('student-management2'){ sh 'mvn test' } }}
    stage('Sonar')    { steps {dir('student-management2'){ withSonarQubeEnv('sonar') { sh 'mvn sonar:sonar' } } }}
    stage('Nexus')    { steps {dir('student-management2'){ sh 'mvn deploy -DskipTests' } }}
    stage('Docker Build') {
            steps {dir('student-management2') { sh 'docker build -t asma2002fst/student-management:latest .' }}
                           }
    stage('Docker Push') {
            steps {dir('student-management2'){
                    withCredentials([usernamePassword(credentialsId: 'dockerhub',
                     usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
                        sh 'echo $DH_PASS | docker login -u $DH_USER --password-stdin'
                        sh 'docker push asma2002fst/student-management:latest'
                     }}
            }
    }
  }
  post {
          always {
              junit testResults: 'student-management/target/surefire-reports/*.xml',
                    allowEmptyResults: true
          }
          success {
              echo 'Pipeline execute avec succes !'
          }
          failure {
              echo 'Echec du pipeline : verifier la console.'
          }
      }

}