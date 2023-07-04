# Objective
The purpose of this project is to auto generate Backstage's catalog.yml from OpenAPI(OAS) 3.x document. 
The mapping between the elements  of OpenAPI's document and Backstage's catalog.yml is driven by rules.yml. A sample rules.yml is provided under "resources folder" in the project

## rules.yml structure
The basic atom for data mapping between OAS and catalog is shown below
```
- mappingName: apiName
  source: info.title
  target: metadata.name
  rule:
    - NoWhitespace
    - CopyField
```

### mappingName : Human readable yaml string to describe the mapping name. In this case the mappingName is called apiName, because the mapping copies OAS's info.title to Catalog.yml metadata.name , which is used by Backstage to display API Name

### source: 

+ The [YAML Path](https://github.com/yaml-path/YamlPath) in the [OAS document](https://learn.openapis.org/specification/)
+ Or if the value of source begins with `env.`, it will look for the value in Spring Boot's [Environment](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/env/Environment.html)

### target: The [YAML Path](https://github.com/yaml-path/YamlPath) in the [Catalog document](https://backstage.io/docs/features/software-catalog/descriptor-format/)

### rule: Its an array type that contains rules which would be executed sequentially. Currently only two rules are supported:

+ NoWhitespace
+ CopyField

#### NoWhitespace rule will replace all white space in `source` with `-`. So if the source contains text `this is cat`, it will be replaced with `this-is-cat`
#### CopyField rule copies the `source` into the target catalog.yml at the `target` location.

Thus using the above example, the mapping would do the following:

+ Copy `info.title` from OAS document. We will call it source.
+ Apply rules sequentially:
  +   Replace any white spaces in source with `-`
  +   Create a catalog.yml entry for the `target field: metadata.name`  with the value of the source


 ### Example of mapping where value is read from environment
 ```
 - mappingName: repoLocation
   source: env.repoLocation
   target: spec.definition.$text
   rule:
    - CopyField
```
Here is an example, where i am passing repoLocation via commmand line
```
java -jar target\demo-0.0.1-SNAPSHOT.jar --repoLocation=http://anywhereinfo.github.com/openapi/angrycats.yml --oasFile="D:\temp\angrycats.yml"
```
## Usage
Jar expects two parameters as shown below
+ repoLocation: This should be the location where Backstage can retrieve  your OAS document. This shouuld be absolutle file name including your OAS file name
+ oasFile: The location of your local OAS file. It should be absoulte path, including the OAS file name. It is used by the software in following ways
   + The backstage file is created in the same directory as `oasFile`. The catalog file would be named as backstage-{openapi document name}
   + This local file is read as the source file for all OAS specific attributes spcified in  `source`

With following usage:

```
java -jar target\demo-0.0.1-SNAPSHOT.jar --repoLocation=http://anywhereinfo.github.com/openapi/angrycats.yml --oasFile="D:\temp\angrycats.yml"
```
+ The OAS document located at `"D:\temp\angrycats.yml"` will be read. The attributes specified as `source` will be read.
+ After executing all the rules specified in rules.yml, the resultant file will be saved in `D:\temp` and will be called `D:\temp\backstage-angrycats.yml"`