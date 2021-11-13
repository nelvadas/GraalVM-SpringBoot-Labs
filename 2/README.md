# 02: Compile a Spring Boot application with GraalVM Native Image

<div class="inline-container">

<span><img src="../images/noun_Stopwatch_14262_100.png"> </span>
<span style="color:blue;font-weight:bold">blue</span>
<strong>
  Estimated time: 15 minutes
</strong>
</div>

<div class="inline-container">
<img src="../images/noun_Book_3652476_100.png">
<strong>References:</strong>
</div>

- [Compiling Native Image](https://docs.oracle.com/en/graalvm/enterprise/21/docs/reference-manual/native-image/)

## Overview 
In this lab, the objective is to create a Native image from a simple Spring Boot application using GraalVM updater utility.

## Native image Extension
GraalVM comes with a couple of extensions shipped sepearately from the core
use the `gu` to install native image 
```shell
$ which gu
/Users/nono/.sdkman/candidates/java/21.3.0-ee11/bin/gu
```

```
$ gu install native-image
```
Check the extension list
```shell
$ gu list
ComponentId              Version             Component name                Stability                     Origin
---------------------------------------------------------------------------------------
graalvm                  21.3.0              GraalVM Core                  Supported
js                       21.3.0              Graal.js                      Supported
native-image             21.3.0              Native Image                  Early adopter
```

## Add a Fibonacci Controller



## Native Image build  


### Standard Build 
Make sure we are running with GraalVM
```shell
$  time native-image --no-fallback -jar target/hello-spring-boot-app-0.0.1-SNAPSHOT.jar hello
[hello:15916]    classlist:   1,024.27 ms,  0.96 GB
[hello:15916]        (cap):   1,546.84 ms,  0.96 GB
[hello:15916]        setup:   3,449.22 ms,  0.96 GB
[hello:15916]     (clinit):     220.06 ms,  1.61 GB
[hello:15916]   (typeflow):   4,054.17 ms,  1.61 GB
[hello:15916]    (objects):   4,450.19 ms,  1.61 GB
[hello:15916]   (features):     527.98 ms,  1.61 GB
[hello:15916]     analysis:   9,631.08 ms,  1.61 GB
[hello:15916]     universe:     662.46 ms,  1.61 GB
[hello:15916]      (parse):     795.63 ms,  2.06 GB
[hello:15916]     (inline):   1,021.76 ms,  2.08 GB
[hello:15916]    (compile):  17,664.19 ms,  4.32 GB
[hello:15916]      compile:  21,106.44 ms,  4.32 GB
[hello:15916]        image:   3,968.85 ms,  4.32 GB
[hello:15916]        write:     632.53 ms,  4.32 GB
[hello:15916]      [total]:  40,683.94 ms,  4.32 GB
# Printing build artifacts to: /Users/nono/Projects/Workshops/EMEA-HOL-SpringBoot/hello-spring-boot-app/hello.build_artifacts.txt

real    0m41.807s
user    5m5.960s
sys     0m7.726s

```

The native image binary file `hello` is generated
```
nono-mac:hello-spring-boot-app nono$ ls -rtlh
total 31296
drwxr-xr-x@  4 nono  staff   128B Nov  3 14:19 src
-rw-r--r--@  1 nono  staff   6.5K Nov  3 14:19 mvnw.cmd
-rwxr-xr-x@  1 nono  staff   9.8K Nov  3 14:19 mvnw
-rw-r--r--@  1 nono  staff   1.3K Nov  3 14:19 HELP.md
-rw-r--r--   1 nono  staff   8.3K Nov  8 22:29 hello-spring-boot-app.iml
-rw-r--r--@  1 nono  staff   3.5K Nov 12 12:51 pom.xml
drwxr-xr-x  11 nono  staff   352B Nov 12 12:51 target
-rwxr-xr-x   1 nono  staff    15M Nov 12 12:53 hello
-rw-r--r--   1 nono  staff    20B Nov 12 12:53 hello.build_artifacts.txt
nono-mac:hello-spring-boot-app nono$ 

```


Try to run the generated hello binary 

```
$ ./hello 
Exception in thread "main" java.lang.IllegalStateException: java.util.zip.ZipException: zip END header not found
        at org.springframework.boot.loader.ExecutableArchiveLauncher.<init>(ExecutableArchiveLauncher.java:52)
        at org.springframework.boot.loader.JarLauncher.<init>(JarLauncher.java:48)
        at org.springframework.boot.loader.JarLauncher.main(JarLauncher.java:88)
Caused by: java.util.zip.ZipException: zip END header not found
        at java.util.zip.ZipFile$Source.zerror(ZipFile.java:1597)

```
:X: an error is reported immediately.  
This is related to the nature of Spring Boot Application. We need a custom build process to be compliant  with GraalVM Native image genration.

That is where Spring Native sit in the picture , Great Job beeing currently done by [Spring Native Project](https://github.com/spring-projects-experimental/spring-native) 
to handle on these actions to make your application Native.
In the next section we will use Spring Native and a custom builder script to create a fully fonctionnal native binary 

### Spring Native In Action

To enable Spring native,
:one:  add the Spring Native dependency in your `pom.xml` 
file. This dependency embarks a set of mandatory API required to build you Spring boot application as native image.

````

		<dependency>
			<groupId>org.springframework.experimental</groupId>
			<artifactId>spring-native</artifactId>
			<version>0.10.5</version>
		</dependency>
```

:two: Update the build section to include the Spring AOT Maven plugin .
This plugin handles AOT transformations during the build phase to make the application compliant with the GraalVM Native Image build process.

```xml

<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
			<plugin>
				<groupId>org.springframework.experimental</groupId>
				<artifactId>spring-aot-maven-plugin</artifactId>
				<version>0.10.5</version>
				<executions>
					<execution>
						<id>test-generate</id>
						<goals>
							<goal>test-generate</goal>
						</goals>
					</execution>
					<execution>
						<id>generate</id>
						<goals>
							<goal>generate</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>

```

:three: Add Mandatory maven repositories for new added dependencies and plugings.


```
repositories>

		<repository>
			<id>spring-release</id>
			<name>Spring release</name>
			<url>https://repo.spring.io/release</url>
		</repository>
	</repositories>
	<pluginRepositories>
		<pluginRepository>
			<id>spring-release</id>
			<name>Spring release</name>
			<url>https://repo.spring.io/release</url>
		</pluginRepository>
	</pluginRepositories>
  ```

:four:  Build Script.
To build our native image, we first need to package it as a fat jar,
then extract all classes from the jar to build the classpath.

```
bash


```


## Graal Compiler vs C2
GraalVM comes with a new JIT compiler 

```shell 
java -jar target/hello-spring-boot-app-0.0.1-SNAPSHOT.jar
```

Send 30000 requests to your application with a concurrency level 100 with Apache Bench or another load tester tool.
```shell
$ ab -n 30000  -c 100 http://localhost:8080/
This is ApacheBench, Version 2.3 <$Revision: 1879490 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/


Concurrency Level:      100
Time taken for tests:   58.214 seconds
Complete requests:      30000
Failed requests:        0
Total transferred:      5070000 bytes
HTML transferred:       1080000 bytes
Requests per second:    515.34 [#/sec] (mean)
Time per request:       194.047 [ms] (mean)
Time per request:       1.940 [ms] (mean, across all concurrent requests)
Transfer rate:          85.05 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0  171 1486.9     22   19323
Processing:     0   22  21.9     19     508
Waiting:        0   21  21.5     18     506
Total:          0  194 1486.1     43   19323

Percentage of the requests served within a certain time (ms)
  50%     43
  66%     51
  75%     58
  80%     62
  90%     69
  95%    107
  98%    164
  99%  11363
 100%  19323 (longest request)
 ```


Restart the application without Graal Compiler 
```shell
java -XX:-UseJVMCICompiler target/hello-spring-boot-app-0.0.1-SNAPSHOT.jar
```

Send the same load to the application instance without Graal Compliler

```shell
$ ab -n 30000  -c 100 http://localhost:8080/

Concurrency Level:      100
Time taken for tests:   60.561 seconds
Complete requests:      30000
Failed requests:        0
Total transferred:      5070000 bytes
HTML transferred:       1080000 bytes
Requests per second:    495.37 [#/sec] (mean)
Time per request:       201.869 [ms] (mean)
Time per request:       2.019 [ms] (mean, across all concurrent requests)
Transfer rate:          81.76 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0  177 1536.7     22   19308
Processing:     0   24  32.1     21     467
Waiting:        0   24  31.9     20     465
Total:          1  201 1535.6     46   19309

Percentage of the requests served within a certain time (ms)
  50%     46
  66%     54
  75%     61
  80%     63
  90%     70
  95%     83
  98%    311
  99%  11199
 100%  19309 (longest request)

```

By Running this simple helloworld application with GraalVM JIT, we can handle **+20 requests per second in average**

Existing applications can leverage GraalVM JIT to accelerate their performances whitout having to change 
any line of code.

Next, we'll try to explore Native images build for this application.

---
<a href="../2/">
    <img src="../images/noun_Next_511450_100.png"
        style="display: inline; height: 6em;" />
</a>


