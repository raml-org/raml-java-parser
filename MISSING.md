## RAML 1.0 features gap

This list covers the current gap of features with the RAML 1.0 RC2 specification:

#### `uses` key is only supported for root RAML documents and libraries
 The [spec](https://github.com/raml-org/raml-spec/blob/raml-10/versions/raml-10/raml-10.md#applying-libraries) allows using `uses` in any fragment. At this moment the parser only allows its use in root level RAML documents and libraries.

#### `annotations` are no supported for scalar fields

The [spec](https://github.com/raml-org/raml-spec/blob/raml-10/versions/raml-10/raml-10.md#annotating-scalar-valued-nodes) allows applying annotations to scalar nodes besides certain mapping nodes. Annotations at scalar level are not supported.

#### Overlay application is not enforcing all the required restrictions

The extra restrictions that Overlays enforce vs. Extensions are not being validated.

#### Security Schemes gaps

- OAuth 1.0 `signatures` setting is not supported.
- OAuth 2.0 `authorizationUri` is always optional, no matter which authorization grants are used

#### XML examples validation against YAML types

Validation of XML examples against types defined in YAML are not supported. XML validations are only supported for schemas defined in XSD format.

#### Type system gaps

- inheritance of primitive types
- `discriminator` field
- examples nested in a `value` field
- Xml facet field
- additional properties
- `null` type
- properties named with trailing question marks

## Parser API navigation gaps

The features in the following list cannot be fully navigated through the API at the moment:

- Resource Types
- Traits
- Types
