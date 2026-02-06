def call(Map config = [:]) {

    if (!config.image || !config.acrName) {
        error "dockerPush: image and acrName are required"
    }

    def appName = config.image.split(':')[0]

    stage("Push Image to ACR (${appName}:latest)") {
        sh """
          az acr login --name ${config.acrName}

          docker tag ${appName}:latest ${config.acrName}.azurecr.io/${appName}:latest
          docker push ${config.acrName}.azurecr.io/${appName}:latest
        """
    }
}
