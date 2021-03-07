# akka-http-mutual-tls

Akka HTTP with mutual TLS authentication

A basic example of setting up [Akka HTTP](https://doc.akka.io/docs/akka-http/current/index.html) to use [mutual TLS authentication](https://doc.akka.io/docs/akka-http/current/server-side/server-https-support.html#mutual-authentication).

**WARNING This project contains sample preconfigurued keystore including certifications, key pairs, and uses a hardcoded password. Do not use these in production, they are a demo sample only!!!! YOU HAVE BEEN WARNED.**

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

Stop the server by hitting enter in the server terminal.

## HTTPS encryption and server authentication

Now lets enable TLS encryption over HTTPS. In this mode the client will authenticate the server by requesting and validating a certification from the server. The client and server will encrypt the connection using TLS. 

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

Stop the server by hitting enter in the server terminal.

## Mutual authentication

In this mode, the server will additionally request and validate a certificate from the client. The client will still validate the server certificate, and the connection will be encrypted, as before. In this case the client must provide a certificate that the server successfully validates or the connection will not be established.

```
$ export SAMPLE_AKKA_HTTP_REQUIRE_MUTUAL_AUTH=true
$ sbt run
```

resulting in
```
Server online at https://localhost:8443
Using mutual TLS authentication, in addition to server authentication performed by the client, the server requires client to send a certificate for authentication
Press RETURN to stop...
```

Now try to hit the server again using the same curl command as before:
```
$ curl --cacert exampleca.crt https://localhost:8443/status
```

This time, you should see something similar to
```
curl: (35) error:1401E412:SSL routines:CONNECT_CR_FINISHED:sslv3 alert bad certificate
```

We have not told curl what client certificate it needs to present to the server, in order for the server to authenticate the client. The server has indicated it received a bad (in this case missing) certificate. So now do: 
```
$ curl --cert client.crt --key client.key --cacert exampleca.crt https://localhost:8443/status
```

passing curl the client cert and key. Now again we get the desired response
```
Hello from Akka HTTP!!
```

## Conclusion

The certificates and trustore for this demo have been set up according to recipes [here](https://lightbend.github.io/ssl-config/CertificateGeneration.html). The slight modifications required are
1. the server certificate must be generated for CN=localhost instead of CN=example.com in order that the client's authentication of the server (running at localhost) passes.
2. The client CA certificate must be added to the keystore used by the server, in order that the server can validate the client's certificate.






