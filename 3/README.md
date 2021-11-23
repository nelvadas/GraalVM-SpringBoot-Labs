# 03: Assisted configuration for GraalVM native image

<div class="inline-container">

<span><img src="../images/noun_Stopwatch_14262_100.png"> </span>
<span style="color:blue;font-weight:bold"></span>
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
In this lab, the objective is to explore reflexion and proxies in GraalVM Builds.



## Add a Fibonacci Utils

In this application we will create a new class with the following snipet


```java
package com.oracle.graalvm.demo.hellospringbootapp;

import java.io.Serializable;
import java.util.Comparator;
import java.util.stream.Stream;

public class FibonacciUtils implements Serializable {

    public FibonacciUtils(){

    }
    /**
     * Get the fibonacci value for the input parameter
     * @param parameter
     * @return
     */
    public Long get(Integer parameter){

        long result = 0;
        result = Stream.iterate( new int[]{0,1}, fib-> new int[]{fib[1], fib[0]+fib[1]} )
                .limit(parameter)
                .map(x->x[0])
                .max(Comparator.naturalOrder())
                .get()
                .longValue();

        return result;
    }
}

```

## Compute Fibonaci value with Reflexion 

Update the `FibonaciController` to load the `FibonaciItils` class by reflexion, create an instance and invoke its `get`  method to retreive 
the fibonaci value of the calling parameter

```java
@RestController
public class FibonacciController {

	@GetMapping("/fibonacci/{parameter}")
	public Long fibonacci(@PathVariable Integer parameter) throws ReflectiveOperationException {
		Class<?> clazz = null;

			clazz = Class.forName("com.oracle.graalvm.demo.hellospringbootapp.FibonacciUtils");
			Object instance = clazz.getConstructor().newInstance();
			Method method = clazz.getDeclaredMethod("get", Integer.class);
			Long result = (Long) method.invoke(instance, parameter);
			return result;

	}

}
```


## Native Image build  

Rebuild the native binary and check the logs
```bash
..build.sh
```

The Spring AOT plugins generates and reuse a file `META-INF/native-image/org.springframework.aot/spring-aot/reflect-config.json` file
containing all the required classed loaded by reflection.

```json
...

-H:ReflectionConfigurationResources@file:///Users/nono/Projects/Workshops/EMEA-HOL-SpringBoot/hello-spring-boot-app/target/native-image/META-INF/native-image/org.springframework.aot/spring-aot/reflect-config.json=META-INF/native-image/org.springframework.aot/spring-aot/reflect-config.json \
```


You can use the GraalVM tracing agent to automatically retreive the reflexion configuration.


```
java -agentlib:native-image-agent=config-output-dir=META-INF/native-image -jar target/hello-spring-boot-app-0.0.1-SNAPSHOT.jar
```

Run the application, and then Check the content of your META-INF Directory
```
$ tree META-INF/native-image/
META-INF/native-image/
├── jni-config.json
├── predefined-classes-config.json
├── proxy-config.json
├── reflect-config.json
├── resource-config.json
└── serialization-config.json

0 directories, 6 files
```

```
cat META-INF/native-image/reflect-config.json  | grep Fib
  "name":"com.oracle.graalvm.demo.hellospringbootapp.FibonacciController",
  "name":"com.oracle.graalvm.demo.hellospringbootapp.FibonacciUtils",

```


To merge your files with an existing folder or directory, use the  `config-merge-dir`option 

```
java -agentlib:native-image-agent=config-merge-dir=/path/to/config-dir/ ...

```


By Running this simple helloworld application with GraalVM Native Technology, we can increase both sartup times and performances. 

Next, we'll try to explore various items related to Native Image Technologie for this application.

---
<a href="../4/">
    <img src="../images/noun_Next_511450_100.png"
        style="display: inline; height: 6em;" />
</a>





