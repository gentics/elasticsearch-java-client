pipeline {
	agent {
		label "maven"
	}
	stages {
		stage("Build") {
			steps {
				sh "mvn -U -B clean package"
			}
			post {
				always {
					junit  "**/target/surefire-reports/*.xml"
				}
			}
		}
	}
}
