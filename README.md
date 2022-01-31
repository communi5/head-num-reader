# Reading of Head Numbers

## Introduction
This is Java example to demonstrate how you can read all head numbers in a C5 system using REST-API.
This functionality is available since release 7.3 of C5.

When you provide the required parameters the application will make REST request(s) to read head numbers 
and will write each number with the ported status information to the standard console output.

## Prerequisites

JDK 11 (OpenJDK 11) is required for the build and JRE 11 for the execution of the application.

## Build

The project will be build with Gradle using the application plugin.

You have two options to build a distribution package when you want to transfer it to other host:
* ./gradlew clean distTar
* ./gradlew clean distZip

This way you will create a TAR or ZIP file in the directory build/distributions with following structure after extraction:

```
./head-num-reader-$version
        ├──bin (contains head-num-reader and head-num-reader.bat execution scripts for Linux and Windows)
        ├──lib (contains all JAR libraries required for the execution)
```

You can invoke also
```shell
./gradlew clean installDist
```
to create the above structure under ./build/install directory.

## C5 Prerequisites

Following information is required for the execution:
* C5 system account (login name and password)
* C5 connection URI
* C5 Application ID required for authentication

## Usage

We assume execution from the ./build/install directory.

When you run the execution script
```shell
./build/install/head-num-reader/bin/head-num-reader 
```
you will get following usage message:
```
com.beust.jcommander.ParameterException: The following options are required: [-app], [-p]
        at com.beust.jcommander.JCommander.validateOptions(JCommander.java:395)
        at com.beust.jcommander.JCommander.parse(JCommander.java:364)
        at com.beust.jcommander.JCommander.parse(JCommander.java:342)
        at com.communi5.c5.example.HeadNumReader.main(HeadNumReader.java:28)
Usage: <main class> [options]
  Options:
  * -app
      C5 application ID to be set in request header (X-C5-Application)
    -l
      Limit of maximal entries the server should return (page size)
      Default: 5000
    -num
      Phone number to search (with possible '%' and/or '_' as wildcard)
  * -p
      Admin password
    -u
      Admin user (login name)
      Default: admin@defaultdomain
    -uri
      C5 Provisioning base URI (path not included)
      Default: http://localhost:8080/
```

All options marked with asterisk (*) are mandatory.

Here is an example for searching of all head numbers starting with 9.

```shell
./build/install/head-num-reader/bin/head-num-reader -uri http://10.220.32.120:8080/ -app 80aac77e-5e8e-1030-9451-dbd28d49c70b -num 9% -p
Admin password: 
rc=200
got: 21 entries
91100,NOT_PORTED
91200,NOT_PORTED
965103,NOT_PORTED
965220900900,NOT_PORTED
965223344,NOT_PORTED
987112123123,NOT_PORTED
99188881,NOT_PORTED
993232324,NOT_PORTED
99887500,NOT_PORTED
998875011001,NOT_PORTED
998875021002,NOT_PORTED
998875031003,NOT_PORTED
998875041004,NOT_PORTED
998875051005,NOT_PORTED
998875061006,NOT_PORTED
998875071007,NOT_PORTED
998875081008,NOT_PORTED
998875091009,NOT_PORTED
998875101010,NOT_PORTED
99898900,NOT_PORTED
99909000100,NOT_PORTED
Total numbers: 21

```

Here is an example for searching of an exact number 998875011001:
```shell
./build/install/head-num-reader/bin/head-num-reader -uri http://10.220.32.120:8080/ -app 80aac77e-5e8e-1030-9451-dbd28d49c70b -num 998875011001 -p
Admin password: 
rc=200
got: 1 entries
998875011001,NOT_PORTED
Total numbers: 1

```

Here is an example for listing of all numbers (output was abbreviated):
```shell
./build/install/head-num-reader/bin/head-num-reader -uri http://10.220.32.120:8080/ -app 80aac77e-5e8e-1030-9451-dbd28d49c70b  -p
 
...
998875101010,NOT_PORTED
99898900,NOT_PORTED
99909000100,NOT_PORTED
Total numbers: 28477

```