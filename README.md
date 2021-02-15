# Qualtrix-Sdk

`qualtrix-sdk` is the qualtrix sdk in the java programming language. Currently only a subset of the API endpoints have been
implemented. This library is designed to be used with spring-webclient. [maven link](https://mvnrepository.com/artifact/io.github.mmihira/qualtrix-sdk)

## Getting Started

### Usage

```java
    var webClient = WebClient.create();
    var client = QualtrixWebFluxClient("QUALTRIX_API_KEY", webClient);
    client.whoAmI().block();
```

### Install

#### Install using maven
```
  <dependency>
    <groupId>io.github.mmihira</groupId>
    <artifactId>qualtrix-sdk</artifactId>
    <version>0.1.0</version>
    <type>pom</type>
  </dependency>
```

#### Install using gradle
```
    implementation 'io.github.mmihira:qualtrix-sdk:0.1.0'
```

### Publish

- In `gradle.properties` have the following properties set
```
signing.keyId=<>
signing.password=<>
signing.secretKeyRingFile=<>

sonatypeUsername=mmihira
sonatypePassword=<>
```
- In `build.gradle` set the updated version, tag and push to upstream.
- Run `./gradlew publish` in project dir
- Go to `https://oss.sonatype.org/#stagingRepositories`. Login if necessary
- Select the repository that was uploaded and `close` it.
- Then `Release` it once the closing process has finished.



## License

This SDK is distributed under the Apache License, Version 2.0, see LICENSE.txt and NOTICE.txt for more information.
