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

If you want to use `*-SNAPSHOT` version, please make sure you have added snapshot maven repository like so

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

Otherwise, use only released version. See [repo1.maven.org](https://repo1.maven.org/maven2/io/github/daggerok/liquibase-r2dbc-spring-boot-starter/) repository

## Configure

Update `src/main/resources/application.yml` file:

```yaml
spring:
  r2dbc:
    url: 'r2dbc:mysql://127.0.0.1:3306/database'
    username: 'user'
    password: 'password'
  liquibase:
    change-log: classpath*:/liquibase/changelog-master.xml
```

## Add liquibase migrations

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

## Test with Testcontainers

To simplify developer workflow and infrastructure setup, use testcontainers with help of next Abstract test class:

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

## Contributing

Feel free to [contribute](https://github.com/daggerok/liquibase-r2dbc-spring-boot-starter#developer-guide-for-contributors)!
