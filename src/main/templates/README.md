# Liquibase R2DBC Spring Boot starter [![tests](https://github.com/daggerok/liquibase-r2dbc-spring-boot-starter/actions/workflows/tests.yml/badge.svg)](https://github.com/daggerok/liquibase-r2dbc-spring-boot-starter/actions/workflows/tests.yml) [![pages](https://github.com/daggerok/liquibase-r2dbc-spring-boot-starter/actions/workflows/pages.yaml/badge.svg)](https://daggerok.github.io/liquibase-r2dbc-spring-boot-starter/)
This repository demonstrates how 2 implement Liquibase R2DBC Spring Boot starter to be used in reactive projects with MySQL, MariaDB,
PostgreSQL, MS SQL Server, r2dbc-pool, r2dbc-proxy or H2 database. It's not pure reactive liquibase implementation as you might think, we
are simply transforming R2DBC URL into liquibase compatible in a little spring-boot starter to apply liquibase migrations update
automatically within application runner bean

[![Maven Central](https://img.shields.io/maven-central/v/io.github.daggerok/liquibase-r2dbc-spring-boot-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.daggerok%22%20AND%20a:%22liquibase-r2dbc-spring-boot-starter%22)

### Getting started

```xml
<dependency>
  <groupId>io.github.daggerok</groupId>
  <artifactId>liquibase-r2dbc-spring-boot-starter</artifactId>
  <version>${project.version}</version>
</dependency>
```

or

```kotlin
dependency("io.github.daggerok:liquibase-r2dbc-spring-boot-starter:${project.version}")
```

If you want to use `*-SNAPSHOT` version, please make sure you have added snapshot maven repository like so

```xml
<repositories>
    <repository>
        <id>sonatype-nexus-snapshots</id>
        <name>Sonatype OSS Snapshot Repository</name>
        <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
        <snapshots><enabled>true</enabled></snapshots>
        <releases><enabled>false</enabled></releases>
    </repository>
</repositories>
```

or

```kotlin
repositories {
    maven {
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}
```

See: https://s01.oss.sonatype.org/content/repositories/snapshots/io/github/daggerok/

Otherwise, use only released version. See: https://repo1.maven.org/maven2/io/github/daggerok/liquibase-r2dbc-spring-boot-starter/

### Technology Stack

* Liquibase
* R2DBC
* Spring Boot
* Kotlin
* Junit Jupiter 5
* H2 (file, mem)
* MySQL
* MariaDB
* Postgresql
* MS SQL Server
* R2DBC (pool, proxy)
* Testcontainers
* Maven

### Reference documentation

Checkout [documentation](https://daggerok.github.io/liquibase-r2dbc-spring-boot-starter/) for details

ALso check [developer guide for contributors](docs/CONTRIBUTING.md)
