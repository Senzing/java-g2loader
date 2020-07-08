# java-g2loader

## Overview

This is a simple Java application that leverages the Senzing API to allow:
 1. loading CSV/JSON files
 1. purging repositories
 1. exporting results in JSON format
 1. exporting statistics in JSON format
 1. adding data sources

## Setup and building

### Dependencies

To build the java-g2loader you will need Apache Maven (recommend version 3.6.1 or later)
as well as OpenJDK version 11.0.x (recommend version 11.0.6+10 or later).

You will also need the Senzing `g2.jar` file installed in your Maven repository.
In order to install `g2.jar` you must:

1. Install G2 and create a project
    1. Linux: https://senzing.zendesk.com/hc/en-us/articles/115002408867-Quickstart-Guide
    1. macOS and Windows: Contact support@senzing.com
    
1. Locate the `g2.jar` file and set the `${SENZING_PROJECT_DIR}` variable
    1. The `g2.jar` will be in the lib directory in the senzing project

1. Install the `g2.jar` file in your local Maven repository, replacing the
   `${SENZING_PROJECT_DIR}`variable as determined above:

    1. Linux and MacOS:

        mvn install:install-file \
            -Dfile=${SENZING_PROJECT_DIR}/lib/g2.jar \
            -DgroupId=com.senzing \
            -DartifactId=g2 \
            -Dversion=unknown \
            -Dpackaging=jar
        ```

    1. Windows:


        mvn install:install-file \
            -Dfile="%SENZING_PROJECT_DIR%\lib\g2.jar" \
            -DgroupId=com.senzing \
            -DartifactId=g2 \
            -Dversion="unknown" \
            -Dpackaging=jar
        ```

### Building

To build simply execute:

```console
mvn install
```

## Running

Before running the Risk Scoring Calculator you need to set up the environment for G2

### Setup

1. Linux

Follow the instructions in the QuickStart above and source the setupEnv
    
1. macOS

NOTE: Requires a user install of Java and will not work with a system installation

    ```console
    export DYLD_LIBRARY_PATH=${SENZING_PROJECT_DIR}/lib
    ```


1. Windows

    ```console
    set Path=%SENZING_PROJECT_DIR%\lib;%Path%
    ```

### Parameters

A few pieces of information are needed for running the application.  They will become parameters on the command line.

1. Path of the G2 ini file is the only required parameter.  The parameter is -iniFile.

### Command

The command for running the application is

```console
java -jar g2loader.jar -iniFile <path to ini file> [-dataFile <path to data file>] [-exportToFile <path for export file>] [-statsToFile <path for statistics file>] [-dataSource <name of data source>] [-purge] [-debug]
```
