def call(Map config = [:]) {

    if (!config.release || !config.chartPath || !config.namespace) {
        error "helmDeploy: release, chartPath, namespace required"
    }

    stage('Deploy to AKS') {
        sh """
          helm upgrade --install ${config.release} ${config.chartPath} \
            --namespace ${config.namespace} \
        """
    }
}

