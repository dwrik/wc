# wc

This is a _java_ implementation of the Unix core tool `wc`.

![wc command line execution](./images/screenshot.png "wc command line execution")

The cli is built using [picocli](https://picocli.info/) and the native executable is generated using [GraalVM](https://www.graalvm.org/). The executable is an `arm64` binary compiled on an M1 Macbook Air 2020.

The binary generated is standalone i.e. everything required to run it, is packaged into it. Java installation of any kind (JRE, JDK, JVM etc.) is not required thanks to _GraalVM native-image_.

The binary can be executed just like any other command line application by simply invoking it on the command line:
```
>> ./wc test.txt
    7145   58164  342190 test.txt
```

## Features

- line count
- word count
- byte count
- char count
- input from file
- input from stdin (compatible with pipes)


## Building the binary

1. Clone the repo.
2. Install dependencies and plugins.
```
>> mvn dependency:resolve
>> mvn dependency:resolve-plugins
```
3. Compile and run tests.
```
>> mvn test
```
4. Generate the _jar_ file.
```
>> mvn package
```
5. Install _graalvm_ and make sure the _native-image_ utility is on your path.
6. Generate the binary.
```
>> native-image -jar /path/to/jar
```

## Todo

- [ ] input from multiple files
