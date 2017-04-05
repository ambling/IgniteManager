#!/bin/bash


JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
IGNITE_HOME=/usr/local/apache-ignite-fabric-1.9.0-bin/

cd "$(dirname "$0")"
${JAVA_HOME}/bin/java -cp "../target/ignite-server-0.1-SNAPSHOT.jar:${IGNITE_HOME}/libs/*:${IGNITE_HOME}/libs/ignite-ssh/*" com.github.ambling.AdminNode $@

#${JAVA_HOME}/bin/java -cp "../target/ignite-server-0.1-SNAPSHOT.jar:${IGNITE_HOME}/libs/*:${IGNITE_HOME}/libs/ignite-ssh/*" com.github.ambling.AdminNode d start ../src/main/resources/cluster.ini
