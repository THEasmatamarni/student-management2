pipeline {
  agent any
  tools { maven 'Maven-3.9'; jdk 'JDK-21' }
  stages {
    stage('Checkout') { steps { git branch: 'main', url: 'https://github.com/THEasmatamarni/student-management2.git' } }
    stage('Build')    { steps { sh 'mvn clean package -DskipTests' } }
    stage('Tests')    { steps { sh 'mvn test' } }
    stage('Sonar')    { steps { withSonarQubeEnv('sonar') { sh 'mvn sonar:sonar' } } }
    stage('Nexus')    { steps { sh 'mvn deploy -DskipTests' } }
    stage('Docker Build') {
            steps { sh 'docker build -t asma2002fst/student-management:latest .' }
                           }
    stage('Docker Push') {
            steps {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub',
                     usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
                        sh 'echo $DH_PASS | docker login -u $DH_USER --password-stdin'
                        sh 'docker push asma2002fst/student-management:latest'
                     }
            }
    }
  }

}