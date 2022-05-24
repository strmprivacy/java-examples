# STRM Privacy Java Driver Examples

## Need help?

See our
[documentation](https://docs.strmprivacy.io/docs/latest/quickstart/full-example/) for
documentation on how to configure streams. Or [reach out to
us](https://docs.strmprivacy.io/docs/latest/contact/index.html).

### Using gradle and Jdk 9?

See [this discussion](https://github.com/jetty-project/jetty-alpn/issues/15)

Add the following section to the `dependencies` block of your Gradle based application.

```
implementation "io.strmprivacy:java-driver:$strmPrivacyJavaDriverVersion"
implementation "io.strmprivacy.schemas:demo-avro:$strmPrivacySchemasClickstreamVersion"
// this dependency is normally added in the Maven build process
// but since Gradle doesn't pick this up, it must be added manually
implementation "org.eclipse.jetty:jetty-alpn-java-client:9.4.38.v20210224"
```

