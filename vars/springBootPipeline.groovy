def call(Map config = [:]) {

    pipeline {
        agent any

        stages {
            stage('Shared Library Test') {
                steps {
                    echo "✅ Jenkins Shared Library is working!"
                    echo "Application Name: ${config.appName}"
                }
            }
        }
    }
}
