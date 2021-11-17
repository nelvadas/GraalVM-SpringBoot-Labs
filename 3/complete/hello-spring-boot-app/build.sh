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