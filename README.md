## 🔧 What This Shared Library Solves

* Eliminates duplicated Jenkinsfile logic
* Enforces consistent pipeline stages
* Enables secure Azure authentication
* Supports DevSecOps (SAST + image scanning)
* Makes pipelines readable, modular, and scalable

---

## 📂 Repository Structure

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

Each file in `vars/` represents a **reusable pipeline step**.

---

## 🚀 Supported CI/CD Stages

This shared library enables the following **end-to-end pipeline stages** for **both frontend and backend**:

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
11. Post-Deployment Verification

---

## 📦 Shared Library Functions

### 🔹 `springBootPipeline.groovy`

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

### 🔹 `angularPipeline.groovy`

Builds and tests the Angular frontend.

**Stages**

* Frontend Build & Test

```groovy
angularPipeline(
  appPath: 'AngularCRUD'
)
```

---

### 🔹 `dockerBuild.groovy`

Builds Docker images and tags them with Git SHA.

```groovy
def image = dockerBuild(
  appName: 'spring-crud',
  dockerfilePath: 'SpringCRUD/Dockerfile',
  context: 'SpringCRUD'
)
```

---

### 🔹 `trivyScan.groovy`

Performs container image security scanning.

```groovy
trivyScan(
  image: image
)
```

---

### 🔹 `dockerPush.groovy`

Pushes Docker images to Azure Container Registry.

```groovy
dockerPush(
  image: image,
  acrName: 'myacrname'
)
```

---

### 🔹 `azureLogin.groovy`

Logs into Azure using a Service Principal.

**Required Jenkins Credentials**

* `AZURE_CLIENT_ID`
* `AZURE_CLIENT_SECRET`
* `AZURE_TENANT_ID`
* `AZURE_SUBSCRIPTION_ID`

---

### 🔹 `aksLogin.groovy`

Fetches AKS kubeconfig securely.

**Required Jenkins Credentials**

* `AKS_NAME`
* `AKS_RG`

---

### 🔹 `helmValidate.groovy`

Validates Helm charts before deployment.

```groovy
helmValidate(
  chartPath: 'helm/umbrella'
)
```

---

### 🔹 `helmDeploy.groovy`

Deploys applications to AKS using Helm.

```groovy
helmDeploy(
  release: 'my-app',
  chartPath: 'helm/umbrella',
  namespace: 'default'
)
```

---

### 🔹 `postDeployCheck.groovy`

Verifies successful rollout after deployment.

```groovy
postDeployCheck(
  deployment: 'spring-crud',
  namespace: 'default'
)
```

---

## 🔐 Jenkins Credentials Required

Create the following credentials in **Jenkins → Manage Credentials**:

| Credential ID         | Type        |
| --------------------- | ----------- |
| AZURE_CLIENT_ID       | Secret Text |
| AZURE_CLIENT_SECRET   | Secret Text |
| AZURE_TENANT_ID       | Secret Text |
| AZURE_SUBSCRIPTION_ID | Secret Text |
| ACR_NAME              | Secret Text |
| AKS_NAME              | Secret Text |
| AKS_RG                | Secret Text |

---

## 🧪 Example Jenkinsfile (Using This Library)

```groovy
@Library('company-shared-lib') _

pipeline {
  agent any

  stages {

    stage('Backend CI') {
      steps {
        springBootPipeline(appPath: 'SpringCRUD')
      }
    }

    stage('Frontend CI') {
      steps {
        angularPipeline(appPath: 'AngularCRUD')
      }
    }

    stage('Azure Login') {
      steps {
        azureLogin()
      }
    }

    stage('Docker Build & Scan') {
      steps {
        script {
          def image = dockerBuild(
            appName: 'spring-crud',
            dockerfilePath: 'SpringCRUD/Dockerfile',
            context: 'SpringCRUD'
          )
          trivyScan(image: image)
        }
      }
    }

    stage('Push to ACR') {
      steps {
        dockerPush(
          image: image,
          acrName: env.ACR_NAME
        )
      }
    }

    stage('Deploy to AKS') {
      steps {
        aksLogin()
        helmValidate(chartPath: 'helm/umbrella')
        helmDeploy(
          release: 'spring-crud',
          chartPath: 'helm/umbrella',
          namespace: 'default'
        )
        postDeployCheck(
          deployment: 'spring-crud',
          namespace: 'default'
        )
      }
    }
  }
}
