pipeline {
  agent any
  tools { maven 'Maven' }
environment {
    REGISTRY = 'asma2002fst/student-management'
    IMAGE_TAG = 'latest'
    AWS_REGION = 'us-east-1'

    AWS_CREDENTIALS_ID = 'awsCredentials'
    AWS_SESSION_TOKEN_ID = 'awsSessionToken'
    KUBECONFIG_CREDENTIAL_ID = 'kubeid'

    EKS_CLUSTER_NAME = 'student-eks'
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


stage('Test AWS Credentials') {
    steps {
        withCredentials([
            usernamePassword(
                credentialsId: 'awsCredentials',
                usernameVariable: 'AWS_ACCESS_KEY_ID',
                passwordVariable: 'AWS_SECRET_ACCESS_KEY'
            ),
            string(
                credentialsId: 'awsSessionToken',
                variable: 'AWS_SESSION_TOKEN'
            )
        ]) {
            sh '''
                set +x
                export AWS_DEFAULT_REGION=us-east-1
                echo "Testing AWS authentication..."
                aws sts get-caller-identity
            '''
        }
    }
}
stage('DEPLOY TO AWS KUBERNETES') {
    steps {
        dir('student-management2') {
            withCredentials([
                usernamePassword(
                    credentialsId: 'awsCredentials',
                    usernameVariable: 'AWS_ACCESS_KEY_ID',
                    passwordVariable: 'AWS_SECRET_ACCESS_KEY'
                ),
                string(
                    credentialsId: 'awsSessionToken',
                    variable: 'AWS_SESSION_TOKEN'
                ),
                file(
                    credentialsId: 'kubeid',
                    variable: 'KUBECONFIG'
                )
            ]) {
                sh '''
                    set +x
                    export AWS_DEFAULT_REGION=us-east-1

                    echo "Checking AWS credentials..."
                    aws sts get-caller-identity

                    echo "Checking Kubernetes cluster..."
                    kubectl --kubeconfig="$KUBECONFIG" cluster-info

                    echo "Deploying MySQL..."
                    kubectl --kubeconfig="$KUBECONFIG" apply -f k8s-eks/mysql.yaml

                    echo "Deploying Spring Boot application..."
                    kubectl --kubeconfig="$KUBECONFIG" apply -f k8s-eks/app.yaml

                    echo "Waiting for MySQL deployment..."
                    kubectl --kubeconfig="$KUBECONFIG" rollout status deployment/mysql --timeout=300s

                    echo "Waiting for Student application deployment..."
                    kubectl --kubeconfig="$KUBECONFIG" rollout status deployment/student-app --timeout=300s

                    echo "Checking Kubernetes resources..."
                    kubectl --kubeconfig="$KUBECONFIG" get deployments
                    kubectl --kubeconfig="$KUBECONFIG" get pods
                    kubectl --kubeconfig="$KUBECONFIG" get services
                '''
            }
        }
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