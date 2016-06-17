# Java RAML 1.0 Parser <sup>(beta)</sup>

See http://raml.org for more information about RAML.

This parser is in beta state of development.


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


