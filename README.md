# GraalVM Spring Boot Hands on Labs
Accelerating and Optimizing SpringBoot Applications with GraalVM Universal Virtual Machine
## Overview
A Workshop for *Spring Boot*  lovers that will introduce & demonstrate some of the coolest things you can do with GraalVM.
The workshop is divided into a number of sub-pages, each largely self-contained, that cover one aspect of GraalVM Native Image. Some of these workshops are equally applicable to the Community Edition & the Enterprise Edition, but some focus on functionality that is only available within the Enterprise Edition, such as Profile Guided Optimisations.
This workshop focus first on the core features provided by GraalVM Enterprise Edition and not specifically on [Spring Native Project](https://github.com/spring-projects-experimental/spring-native) that is still Beta when writing this pages.


## Credits
This workshop relies a couple of materials from the following repositories. Thanks for your work and Open source engagement.
* [Spring Boot Guides](https://spring.io/guides/gs/spring-boot/)
* Link2


## Install GraalVM
This labs is build with the following components
* GraalVM latest version is 21.3.0 
* Java 11
* Gradle 4+ or Maven 3.2+

The instructions to install GraalVM can be found online [here](https://docs.oracle.com/en/graalvm/enterprise/21/docs/getting-started/#install-graalvm-enterprise).


## Table of Contents

* [What is Native Image?](./0/)
* [Creating a simple Spring Boot application](./1/)
* [Compile a simple Spring Boot application with GraalVM Native Image](./2/)
* [Assisted configuration for GraalVM native image](./3/)
* [Class initialization strategy for GraalVM native image](./4/)
* [Smaller deployment options for GraalVM native image](./5/)
* [Deployment options for GraalVM native images](./6/)
* [Configuring memory used by GraalVM native images](./7/)
* [GC options for GraalVM native image](./8/)
* [Profile guided optimizations for GraalVM native image](./9/)

More Readings and Workshops
* [GraalVM Native Image Workshop](https://github.com/krisfoster/Native-Image-Workshop)
* [GraalVM Polyglot Workshop](https://github.com/nelvadas/GraalVM-Polyglot-Labs)
* [Accelerating Apache Spark with GraalVM](https://github.com/nelvadas/spark-with-graalvm)
