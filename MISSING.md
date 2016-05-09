## RAML 1.0 unsupported features

- `uses` key is only supported for root RAML documents and libraries
- `annotations` are supported inside maps, not scalars yet
- Yaml types validation against xml examples


## Parser API navigation gaps

Though the parser may support and validate certain features, there's no way to fully navigate them through the API at the moment:

- Libraries
- Resource Types
- Traits
- Types
- Annotations
