grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve

    repositories {
        inherits true // Whether to inherit repository definitions from plugins
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()

        // uncomment these to enable remote dependency resolution from public Maven repositories
        mavenCentral()
        //mavenLocal()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
		mavenRepo "http://ci-dev.renci.org/nexus/content/repositories/public"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
		// runtime 'mysql:mysql-connector-java:5.1.16'
		runtime 'postgresql:postgresql:9.0-801.jdbc4'
		runtime 'org.hsqldb:hsqldb:2.3.1'
		compile('org.apache.activemq:activemq-core:5.3.0',
			'org.apache.activemq:activeio-core:3.1.2',
			'org.apache.xbean:xbean-spring:3.7') {
			 excludes 'activemq-openwire-generator'
			 excludes 'commons-logging'
			 excludes 'xalan'
			 excludes 'xml-apis'
			 exported = false
		}
		compile('org.irods.jargon:jargon-core:3.0.1-SNAPSHOT') {
			excludes "slf4j-api", "slf4j-log4j12", "commons-io", "commons-codec"
		}
    }

    plugins {
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.7.1"
        runtime ":resources:1.1.6"
		runtime('org.apache.activemq:activemq-core:5.3.0',
			'org.apache.activemq:activeio-core:3.1.2',
			'org.apache.xbean:xbean-spring:3.7') {
			 excludes 'activemq-openwire-generator'
			 excludes 'commons-logging'
			 excludes 'xalan'
			 excludes 'xml-apis'
			 exported = false
		   }
	
        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.4"

        build ":tomcat:$grailsVersion"
		compile ":jms:1.2"
		
	  compile ":jquery-ui:1.8.15" 
	  compile ":modernizr:2.0.6" 
	  compile ":uploadr:0.5.11"
    }
}
