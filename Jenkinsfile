pipeline {
  agent any
  tools { maven 'Maven' }
  environment {
      PROJECT_DIR = 'student-management2'
  }
  stages {
    stage('Checkout') { steps { git branch: 'main', url: 'https://github.com/THEasmatamarni/student-management2.git' } }
    stage('Build')    { steps {dir('student-management2'){ sh 'mvn clean package -DskipTests' } }}
    stage('Tests')    { steps {dir('student-management2'){ sh 'mvn test' } }}
    stage('Sonar')    { steps {dir('student-management2'){ withSonarQubeEnv('sonar') { sh '''
                                                                                                           mvn org.sonarsource.scanner.maven:sonar-maven-plugin:5.1.0.4751:sonar
                                                                                                       '''} } }}
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
        junit '**/target/surefire-reports/*.xml'
          }
          success {
              echo 'Pipeline execute avec succes !'
          }
          failure {
              echo 'Echec du pipeline : verifier la console.'
          }
      }

}