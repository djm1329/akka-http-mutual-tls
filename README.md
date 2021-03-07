# akka-http-mutual-tls

Akka HTTP with mutual TLS authentication

A basic example of setting up [Akka HTTP](https://doc.akka.io/docs/akka-http/current/index.html) to use [mutual TLS authentication](https://doc.akka.io/docs/akka-http/current/server-side/server-https-support.html#mutual-authentication).

**Warning** This project contains sample preconfigurued truststore including certifications, key pairs, and uses a hardcoded password. Do not use these in production, they are a demo sample only!!!! You have been warned.

## Plain HTTP with no encryption or authentication

Out of the box the app runs a basic unencrypted HTTP server on port 8080. Navigate to the project's top-level directory and run:
```bash
sbt run
```
should result in 
```
Server online at http://localhost:8080

Press RETURN to stop...
```
Use curl from another terminal to check it works:
```
curl http://localhost:8080/status
```
should answer
```
Hello from Akka HTTP!!
```




