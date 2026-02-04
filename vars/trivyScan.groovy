def call(Map config) {
    stage('Trivy Scan') {
        sh """
          trivy image --severity HIGH,CRITICAL ${config.image}
        """
    }
}

