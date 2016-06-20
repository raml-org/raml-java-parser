# RAML Java Parser
[![Gitter](https://badges.gitter.im/Join Chat.svg)](https://gitter.im/raml-org/raml-java-parser?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

This is a Java parser for [RAML](http://raml.org) version 0.8 as defined in the [0.8 RAML specification](https://github.com/raml-org/raml-spec/blob/master/raml-0.8.md) The parser depends on SnakeYaml, a Java YAML parser.

A newer [version](https://github.com/raml-org/raml-java-parser/tree/v2) is now available as a beta. It supports RAML 1.0 as well as RAML 0.8.

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
