# RAML Java Parser
[![Gitter](https://badges.gitter.im/JoinChat.svg)](https://gitter.im/raml-org/raml-java-parser?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

This is a Java parser for [RAML](http://raml.org) version 0.8 as defined in the [0.8 RAML specification](https://github.com/raml-org/raml-spec/blob/master/raml-0.8.md) The parser depends on SnakeYAML, a Java YAML parser.

The version of this parser supporting RAML 1.0 is in the [master branch](https://github.com/raml-org/raml-java-parser/tree/master).

## Build

### JAR file without dependencies

```mvn clean package```

### JAR file with dependencies

```mvn clean package -P jar-with-dependencies```

**Run standalone validator**

```java -jar raml-parser-{version}.jar raml-file ...```

## System properties
In order to provide more flexibility, users can set different system properties when parsing different RAML files. Here we list all the system properties you can use right now:

Argument | Description | Default Value
-------- | ----------- | -------------
```raml.parser.encoding```|	Defines the charset being used by the parser| ```UTF-8```
```raml.verifyRaml```|Verify the RAML file for YAML reference abuses | `true`
```raml.verifyReferenceCycle```|Specifically verify YAML reference cycles| `true`
```raml.maxDepth```|Limit depth of YAML references | `2000`
```raml.maxReferences```|Limit number of YAML references in expansions|`10000`

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
