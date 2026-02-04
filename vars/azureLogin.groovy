def call() {
    stage('Azure Login') {
        withCredentials([
            string(credentialsId: 'AZURE_CLIENT_ID', variable: 'AZ_CLIENT_ID'),
            string(credentialsId: 'AZURE_CLIENT_SECRET', variable: 'AZ_CLIENT_SECRET'),
            string(credentialsId: 'AZURE_TENANT_ID', variable: 'AZ_TENANT_ID'),
            string(credentialsId: 'AZURE_SUBSCRIPTION_ID', variable: 'AZ_SUBSCRIPTION_ID')
        ]) {
            sh '''
              az login --service-principal \
                -u $AZ_CLIENT_ID \
                -p $AZ_CLIENT_SECRET \
                --tenant $AZ_TENANT_ID

              az account set --subscription $AZ_SUBSCRIPTION_ID
            '''
        }
    }
}

