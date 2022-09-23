---
---

# Liquibase with Spring Boot R2DBC [![Maven Central](https://img.shields.io/maven-central/v/io.github.daggerok/liquibase-r2dbc-spring-boot-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.daggerok%22%20AND%20a:%22liquibase-r2dbc-spring-boot-starter%22)
This [library](https://github.com/daggerok/liquibase-r2dbc-spring-boot-starter) can help you use good old liquibase database migration within reactive spring-boot R2DBC projects

Status: [![tests](https://github.com/daggerok/liquibase-r2dbc-spring-boot-starter/actions/workflows/tests.yml/badge.svg)](https://github.com/daggerok/liquibase-r2dbc-spring-boot-starter)

[[toc]]

## Install

Update maven `pom.xml` file:

```xml:no-v-pre:no-line-numbers
<dependency>
    <groupId>io.github.daggerok</groupId>
    <artifactId>liquibase-r2dbc-spring-boot-starter</artifactId>
    <version>{{ $site.version }}</version>
</dependency>
```

Update gradle `build.gradle(.kts)` file:

```java:no-v-pre:no-line-numbers
dependency("io.github.daggerok:liquibase-r2dbc-spring-boot-starter:{{ $site.version }}")
```

NOTE: Released version located on [repo1.maven.org](https://repo1.maven.org/maven2/io/github/daggerok/liquibase-r2dbc-spring-boot-starter/) repository

## Supported databases

Necessary dependencies related to database is going to be used.
Do not forget add yours:

### MySQL

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
        <version>${r2dbc-mysql.version}</version>
        <scope>runtime</scope>
    </dependency>
<dependencies>
```

### MariaDB

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

### PostreSQL

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

### H2

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

```xml:no-v-pre:no-line-numbers
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

```java:no-v-pre:no-line-numbers
repositories {
    maven {
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}
```

See [s01.oss.sonatype.org](https://s01.oss.sonatype.org/content/repositories/snapshots/io/github/daggerok/) repository

## MySQL example

### Install MySQL + R2DBC

Update `pom.xml` file:

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
        <version>${r2dbc-mysql.version}</version>
        <scope>runtime</scope>
    </dependency>
<dependencies>
```

### Configure Spring Boot

Update `src/main/resources/application.yml` file:

```yaml
spring:
  r2dbc:
    url: 'r2dbc:mysql://127.0.0.1:3306/database'
    username: 'user'
    password: 'password'
  liquibase:
    change-log: classpath*:/db/changelog/db.changelog-master.xml
```

### Add Liquibase migrations

Create `src/main/resources/db/changelog/db.changelog-master.xml` file with for example next content:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:ext="http://www.liquibase.org/xml/ns/dbchangelog-ext"
        xmlns:pro="http://www.liquibase.org/xml/ns/pro"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                            https://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.9.xsd
		                    http://www.liquibase.org/xml/ns/dbchangelog-ext
		                    https://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd
		                    http://www.liquibase.org/xml/ns/pro https://www.liquibase.org/xml/ns/pro/liquibase-pro-4.9.xsd">

    <!-- MySQL data types: https://xenovation.com/blog/development/java/java-professional-developer/liquibase-related-sql-java-types -->
    <property name="current.timestamp.function" dbms="mysql" value="CURRENT_TIMESTAMP"/>
    <property name="timestamp.type" dbms="mysql" value="TIMESTAMP"/>
    <property name="long.datetime.type" dbms="mysql" value="DATETIME(6)"/>
    <property name="datetime.type" dbms="mysql" value="DATETIME"/>
    <property name="date.type" dbms="mysql" value="DATE"/>
    <property name="time.type" dbms="mysql" value="TIME"/>
    <property name="decimal.type" dbms="mysql" value="DECIMAL(19,8)"/>
    <property name="double.type" dbms="mysql" value="DOUBLE"/>
    <property name="boolean.type" dbms="mysql" value="BOOLEAN"/>
    <property name="blob.type" dbms="mysql" value="LONGBLOB"/>
    <property name="longtext.type" dbms="mysql" value="LONGTEXT"/>
    <property name="text.type" dbms="mysql" value="TEXT"/>
    <property name="id.type" dbms="mysql" value="BIGINT"/>

    <!-- Migrations -->
    <include file="classpath*:/db/changelog/V202206022344-create-table-messages.xml"/>

</databaseChangeLog>
```

And finally add `src/main/resources/db/changelog/V202206022344-create-table-messages.xml` migration file:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:ext="http://www.liquibase.org/xml/ns/dbchangelog-ext"
        xmlns:pro="http://www.liquibase.org/xml/ns/pro"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                            https://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.9.xsd
		                    http://www.liquibase.org/xml/ns/dbchangelog-ext
		                    https://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd
		                    http://www.liquibase.org/xml/ns/pro https://www.liquibase.org/xml/ns/pro/liquibase-pro-4.9.xsd">

    <changeSet id="V202206022344" author="Maksim Kostromin">
        <comment>Create `messages` table and `messages_sent_at_idx` index with rollback</comment>

        <createTable tableName="messages">
            <column name="body" type="${text.type}"/>
            <column name="address_to" type="VARCHAR(255)"/>
            <column name="address_from" type="VARCHAR(255)"/>
            <column name="sent_at" type="${timestamp.type}" defaultValueComputed="${current.timestamp.function}"/>
            <column autoIncrement="true" name="id" type="${id.type}">
                <constraints primaryKey="true" primaryKeyName="messages_pk"/>
            </column>
        </createTable>

        <createIndex tableName="messages"
                     indexName="messages_sent_at_idx">
            <column name="sent_at"/>
        </createIndex>

        <rollback>
            <dropIndex tableName="messages"
                       indexName="messages_sent_at_idx"/>

            <dropTable tableName="messages"/>
        </rollback>
    </changeSet>

</databaseChangeLog>
```

### Test with Testcontainers

To simplify developer workflow and infrastructure setup, use testcontainers:

```xml
    <dependencies>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>mysql</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
```

...with help of next Abstract test class:

```kotlin{14-16}
@Testcontainers
@SpringBootTest
abstract class AbstractTestMySQLContainerTest {

    companion object {
        data class TestMySQLContainer(val image: String = "mysql:8.0.24") : MySQLContainer<TestMySQLContainer>(image)

        @Container
        val mysqlContainer: TestMySQLContainer = TestMySQLContainer().withDatabaseName("database")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url") { mysqlContainer.jdbcUrl.replaceFirst("jdbc:", "r2dbc:") }
            registry.add("spring.r2dbc.username", mysqlContainer::getUsername)
            registry.add("spring.r2dbc.password", mysqlContainer::getPassword)
        }
    }
}
```

Usage:

```kotlin
class MysqlApplicationTest(@Autowired val databaseClient: DatabaseClient) : AbstractTestMySQLContainerTest() {

    @Test
    fun `should test with database client`() {
        databaseClient.sql { " SELECT 1 AS result ; " }
            .fetch().one()
            .test()
            .consumeNextWith {
                it.forEach { (column, value) ->
                    log.info { "$column: $value" }
                }
            }
            .verifyComplete()
    }

    companion object {
        val log = logger()
    }
}
```

## Technology Stack

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

## Contributing

Feel free to [contribute](https://github.com/daggerok/liquibase-r2dbc-spring-boot-starter#developer-guide-for-contributors)!

Also check [developer guide](./CONTRIBUTING.md)
