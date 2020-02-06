# qualtrix-sdk

`Qualtrix-sdk` is the qualtrix sdk in the java programming language. Currently only a subset of the API endpoints have been
implemented. This library is designed to be used with spring-rest-templates or spring-webclient.

## Getting Started

### Install using maven
### Install using gradle

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

## License

This SDK is distributed under the Apache License, Version 2.0, see LICENSE.txt and NOTICE.txt for more information.
