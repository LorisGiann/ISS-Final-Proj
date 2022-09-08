import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.2"
	id("io.spring.dependency-management") version "1.0.12.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	distribution
	application
}

group = "unibo"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

application {
	mainClass.set("unibo.WebRobotKt.WebRobotKtApplicationKt")
}

repositories {
	mavenCentral()
	flatDir { dirs("../../unibolibs") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-mustache")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework:spring-websocket:5.3.22")
	implementation("org.springframework:spring-messaging:5.3.22")
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")
	developmentOnly("org.springframework.boot:spring-boot-devtools")

//FOR WebSocket NOSTOMP
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("org.springframework:spring-websocket:5.3.14")
//added
	implementation("org.webjars:webjars-locator-core")
	implementation("org.webjars:bootstrap:5.1.3")
	implementation("org.webjars:jquery:3.6.0")

//-------------------------------------------------------------------------------

	/*
        //SERVE ALLA INFRASTRUTTURA AD ATTORI
        // This dependency is used by the application.
        implementation 'com.google.guava:guava:30.1.1-jre'
        // Use the Kotlin test library.
        testImplementation 'org.jetbrains.kotlin:kotlin-test'
        // Use the Kotlin JUnit integration.
        testImplementation 'org.jetbrains.kotlin:kotlin-test-junit'
        // COROUTINES **********************************
        // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
        implementation group: 'org.jetbrains.kotlinx', name: 'kotlinx-coroutines-core', version: "$kotlinVersion", ext: 'pom'
        // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core-jvm
        implementation group: 'org.jetbrains.kotlinx', name: 'kotlinx-coroutines-core-jvm', version: "$kotlinVersion"

    */

	/* COAP **************************************************************************************************************** */
	// https://mvnrepository.com/artifact/org.eclipse.californium/californium-core
	implementation("org.eclipse.californium:californium-core:3.6.0")
	// https://mvnrepository.com/artifact/org.eclipse.californium/californium-proxy2
	implementation("org.eclipse.californium:californium-proxy2:3.6.0")


	/* UNIBO *************************************************************************************************************** */
	implementation(":uniboInterfaces")
	implementation(":2p301")
	implementation(":unibo.comm22-1.1")
//	implementation name: 'it.unibo.qakactor-2.7'
//	implementation name: 'unibonoawtsupports'  //required by the old infrastructure

	//implementation name: 'unibo.actor22-1.1'   //using actor22comm  FLAT?


	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4")
	testImplementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
	testImplementation(":unibo.qakactor22-3.1")
	//implementation name: 'unibo.qakactor22-2.8'  //per ApplMessage


}


tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> { useJUnitPlatform() }