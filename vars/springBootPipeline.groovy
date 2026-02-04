def call(Map config = [:]) {

    pipeline {
        agent any

        environment {
            APP_NAME = config.appName
            APP_PATH = config.appPath
        }

        stages {

            stage('Checkout') {
                steps {
                    checkout scm
                }
            }

            stage('Gradle Build') {
                steps {
                    sh """
                      cd ${APP_PATH}
                      chmod +x gradlew
                      ./gradlew clean build
                    """
                }
            }
        }
    }
}

