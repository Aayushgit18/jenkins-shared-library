def call(Map config = [:]) {

    pipeline {
        agent any

        stages {

            stage('Checkout') {
                steps {
                    checkout scm
                }
            }

            stage('Gradle Build') {
                steps {
                    script {
                        def appPath = config.appPath
                        sh """
                          cd ${appPath}
                          chmod +x gradlew
                          ./gradlew clean build
                        """
                    }
                }
            }
        }
    }
}
