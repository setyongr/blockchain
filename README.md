# Simple Blockchain Implementation in Kotlin

## Prerequisites

- Java JDK 1.8
- PostgreSQL

## Configuration
- Open `src/main/resources/application.conf`
- Set PostgreSQL Credentials in `postgresDatabase` block 
- Save


## Run Command

Default Port is 8080

You can change the port in `src/main/resources/application.conf`


### Linux/Unix/MacOS
```
$ ./gradlew run
```
### Windows
```
$ gradlew.bat run
```

## Build Fat JAR
You need to build `.jar` file to deploy the application
```
$ ./gradlew shadowJar
```

The .jar file will be stored in `build/libs`

Your can run the `.jar` file with

```
$ java -jar filename.jar
```

## API Docs
[See here](API.md)