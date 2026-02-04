def call(Map config = [:]) {

    if (!config.appPath) {
        error "springBootPipeline: 'appPath' is required"
    }

    stage('Checkout') {
        checkout scm
    }

    stage('Gradle Build') {
        dir(config.appPath) {
            sh '''
                chmod +x gradlew
                ./gradlew clean build
            '''
        }
    }
}
