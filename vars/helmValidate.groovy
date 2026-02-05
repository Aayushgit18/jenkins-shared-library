def call(Map config = [:]) {

    if (!config.chartPath) {
        error "helmValidate: chartPath is required"
    }

    stage('Helm Lint & Template') {
        sh """
          cd ${config.chartPath}
          helm dependency build
          helm lint .
          helm template .
        """
    }
}
