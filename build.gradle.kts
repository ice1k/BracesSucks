import groovy.lang.Closure
import org.gradle.api.internal.HasConvention
import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val packageName = "org.ice1000.braceSucks"
val pluginVersion = "0.0.1"
val kotlinVersion: String by extra

group = packageName
version = pluginVersion

buildscript {
	var kotlinVersion: String by extra
	kotlinVersion = "1.2.31"
	repositories { mavenCentral() }
	dependencies { classpath(kotlin("gradle-plugin", kotlinVersion)) }
}

plugins {
	idea
	java
	id("org.jetbrains.intellij") version "0.3.1"
	kotlin("jvm") version "1.2.31"
}

// https://github.com/gradle/kotlin-dsl/issues/537/
idea { module { excludeDirs = excludeDirs + file("pinpoint_piggy") } }

intellij {
	updateSinceUntilBuild = false
	instrumentCode = true
	if (System.getProperty("user.name") == "ice1000")
		localPath = "/home/ice1000/.local/share/JetBrains/Toolbox/apps/IDEA-U/ch-0/181.4203.550"
	else version = "2018.1"
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> { kotlinOptions { jvmTarget = "1.8" } }

tasks.withType<PatchPluginXmlTask> {
	changeNotes(file("res/META-INF/change-notes.html").readText())
	pluginDescription(file("res/META-INF/description.html").readText())
	version(pluginVersion)
	pluginId(packageName)
}

val SourceSet.kotlin get() = (this as HasConvention).convention.getPlugin(KotlinSourceSet::class.java).kotlin

java.sourceSets {
	"main" {
		kotlin.srcDirs("src")
		resources.srcDirs("res")
	}

	"test" { kotlin.srcDirs("test") }
}

repositories { mavenCentral() }

dependencies {
	compileOnly(kotlin("stdlib-jdk8", kotlinVersion))
	compile(kotlin("stdlib-jdk8", kotlinVersion).toString()) {
		exclude(module = "kotlin-runtime")
		exclude(module = "kotlin-stdlib")
		exclude(module = "kotlin-reflect")
	}
	testCompile("junit", "junit", "4.12")
	testCompile(kotlin("test-junit", kotlinVersion))
	testCompile(kotlin("stdlib-jdk8", kotlinVersion))
}

