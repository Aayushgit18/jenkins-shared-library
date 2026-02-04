
## What This Shared Library Solves

* Eliminates duplicated Jenkinsfile logic
* Enforces consistent pipeline stages
* Enables secure Azure authentication
* Supports DevSecOps (SAST + container image scanning)
* Makes pipelines readable, modular, and scalable

---

## Repository Structure

```
jenkins-shared-library/
├── vars/
│   ├── aksLogin.groovy
│   ├── angularPipeline.groovy
│   ├── azureLogin.groovy
│   ├── dockerBuild.groovy
│   ├── dockerPush.groovy
│   ├── helmDeploy.groovy
│   ├── helmValidate.groovy
│   ├── postDeployCheck.groovy
│   ├── springBootPipeline.groovy
│   └── trivyScan.groovy
└── README.md
```

Each file inside `vars/` represents a **reusable Jenkins pipeline step** that can be composed inside application Jenkinsfiles.

---

## Supported CI/CD Stages

This shared library enables the following **end-to-end CI/CD stages** for **both frontend and backend services**:

1. Checkout SCM
2. Backend Build & Unit Test (Gradle)
3. Frontend Build & Test (Angular)
4. Code Quality Validation (Checkstyle, SpotBugs, PMD, JaCoCo)
5. Docker Image Build (Spring Boot & Angular)
6. Docker Image Security Scan (Trivy)
7. Docker Push to Azure Container Registry (ACR)
8. Helm Lint & Template Validation
9. Deploy to AKS using Helm
10. Azure Key Vault Secret Injection
11. Post Deployment Verification

---

## Shared Library Functions

### `springBootPipeline.groovy`

Builds and tests the Spring Boot backend using Gradle.

**Stages**

* Checkout
* Gradle Build & Unit Tests

```groovy
springBootPipeline(
  appPath: 'SpringCRUD'
)
```

---

### `angularPipeline.groovy`

Builds and tests the Angular frontend.

**Stages**

* Frontend Build & Test

```groovy
angularPipeline(
  appPath: 'AngularCRUD'
)
```

---

### `dockerBuild.groovy`

Builds Docker images and tags them using the Git commit SHA.

```groovy
def image = dockerBuild(
  appName: 'spring-crud',
  dockerfilePath: 'SpringCRUD/Dockerfile',
  context: 'SpringCRUD'
)
```

---

### `trivyScan.groovy`

Scans Docker images for HIGH and CRITICAL vulnerabilities.

```groovy
trivyScan(
  image: image
)
```

---

### `dockerPush.groovy`

Pushes Docker images to Azure Container Registry.

```groovy
dockerPush(
  image: image,
  acrName: 'myacrname'
)
```

---

### `azureLogin.groovy`

Authenticates Jenkins to Azure using a Service Principal.

**Required Jenkins Credentials**

* `AZURE_CLIENT_ID`
* `AZURE_CLIENT_SECRET`
* `AZURE_TENANT_ID`
* `AZURE_SUBSCRIPTION_ID`

---

### `aksLogin.groovy`

Fetches AKS kubeconfig securely.

**Required Jenkins Credentials**

* `AKS_NAME`
* `AKS_RG`

---

### `helmValidate.groovy`

Validates Helm charts before deployment.

```groovy
helmValidate(
  chartPath: 'helm/umbrella'
)
```

---

### `helmDeploy.groovy`

Deploys applications to AKS using Helm.

```groovy
helmDeploy(
  release: 'my-app',
  chartPath: 'helm/umbrella',
  namespace: 'default'
)
```

---

### `postDeployCheck.groovy`

Verifies Kubernetes rollout status after deployment.

```groovy
postDeployCheck(
  deployment: 'spring-crud',
  namespace: 'default'
)
```

---

## Jenkins Credentials Required

Create the following credentials in **Jenkins → Manage Credentials**:

| Credential ID         | Type        |
| --------------------- | ----------- |
| AZURE_CLIENT_ID       | Secret Text |
| AZURE_CLIENT_SECRET   | Secret Text |
| AZURE_TENANT_ID       | Secret Text |
| AZURE_SUBSCRIPTION_ID | Secret Text |
| ACR_NAME              | Secret Text |
| ACR_LOGIN_SERVER      | Secret Text |
| AKS_NAME              | Secret Text |
| AKS_RG                | Secret Text |

---

## Example Jenkinsfile (End-to-End, Secure)

```groovy
@Library('company-shared-lib') _

pipeline {
    agent any

    environment {
        // Azure / AKS
        ACR_NAME        = credentials('ACR_NAME')
        ACR_LOGIN       = credentials('ACR_LOGIN_SERVER')
        AKS_NAME        = credentials('AKS_NAME')
        AKS_RG          = credentials('AKS_RG')

        // Kubernetes
        K8S_NAMESPACE   = 'default'
    }

    stages {

        stage('Backend Build & Unit Test') {
            steps {
                springBootPipeline(
                    appPath: 'SpringCRUD'
                )
            }
        }

        stage('Frontend Build & Test') {
            steps {
                angularPipeline(
                    appPath: 'AngularCRUD'
                )
            }
        }

        stage('Docker Build - Backend') {
            steps {
                script {
                    BACKEND_IMAGE = dockerBuild(
                        appName: 'spring-crud',
                        dockerfilePath: 'SpringCRUD/Dockerfile',
                        context: 'SpringCRUD'
                    )
                }
            }
        }

        stage('Docker Build - Frontend') {
            steps {
                script {
                    FRONTEND_IMAGE = dockerBuild(
                        appName: 'angular-crud',
                        dockerfilePath: 'AngularCRUD/Dockerfile',
                        context: 'AngularCRUD'
                    )
                }
            }
        }

        stage('Trivy Scan Images') {
            steps {
                trivyScan(image: BACKEND_IMAGE)
                trivyScan(image: FRONTEND_IMAGE)
            }
        }

        stage('Azure Login') {
            steps {
                azureLogin()
            }
        }

        stage('Push Images to ACR') {
            steps {
                dockerPush(image: BACKEND_IMAGE, acrName: env.ACR_NAME)
                dockerPush(image: FRONTEND_IMAGE, acrName: env.ACR_NAME)
            }
        }

        stage('AKS Login') {
            steps {
                aksLogin()
            }
        }

        stage('Helm Lint & Template') {
            steps {
                helmValidate(chartPath: 'helm/umbrella')
            }
        }

        stage('Deploy to AKS') {
            steps {
                helmDeploy(
                    release: 'three-tier-app',
                    chartPath: 'helm/umbrella',
                    namespace: env.K8S_NAMESPACE
                )
            }
        }

        stage('Post Deployment Verification') {
            steps {
                postDeployCheck(
                    deployment: 'spring-crud',
                    namespace: env.K8S_NAMESPACE
                )
            }
        }
    }
}
