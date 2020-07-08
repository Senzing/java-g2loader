# java-g2loader

## Synopsis

This is a simple Java application that leverages the Senzing API to allow:
 1. loading CSV/JSON files
 1. purging repositories
 1. exporting results in JSON format
 1. exporting statistics in JSON format
 1. adding data sources
 
## Overview

Command line usage: java -jar g2loader.jar -iniFile <path to ini file> [-dataFile <path to data file>] [-exportToFile <path for export file>] [-statsToFile <path for statistics file>] [-dataSource <name of data source>] [-purge] [-debug]
    
macOS:
  1. Requires a user install of Java and will not work with a system installation
  1. export DYLD_LIBRARY_PATH=<path>/senzing/g2/lib


### Contents

1. [Related artifacts](#related-artifacts)
1. [Expectations](#expectations)
1. [Demonstrate using Command Line Interface](#demonstrate-using-command-line-interface)
    1. [Prerequisites for CLI](#prerequisites-for-cli)
    1. [Download](#download)
    1. [Environment variables for CLI](#environment-variables-for-cli)
    1. [Run command](#run-command)
1. [Develop](#develop)
    1. [Prerequisites for development](#prerequisites-for-development)
    1. [Clone repository](#clone-repository)
1. [Examples](#examples)
    1. [Examples of CLI](#examples-of-cli)
1. [Advanced](#advanced)
    1. [Configuration](#configuration)
1. [Errors](#errors)
1. [References](#references)

## Related artifacts

## Expectations

- **Space:** This repository and demonstration require 6 GB free disk space.
- **Time:** Budget 40 minutes to get the demonstration up-and-running, depending on CPU and network speeds.
- **Background knowledge:** This repository assumes a working knowledge of:
  - Java, mvn

## Demonstrate using Command Line Interface

### Prerequisites for CLI

### Download

### Environment variables for CLI

### Run command

## Develop

### Prerequisites for development

### Clone repository

## Examples

### Examples of CLI

The following examples require initialization described in
[Demonstrate using Command Line Interface](#demonstrate-using-command-line-interface).

## Advanced

### Configuration

## Errors

1. See [docs/errors.md](docs/errors.md).

## References
