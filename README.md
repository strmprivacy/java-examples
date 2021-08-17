# Stream Machine Java Driver Examples

## Need help?

See our
[documentation](https://docs.streammachine.io/quickstart/full-example.html) for
documentation on how to configure streams. Or [reach out to
us](https://docs.streammachine.io/docs/latest/contact/index.html).

### Using gradle and Jdk 9?

See [this discussion](https://github.com/jetty-project/jetty-alpn/issues/15)

Add the following section to the `dependencies` block of your Gradle based application.

```
implementation "io.streammachine:java-driver:$streamMachineJavaDriverVersion"
implementation "io.streammachine.schemas:demo-avro:$streamMachineSchemasClickstreamVersion"
// this dependency is normally added in the Maven build process
// but since Gradle doesn't pick this up, it must be added manually
implementation "org.eclipse.jetty:jetty-alpn-java-client:9.4.38.v20210224"
```

