def call(Map config = [:]) {

    if (!config.deployment || !config.namespace) {
        error "postDeployCheck: deployment and namespace required"
    }

    stage('Post Deployment Verification') {
        sh """
          kubectl rollout status deployment/${config.deployment} -n ${config.namespace}
        """
    }
}

