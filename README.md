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

## Build

### JAR file without dependencies

```mvn clean package```

### JAR file with dependencies

```mvn clean package -P jar-with-dependencies```

**Run standalone validator**

```java -jar raml-parser-{version}.jar raml-file ...```

## Usage

### Validation

The validator allows you to check whether a RAML resource is valid or not,
and in the case is not valid it provides a List of validation results:

```java
List<ValidationResult> results = RamlValidationService.createDefault().validate(ramlLocation);
```

### Parsing

The parser returns a Raml object and can be invoked using a String with the RAML file location:

```java
Raml raml = new RamlDocumentBuilder().build(ramlLocation);
```

### Emitter

If you do any change to the Raml object model and you want to get the updated RAML descriptor
you can use RamlEmitter class:

```java
Raml raml = new RamlDocumentBuilder().build(ramlLocation);

// modify the raml object

RamlEmitter emitter = new RamlEmitter();
String dumpFromRaml = emitter.dump(raml);
```
