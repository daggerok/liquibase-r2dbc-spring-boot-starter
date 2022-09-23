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
  <version>2.1.4</version>
</dependency>
```

or

```kotlin
dependency("io.github.daggerok:liquibase-r2dbc-spring-boot-starter:2.1.4")
```

And then use regular Spring Boot + Liquibase setup:

* Create necessary Liquibase migration `src/main/resources/liquibase/master-changelog.xml` file
* In `application.yaml` file add necessary liquibase configuration:
  ```yaml
  spring:
    r2dbc:
      url: 'r2dbc:mysql://127.0.0.1:3306/database'
      username: 'user'
      password: 'password'
    liquibase:
      change-log: classpath*:/liquibase/changelog-master.xml
  logging:
    level:
      io.netty.resolver.dns.DnsServerAddressStreamProviders: off
  ```
* In `pom.xml` add necessary dependencies related to database going to be used:
  * MySQL:
    ```xml
    <dependencies>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>dev.miku</groupId>
            <artifactId>r2dbc-mysql</artifactId>
            <version>0.8.2.RELEASE</version>
            <scope>runtime</scope>
        </dependency>
    <dependencies>
    ```
  * MariaDB:
    ```xml
    <dependencies>
        <dependency>
            <groupId>org.mariadb.jdbc</groupId>
            <artifactId>mariadb-java-client</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.mariadb</groupId>
            <artifactId>r2dbc-mariadb</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.r2dbc</groupId>
            <artifactId>r2dbc-pool</artifactId>
            <scope>runtime</scope>
        </dependency>
    <dependencies>
    ```
  * PostgreSQL:
    ```xml
    <dependencies>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>r2dbc-postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
    <dependencies>
    ```
  * H2:
    ```xml
    <dependencies>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.r2dbc</groupId>
            <artifactId>r2dbc-h2</artifactId>
            <scope>runtime</scope>
        </dependency>
    <dependencies>
    ```

NOTE: If you want to use `*-SNAPSHOT` version, please make sure you have added snapshot maven repository like so

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

### TODO: Future plans

#### TODO: Add more database support

TODO: Implement HyperSQL (HSQL) database example

#### TODO: Implement @WithLiquibaseR2DBC annotation

TODO: Implement `@WithLiquibaseR2DBC` annotation and `WithLiquibaseR2dbcListener` so they can be used,
for example during testing to execute different liquibase migrations separatelly.

Class-level annotation usage:

```kotlin
package my.company

/**
 * This `@WithLiquibaseR2DBC` annotation by default should ask `WithLiquibaseR2dbcListener`
 * to pick and execute class and methods related migrations if they are exists:
 *
 * - classpath:/my/company/UsersRepositoryTests.xml
 * - classpath:/my/company/UsersRepositoryTests.shouldTestUsers.xml
 * - classpath:/my/company/UsersRepositoryTests.shouldTestUserMessages.xml
 *
 * NOTE: migrations are related to class package, name and class method names
 *
 * ie next files:
 *
 * - src/main/kotlin/my/company/UsersRepositoryTests.xml
 * - src/main/resources/my/company/UsersRepositoryTests.xml
 * - src/test/kotlin/my/company/UsersRepositoryTests.xml
 * - src/test/resources/my/company/UsersRepositoryTests.xml
 *
 * - src/main/kotlin/my/company/UsersRepositoryTests.shouldTestUsers.xml
 * - src/main/resources/my/company/UsersRepositoryTests.shouldTestUsers.xml
 * - src/test/kotlin/my/company/UsersRepositoryTests.shouldTestUsers.xml
 * - src/test/resources/my/company/UsersRepositoryTests.shouldTestUsers.xml
 *
 * - src/main/kotlin/my/company/UsersRepositoryTests.shouldTestUserMessages.xml
 * - src/main/resources/my/company/UsersRepositoryTests.shouldTestUserMessages.xml
 * - src/test/kotlin/my/company/UsersRepositoryTests.shouldTestUserMessages.xml
 * - src/test/resources/my/company/UsersRepositoryTests.shouldTestUserMessages.xml
 *
 * Or in this case only specified migrations should be executed by `WithLiquibaseR2dbcListener`
 * before that test classes, for example these two:
 *
 * ```kotlin
 * @WithLiquibaseR2DBC("classpath:/users-test-liquibase.xml", "classpath:/user-messages-test-liquibase.xml")
 * ```
*/
@SpringBootTest
@WithLiquibaseR2DBC
class UsersRepositoryTests(@Autowired val userRepository: UserRepository) {

    @Test
    fun shouldTestUsers() {
        // ...
    }

    @Test
    fun shouldTestUserMessages() {
        // ...
    }
}
```

Method-level annotation usage:

```kotlin
package my.company

@SpringBootTest
class UsersRepositoryTests(@Autowired val userRepository: UserRepository) {

    /**
     * This `@WithLiquibaseR2DBC` annotation should ask `WithLiquibaseR2dbcListener`
     * to appliy specified `classpath:/users-test-liquibase.xml` migration, ie:
     *
     * - src/main/kotlin/users-test-liquibase.xml
     * - src/main/resources/users-test-liquibase.xml
     * - src/test/kotlin/users-test-liquibase.xml
     * - src/test/resources/users-test-liquibase.xml
     */
    @Test
    @WithLiquibaseR2DBC("classpath:/users-test-liquibase.xml")
    fun `should test users`() {
        // ...
    }

    /**
     * In this case with `@WithLiquibaseR2DBC` annotation by default `WithLiquibaseR2dbcListener`
     * should try to execute next migrations:
     *
     * - classpath:/my/company/UsersRepositoryTests.shouldTestUserMessages.xml
     *
     * ie, next files:
     *
     * - src/main/kotlin/my/company/UsersRepositoryTests.shouldTestUserMessages.xml
     * - src/main/resources/my/company/UsersRepositoryTests.shouldTestUserMessages.xml
     * - src/test/kotlin/my/company/UsersRepositoryTests.shouldTestUserMessages.xml
     * - src/test/resources/my/company/UsersRepositoryTests.shouldTestUserMessages.xml
     */
    @Test
    @WithLiquibaseR2DBC
    fun shouldTestUserMessages() {
        // ...
    }
}
```

### Reference documentation

Checkout [documentation](https://daggerok.github.io/liquibase-r2dbc-spring-boot-starter/) for details

ALso check [developer guide for contributors](docs/CONTRIBUTING.md)
