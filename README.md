# Simple Blockchain Implementation in Kotlin

## Prerequisites

- Java JDK 1.8
- PostgreSQL

## Configuration
- Open `src/main/resources/application.conf`
- Set the `blockChainConfig.blockStorage` to `db`
- Set PostgreSQL Credentials in `postgresDatabase` block 
- Save


## Run Command
### IntelliJ IDEA
For the best experience open this project in IntelliJ IDEA and run `Main.kt`
``
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