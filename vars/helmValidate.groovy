def call(Map config = [:]) {

    if (!config.chartPath) {
        error "helmValidate: chartPath is required"
    }

    stage('Helm Lint & Template') {
        sh """
          helm lint ${config.chartPath}
          helm template ${config.chartPath}
        """
    }
}

