#!/bin/bash


JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
IGNITE_HOME=/usr/local/apache-ignite-fabric-1.9.0-bin
IGNITE_LIBS=${IGNITE_HOME}/libs/*:${IGNITE_HOME}/libs/ignite-spring/*:${IGNITE_HOME}/libs/ignite-ssh/*:${IGNITE_HOME}/libs/ignite-hadoop/*
IGNITE_CONFIG=${IGNITE_HOME}/config/default-config.xml

cd "$(dirname "$0")"
${JAVA_HOME}/bin/java -cp "../target/ignite-manager-0.1-SNAPSHOT.jar:${IGNITE_LIBS}" com.github.ambling.NodeManager ${IGNITE_CONFIG} $@
