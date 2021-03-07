# akka-http-mutual-tls

Akka HTTP with mutual TLS authentication

A basic example of setting up [Akka HTTP](https://doc.akka.io/docs/akka-http/current/index.html) to use [mutual TLS authentication](https://doc.akka.io/docs/akka-http/current/server-side/server-https-support.html#mutual-authentication).

**Warning** This project contains sample preconfigurued truststore including certifications, key pairs, and uses a hardcoded password. Do not use these in production, they are a demo sample only!!!! You have been warned.

## Plain HTTP with no encryption or authentication

Out of the box the app runs a basic unencrypted HTTP server on port 8080. Navigate to the project's top-level directory and run:
```
$ sbt run
```

should result in 
```
Server online at http://localhost:8080

Press RETURN to stop...
```

Use curl from another terminal to check it works:
```
$ curl http://localhost:8080/status
```

should answer
```
Hello from Akka HTTP!!
```

Stop the server by hitting enter in the server temrinal 

## HTTPS encryption and server authentication

Now lets enable TLS encryption over HTTPS. In this mode the client will authenticate the server by requesting and validating a certification from the server.

```
$ export SAMPLE_AKKA_HTTP_ENABLE_TLS=true
$ sbt run
```

Now you should get:
```
Server online at https://localhost:8443
Using server TLS authentication only, client requires server to send a certificate for authentication
Press RETURN to stop...
```

Notice the server protocol has changed to https, and the port is now 8443. Try to curl the new server instance:
```
$ https://localhost:8443/status
```

you will get a response similar to
```
curl: (60) SSL certificate problem: unable to get local issuer certificate
More details here: https://curl.haxx.se/docs/sslcerts.html

curl failed to verify the legitimacy of the server and therefore could not
establish a secure connection to it. To learn more about this situation and
how to fix it, please visit the web page mentioned above.
```

We have not provided a way for curl to verify the certificate provided by the server. Let's do that:
```
$ curl --cacert exampleca.crt https://localhost:8443/status
```

and now we should see expected response:
```
Hello from Akka HTTP!!
```




