def call() {
    stage('AKS Login') {
        withCredentials([
            string(credentialsId: 'AKS_NAME', variable: 'AKS_NAME'),
            string(credentialsId: 'AKS_RG', variable: 'AKS_RG')
        ]) {
            sh '''
              az aks get-credentials \
                --name $AKS_NAME \
                --resource-group $AKS_RG \
                --overwrite-existing
            '''
        }
    }
}

