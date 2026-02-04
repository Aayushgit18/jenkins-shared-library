def call(Map config) {

    if (!config.appName || !config.dockerfilePath || !config.context) {
        error "dockerBuild: appName, dockerfilePath, and context are required"
    }

    def gitSha = sh(
        script: "git rev-parse --short HEAD",
        returnStdout: true
    ).trim()

    def imageTag = "${config.appName}:${gitSha}"

    stage("Docker Build") {
        sh """
            docker build \
              -f ${config.dockerfilePath} \
              -t ${imageTag} \
              -t ${config.appName}:latest \
              ${config.context}
        """
    }

    echo "Docker image built: ${imageTag}"
    return imageTag
}

