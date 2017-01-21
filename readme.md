# Addons Loader Module

[![](https://jitpack.io/v/aziascreations/module-addonloader.svg?style=flat)](https://jitpack.io/#aziascreations/module-addonloader)
[![](https://img.shields.io/badge/License-Apache%20V2-blue.svg?style=flat)](LICENSE)

The addon loader module allows you to easily load addons in your program.

## Installation
The module isn't on the Maven Central Repository, therefore a third-party repository is required.

You have to add the [JitPack](https://jitpack.io/) repository to you project's pom.xml file.
```xml
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
```

And this dependency.
```xml
<dependency>
	<groupId>com.github.aziascreations</groupId>
	<artifactId>module-addonloader</artifactId>
	<version>-SNAPSHOT</version>
</dependency>
```
If you want a specific version, you just have to change the *-SNAPSHOT* version to the desired commit hash or release.

If you are using another build automation tool, you can check the [JitPack page](https://jitpack.io/#aziascreations/module-addonloader/) to see how to do it.

## Usage
You can check [usage.md](usage.md) for a "complete" guide.<br>
You can also check the [tests](src/test/java/com/azias/module/addons) and [examples](src/example/java/com/azias/module/addons/examples) to see how to "implement" it.

## Dependencies

#### Generic
* [Gson](https://github.com/google/gson)
* [Guava](https://github.com/google/guava)
* [Reflections](https://github.com/ronmamo/reflections)
* [Versioning Module](https://github.com/aziascreations/module-versioning)

#### Logging
* [SLF4J](https://github.com/qos-ch/slf4j)
* [Logback-core](https://mvnrepository.com/artifact/ch.qos.logback/logback-core)
* [Logback-classic](https://mvnrepository.com/artifact/ch.qos.logback/logback-classic)

#### Testing
* [JUnit 4](https://github.com/junit-team/junit4)
