def call(Map config = [:]) {

    if (!config.appName || !config.dockerfilePath || !config.context) {
        error "dockerBuild: appName, dockerfilePath, context are required"
    }

    def gitSha = sh(
        script: "git rev-parse --short HEAD",
        returnStdout: true
    ).trim()

    def image = "${config.appName}:${gitSha}"

    stage("Docker Build (${config.appName})") {
        sh """
          docker build \
            -f ${config.dockerfilePath} \
            -t ${image} \
            -t ${config.appName}:latest \
            ${config.context}
        """
    }

    return image
}

