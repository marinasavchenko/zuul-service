## **Zuul service**

Services gateway.
The service client talks only to a single URL managed by the service gateway.
The service gateway determines what service the service client is trying to invoke.

## **Building**

1. To compile source code and build Docker image:
```
mvn clean package docker:build
```

## **Running**

1. To start service in Docker container:
```
docker run marinasavchenko/onlinestore-zuulsrv:v1
```

## **Running the tests**

1.To run tests via Maven:
```
mvn clean test
```

## **Technology stack**

* Java
* Spring Boot
* Spring Cloud
* Netflix Zuul

* Maven
* Docker