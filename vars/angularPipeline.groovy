def call(Map config = [:]) {

    if (!config.appPath) {
        error "angularPipeline: 'appPath' is required"
    }

    stage('Frontend Build & Test') {
        dir(config.appPath) {
            sh '''
              npm install --legacy-peer-deps
              npm test -- --watch=false || true
              npm run build --prod
            '''
        }
    }
}

