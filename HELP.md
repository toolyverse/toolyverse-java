# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.6/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.5.6/maven-plugin/build-image.html)
* [Distributed Tracing Reference Guide](https://docs.micrometer.io/tracing/reference/index.html)
* [Getting Started with Distributed Tracing](https://docs.spring.io/spring-boot/3.5.6/reference/actuator/tracing.html)
* [Spring Boot Testcontainers support](https://docs.spring.io/spring-boot/3.5.6/reference/testing/testcontainers.html#testing.testcontainers)
* [Testcontainers Kafka Modules Reference Guide](https://java.testcontainers.org/modules/kafka/)
* [Testcontainers MySQL Module Reference Guide](https://java.testcontainers.org/modules/databases/mysql/)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/3.5.6/reference/actuator/index.html)
* [Spring Batch](https://docs.spring.io/spring-boot/3.5.6/how-to/batch.html)
* [Spring Cache Abstraction](https://docs.spring.io/spring-boot/3.5.6/reference/io/caching.html)
* [OpenFeign](https://docs.spring.io/spring-cloud-openfeign/reference/)
* [Resilience4J](https://docs.spring.io/spring-cloud-circuitbreaker/reference/spring-cloud-circuitbreaker-resilience4j.html)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/3.5.6/specification/configuration-metadata/annotation-processor.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.5.6/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Data Redis (Access+Driver)](https://docs.spring.io/spring-boot/3.5.6/reference/data/nosql.html#data.nosql.redis)
* [Apache Freemarker](https://docs.spring.io/spring-boot/3.5.6/reference/web/servlet.html#web.servlet.spring-mvc.template-engines)
* [Spring for Apache Kafka](https://docs.spring.io/spring-boot/3.5.6/reference/messaging/kafka.html)
* [Java Mail Sender](https://docs.spring.io/spring-boot/3.5.6/reference/io/email.html)
* [OAuth2 Client](https://docs.spring.io/spring-boot/3.5.6/reference/web/spring-security.html#web.security.oauth2.client)
* [Prometheus](https://docs.spring.io/spring-boot/3.5.6/reference/actuator/metrics.html#actuator.metrics.export.prometheus)
* [Quartz Scheduler](https://docs.spring.io/spring-boot/3.5.6/reference/io/quartz.html)
* [Spring Security](https://docs.spring.io/spring-boot/3.5.6/reference/web/spring-security.html)
* [Spring gRPC](https://docs.spring.io/spring-grpc/reference/index.html)
* [Testcontainers](https://java.testcontainers.org/)
* [Thymeleaf](https://docs.spring.io/spring-boot/3.5.6/reference/web/servlet.html#web.servlet.spring-mvc.template-engines)
* [Validation](https://docs.spring.io/spring-boot/3.5.6/reference/io/validation.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.5.6/reference/web/servlet.html)
* [WebSocket](https://docs.spring.io/spring-boot/3.5.6/reference/messaging/websockets.html)

### Guides

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
* [Creating a Batch Service](https://spring.io/guides/gs/batch-processing/)
* [Caching Data with Spring](https://spring.io/guides/gs/caching/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Messaging with Redis](https://spring.io/guides/gs/messaging-redis/)
* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Handling Form Submission](https://spring.io/guides/gs/handling-form-submission/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Using WebSocket to build an interactive web application](https://spring.io/guides/gs/messaging-stomp-websocket/)

### Additional Links

These additional references should also help you:

* [Declarative REST calls with Spring Cloud OpenFeign sample](https://github.com/spring-cloud-samples/feign-eureka)
* [Various sample apps using Spring gRPC](https://github.com/spring-projects/spring-grpc/tree/main/samples)

### Testcontainers support

This project
uses [Testcontainers at development time](https://docs.spring.io/spring-boot/3.5.6/reference/features/dev-services.html#features.dev-services.testcontainers).

Testcontainers has been configured to use the following Docker images:

* [`apache/kafka-native:latest`](https://hub.docker.com/r/apache/kafka-native)
* [`mysql:latest`](https://hub.docker.com/_/mysql)
* [`redis:latest`](https://hub.docker.com/_/redis)

Please review the tags of the used images and set them to the same as you're running in production.

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the
parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

