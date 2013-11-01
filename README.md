# RAML Java Parser

This project contains a RAML java parser compatible with version 0.8 of the RAML
specification. The parser depends on SnakeYaml, a Java YAML parser.

## Versioning

The Java RAML parser is versioned in the following manner:

```
x.y.z
```

in which *x.y* denotes the version of the RAML specification
and *z* is the version of the parser.

So *0.1.2* is the 2nd revision of the parser for the *0.1* version
of the RAML specification.

## Usage

### Validation

The validator allows you to check whether a RAML String is valid or not,
and in the case is not valid it provides a List of validation results:

```java
List<ValidationResult> results = RamlValidationService.createDefault().validate(ramlBuffer);
```

### Parsing

The parser returns a Raml object and can be invoked using a String, an InputStream or a Reader:

```java
Raml = new RamlDocumentBuilder().build(ramlBuffer);
```
