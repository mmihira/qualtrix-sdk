# Qualtrix-Sdk

`qualtrix-sdk` is the qualtrix sdk in the java programming language. Currently only a subset of the API endpoints have been
implemented. This library is designed to be used with spring-rest-template or spring-webclient. [maven link](https://mvnrepository.com/artifact/io.github.mmihira/qualtrix-sdk)

## Getting Started

### Usage

#### Rest Template
```java
    var restTemplate = new RestTemplate();
    var client = QualtrixRestTemplateClient("QUALTRIX_API_KEY", restTemplate);
    client.whoAmI();
```

#### Webclient
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

## License

This SDK is distributed under the Apache License, Version 2.0, see LICENSE.txt and NOTICE.txt for more information.
