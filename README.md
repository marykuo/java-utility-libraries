# java-utility-libraries
a repository that contains multiple JAR utility libraries

## Features

- storage: File I/O operations in local storage.
- time: Utilities for `java.time` API.

## Usage

### Deploy

To deploy the JAR files, use Maven commands as described below.

```
mvn clean deploy
```

- To deploy all modules, run the command from the root directory of the **project**:
  - Example: `C:\Users\marykuo\workspace\java-utility-libraries`
- To deploy a single module, run the command from the root directory of the **module**:
  - Example: `C:\Users\marykuo\workspace\java-utility-libraries\storage`

### Test

To run all tests in the project, use the following command:

```
mvn clean test
```
