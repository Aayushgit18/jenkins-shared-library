def call(Map config = [:]) {

    if (!config.image || !config.acrName) {
        error "dockerPush: image and acrName are required"
    }

    stage('Push Image to ACR') {
        sh """
          az acr login --name ${config.acrName}
          docker tag ${config.image} ${config.acrName}.azurecr.io/${config.image}
          docker push ${config.acrName}.azurecr.io/${config.image}
        """
    }
}

