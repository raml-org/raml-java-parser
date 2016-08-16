# RAML Java Parser
[![Gitter](https://badges.gitter.im/Join Chat.svg)](https://gitter.im/raml-org/raml-java-parser?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

This is a Java implementation of a [RAML](http://raml.org) parser for versions [1.0](http://raml.org/raml-10-spec) and [0.8](http://raml.org/raml-08-spec).
The parser depends on SnakeYaml, a Java YAML parser.

The old version that only support RAML 0.8 is still available [here](https://github.com/raml-org/raml-java-parser/tree/v1).

See http://raml.org for more information about RAML.


## Build

### JAR file without dependencies

```mvn clean package```

### JAR file with dependencies

```mvn clean package -P jar-with-dependencies```

**Run standalone validator**

```java -jar raml-parser-2-{version}.jar raml-file ...```


## Usage

```java
RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(input);
if (ramlModelResult.hasErrors())
{
    for (ValidationResult validationResult : ramlModelResult.getValidationResults())
    {
        System.out.println(validationResult.getMessage());
    }
}
else
{
    Api api = ramlModelResult.getApiV10();

}
```
