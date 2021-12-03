# 02: Compile a Spring Boot application with GraalVM Native Image

<div class="inline-container">

<span><img src="../images/noun_Stopwatch_14262_100.png"> </span>
<span style="color:blue;font-weight:bold">blue</span>
<strong>
  Estimated time: 30 minutes
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

In this application we will use Java Streams to compute the Fibonacci value for an input integer.

```java
@RestController
public class FibonacciController {

	@GetMapping("/fibonacci/{parameter}")
	public Long fibonacci(@PathVariable Integer parameter) {
		long result = 0;
		result = Stream.iterate( new int[]{0,1}, fib-> new int[]{fib[1], fib[0]+fib[1]} )
				.limit(parameter)
				.map(x->x[0])
				.max(Comparator.naturalOrder())
				.get()
				.longValue();

		return result;

	}
```

Build the application JAR and Start it the application in the standard way
```bash
$ java -jar target/hello-spring-boot-app-0.0.1-SNAPSHOT.jar

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.5.6)

2021-11-13 08:42:46.598  INFO 3370 --- [           main] c.o.g.d.h.HelloSpringBootAppApplication  : Starting HelloSpringBootAppApplication v0.0.1-SNAPSHOT using Java 11.0.13 on nono-mac with PID 3370 (/Users/nono/Projects/Workshops/EMEA-HOL-SpringBoot/hello-spring-boot-app/target/hello-spring-boot-app-0.0.1-SNAPSHOT.jar started by nono in /Users/nono/Projects/Workshops/EMEA-HOL-SpringBoot/hello-spring-boot-app)
2021-11-13 08:42:46.600  INFO 3370 --- [           main] c.o.g.d.h.HelloSpringBootAppApplication  : No active profile set, falling back to default profiles: default
2021-11-13 08:42:47.468  INFO 3370 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
2021-11-13 08:42:47.478  INFO 3370 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2021-11-13 08:42:47.478  INFO 3370 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.54]
2021-11-13 08:42:47.536  INFO 3370 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2021-11-13 08:42:47.536  INFO 3370 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 888 ms
2021-11-13 08:42:48.028  INFO 3370 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 1 endpoint(s) beneath base path '/actuator'
2021-11-13 08:42:48.098  INFO 3370 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2021-11-13 08:42:48.112  INFO 3370 --- [           main] c.o.g.d.h.HelloSpringBootAppApplication  : Started HelloSpringBootAppApplication in 1.868 seconds (JVM running for 2.245)
```

Note the `startup` time and the `package size`
- ` c.o.g.d.h.HelloSpringBootAppApplication  : Started HelloSpringBootAppApplication in 1.868 seconds (JVM running for 2.245)`
- `-rw-r--r--  1 nono  staff    19M Nov 13 08:42 hello-spring-boot-app-0.0.1-SNAPSHOT.jar`

``` bash {highlight=11}
$ ls -rtlh target/
total 38136
drwxr-xr-x  3 nono  staff    96B Nov 13 08:42 generated-sources
drwxr-xr-x  3 nono  staff    96B Nov 13 08:42 maven-status
drwxr-xr-x  5 nono  staff   160B Nov 13 08:42 classes
drwxr-xr-x  3 nono  staff    96B Nov 13 08:42 generated-test-sources
drwxr-xr-x  3 nono  staff    96B Nov 13 08:42 test-classes
drwxr-xr-x  4 nono  staff   128B Nov 13 08:42 surefire-reports
drwxr-xr-x  3 nono  staff    96B Nov 13 08:42 maven-archiver
-rw-r--r--  1 nono  staff   5.1K Nov 13 08:42 hello-spring-boot-app-0.0.1-SNAPSHOT.jar.original
-rw-r--r--  1 nono  staff    19M Nov 13 08:42 hello-spring-boot-app-0.0.1-SNAPSHOT.jar

```

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

The native image file is generated
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


Try to run the generated `hello` binary

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
This is related to the nature of Spring Boot Applications. We need a custom build process to be compliant  with GraalVM
and that is the Great Job beeing currently done by [Spring Native Project](https://github.com/spring-projects-experimental/spring-native)
to handle on these actions to make your application Native.
In the next section we will use Spring Native and a custom builder script to create a fully fonctionnal native binary

### Spring Native In Action

To enable Spring native,
:one:  add the Spring Native dependency in your `pom.xml`
file. This dependency embarks a set of mandatory API required to build you Spring boot application as native image.

```xml

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

:three: Add Mandatory maven repositories

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

:four: Add a main class
GraalVM Native image process requires a main class
Add the following property to your `pom.xml` file.

```xml
<properties>
		
		<main-class>com.oracle.graalvm.demo.hellospringbootapp.HelloSpringBootAppApplication</main-class>
	</properties>
  ```

:five: Add a custom builder script ( `build.sh`)
```shell
#!/usr/bin/env bash

echo "--- [           build.sh] Retreive Artifact name and version from  pom.xml"
VERSION=$(mvn -q \
  -Dexec.executable=echo \
  -Dexec.args='${project.version}' \
  --non-recursive \
  exec:exec);


ARTIFACT=$(mvn -q \
-Dexec.executable=echo \
-Dexec.args='${project.artifactId}' \
--non-recursive \
exec:exec);

JAR="$ARTIFACT-$VERSION.jar"

echo "--- [           build.sh] Artifact  is  '$JAR'"


echo "--- [           build.sh] Get  the Main Class "
MAINCLASS=$(mvn -q \
-Dexec.executable=echo \
-Dexec.args='${main-class}' \
--non-recursive \
exec:exec);
echo "--- [           build.sh] Spring Boot Main class :  '$MAINCLASS'"

rm -rf target
mkdir -p target/native-image

echo "--- [           build.sh] Build Spring Boot App with mvn package"
mvn -DskipTests package

echo "--- [           build.sh] Creating Path   "
cd target/native-image
jar -xvf ../$JAR >/dev/null 2>&1
cp -R META-INF BOOT-INF/classes


echo "[--->] Set the classpath "
LIBPATH=`find BOOT-INF/lib | tr '\n' ':'`
CP=BOOT-INF/classes:$LIBPATH

time native-image \
  --verbose \
  --no-server \
  --no-fallback \
  -H:TraceClassInitialization=true \
  -H:Name=$ARTIFACT \
  -H:+ReportExceptionStackTraces \
  --allow-incomplete-classpath \
  --report-unsupported-elements-at-runtime \
  -Dspring.graal.remove-unused-autoconfig=true \
  -Dspring.graal.remove-yaml-support=true \
  -cp $CP $MAINCLASS;

  ```

The script first packages the application as a JAR file
 then it create the rigth classpath to build this application as native.

The native builder plugins detects and list all the required classes to build a native image in the `target/native-image/META-INF` folder.
These classes are copied in `BOOT-INF/classes' and added in the classpath along with libraries required to build the application.


Build the native image with the following command
```bash
$ ./build.sh
...
tialComparable
[hello-spring-boot-app:11751]     (clinit):   1,088.34 ms,  7.20 GB
[hello-spring-boot-app:11751]   (typeflow):   9,072.92 ms,  7.20 GB
[hello-spring-boot-app:11751]    (objects):  31,001.53 ms,  7.20 GB
[hello-spring-boot-app:11751]   (features):  18,833.87 ms,  7.20 GB
[hello-spring-boot-app:11751]     analysis:  62,617.75 ms,  7.20 GB
[hello-spring-boot-app:11751]     universe:   4,034.00 ms,  7.20 GB
[hello-spring-boot-app:11751]      (parse):   3,429.36 ms,  7.07 GB
[hello-spring-boot-app:11751]     (inline):   6,228.65 ms,  6.96 GB
[hello-spring-boot-app:11751]    (compile):  69,021.07 ms,  7.53 GB
[hello-spring-boot-app:11751]      compile:  85,091.03 ms,  7.53 GB
[hello-spring-boot-app:11751]        image:   7,366.61 ms,  7.38 GB
[hello-spring-boot-app:11751]        write:   2,655.32 ms,  7.38 GB
[hello-spring-boot-app:11751]      [total]: 180,177.36 ms,  7.38 GB
# Printing build artifacts to: /Users/nono/Projects/Workshops/EMEA-HOL-SpringBoot/hello-spring-boot-app/target/native-image/hello-spring-boot-app.build_artifacts.txt

real    3m2.786s
user    21m49.107s
sys     0m24.013s

```

Check the newly built native image
```bash
$ ls -rtlh target/native-image/
total 167880
drwxr-xr-x  3 nono  staff    96B Feb  1  1980 org
drwxr-xr-x  6 nono  staff   192B Nov 13 16:33 META-INF
drwxr-xr-x  6 nono  staff   192B Nov 13 16:33 BOOT-INF
-rwxr-xr-x  1 nono  staff    82M Nov 13 16:36 hello-spring-boot-app
-rw-r--r--  1 nono  staff    36B Nov 13 16:36 hello-spring-boot-app.build_artifacts.txt
```

Start the `hello-spring-boot-app` binary
```bash
$  target/native-image/hello-spring-boot-app
2021-11-13 16:40:29.216  INFO 11927 --- [           main] o.s.nativex.NativeListener               : This application is bootstrapped with code generated with Spring AOT

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.5.6)

2021-11-13 16:40:29.217  INFO 11927 --- [           main] c.o.g.d.h.HelloSpringBootAppApplication  : Starting HelloSpringBootAppApplication using Java 11.0.13 on nono-mac with PID 11927 (/Users/nono/Projects/Workshops/EMEA-HOL-SpringBoot/hello-spring-boot-app/target/native-image/hello-spring-boot-app started by nono in /Users/nono/Projects/Workshops/EMEA-HOL-SpringBoot/hello-spring-boot-app)
2021-11-13 16:40:29.217  INFO 11927 --- [           main] c.o.g.d.h.HelloSpringBootAppApplication  : No active profile set, falling back to default profiles: default
2021-11-13 16:40:29.264  INFO 11927 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
2021-11-13 16:40:29.264  INFO 11927 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2021-11-13 16:40:29.264  INFO 11927 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.54]
2021-11-13 16:40:29.269  INFO 11927 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2021-11-13 16:40:29.269  INFO 11927 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 51 ms
2021-11-13 16:40:29.271  WARN 11927 --- [           main] i.m.c.i.binder.jvm.JvmGcMetrics          : GC notifications will not be available because MemoryPoolMXBeans are not provided by the JVM
2021-11-13 16:40:29.296  INFO 11927 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 1 endpoint(s) beneath base path '/actuator'
2021-11-13 16:40:29.299  INFO 11927 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2021-11-13 16:40:29.299  INFO 11927 --- [           main] c.o.g.d.h.HelloSpringBootAppApplication  : Started HelloSpringBootAppApplication in 0.104 seconds (JVM running for 0.104)

```

`Started HelloSpringBootAppApplication in 0.104 seconds (JVM running for 0.104)`

The application start very fast (100X faster than regular startup with java -jar ) and use less resources than the traditionnal App



## Native Image vs JIT

Send 10000 requests to compute Fib(100) to your running  native image application
```
$ ab -n 10000  -c 100 http://localhost:8080/fibonacci/100
This is ApacheBench, Version 2.3 <$Revision: 1879490 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking localhost (be patient)
Completed 1000 requests
Completed 2000 requests
Completed 3000 requests
Completed 4000 requests
Completed 5000 requests
Completed 6000 requests
Completed 7000 requests
Completed 8000 requests
Completed 9000 requests
Completed 10000 requests
Finished 10000 requests


Server Software:
Server Hostname:        localhost
Server Port:            8080

Document Path:          /fibonacci/100
Document Length:        10 bytes

Concurrency Level:      100
Time taken for tests:   2.741 seconds
Complete requests:      10000
Failed requests:        0
Total transferred:      1150000 bytes
HTML transferred:       100000 bytes
Requests per second:    3648.95 [#/sec] (mean)
Time per request:       27.405 [ms] (mean)
Time per request:       0.274 [ms] (mean, across all concurrent requests)
Transfer rate:          409.79 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0   14  24.2     11     265
Processing:     2   13  25.5     10     265
Waiting:        1   13  24.6     10     265
Total:          6   27  35.4     22     281

Percentage of the requests served within a certain time (ms)
  50%     22
  66%     26
  75%     29
  80%     30
  90%     32
  95%     35
  98%     50
  99%    269
 100%    281 (longest request)
 ````




```shell
java -jar target/hello-spring-boot-app-0.0.1-SNAPSHOT.jar
```

Send 10000 requests to your application with a concurrency level 100 with Apache Bench or another load tester tool.
```shell
$ $ ab -n 10000  -c 100 http://localhost:8080/fibonacci/100
This is ApacheBench, Version 2.3 <$Revision: 1879490 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking localhost (be patient)
Completed 1000 requests
Completed 2000 requests
Completed 3000 requests
Completed 4000 requests
Completed 5000 requests
Completed 6000 requests
Completed 7000 requests
Completed 8000 requests
Completed 9000 requests
Completed 10000 requests
Finished 10000 requests


Server Software:
Server Hostname:        localhost
Server Port:            8080

Document Path:          /fibonacci/100
Document Length:        10 bytes

Concurrency Level:      100
Time taken for tests:   4.469 seconds
Complete requests:      10000
Failed requests:        0
Total transferred:      1150000 bytes
HTML transferred:       100000 bytes
Requests per second:    2237.44 [#/sec] (mean)
Time per request:       44.694 [ms] (mean)
Time per request:       0.447 [ms] (mean, across all concurrent requests)
Transfer rate:          251.28 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0   17  26.7     14     408
Processing:     6   26  40.3     21     433
Waiting:        1   20  38.1     15     417
Total:          9   43  48.3     35     457

Percentage of the requests served within a certain time (ms)
  50%     35
  66%     38
  75%     40
  80%     43
  90%     51
  95%     56
  98%    178
  99%    318
 100%    457 (longest request)
 ```

GraalVM Native image process start fast and even use less resources.
```
nono-mac:target nono$ ps auwx|egrep "MEM|12370"|grep -v grep
USER               PID  %CPU %MEM      VSZ    RSS   TT  STAT STARTED      TIME COMMAND
nono             12370   0,1  1,2 16445252 403896 s002  S+    4:55     0:26.68 java -jar target/hello-spring-boot-app-0.0.1-SNAPSHOT.jar

nono-mac:target nono$ ps auwx|egrep "MEM|12271"|grep -v grep
USER               PID  %CPU %MEM      VSZ    RSS   TT  STAT STARTED      TIME COMMAND
nono             12271   0,0  0,9 38333440 289392 s002  S+    4:52     0:02.58 target/native-image/hello-spring-boot-app
```

By Running this simple helloworld application with GraalVM Native Technology, we can increase both sartup times and performances.

Next, we'll try to explore various items related to Native Image Technologie for this application.

---
<a href="../3/">
    <img src="../images/noun_Next_511450_100.png"
        style="display: inline; height: 6em;" />
</a>
