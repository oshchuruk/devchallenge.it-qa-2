# devchallenge.it-qa-2
This is a sample test project for a [OpenWeatherMap Weather API](https://openweathermap.org/api).

## Prerequisites

To run this project you need to have Java and Apache Maven installed on your computer. The manuals on how to do it are stored on the [Java website](https://www.java.com/en/download/help/download_options.xml) the [Apache Project website](https://maven.apache.org/install.html). 

Make sure that JAVA_HOME is set as the enviromental variable and that Maven is set to your PATH environment variable.

## Running the project

1. Open the project's folder in your terminal or in a command prompt.

2. Run the following command. It will download all the external libraries and run the tests. You will see the results in your terminal or in a command prompt after all tests are run.

```
mvn package
```

3. After that you will be able to run the tests running the other command.

```
mvn test
```

Alternatively you could run the tests from you IDE (e.g. Intellij IDEA) after building the project as a Maven Project.
