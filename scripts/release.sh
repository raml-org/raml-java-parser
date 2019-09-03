#!/usr/bin/env bash

TRAVIS_TAG=$1
mvn --settings ~/settings.xml org.codehaus.mojo:versions-maven-plugin:2.1:set -DnewVersion=$TRAVIS_TAG
mvn clean deploy --settings ~/settings.xml -DskipTests=true -B -U
mvn clean deploy --settings ~/settings.xml -DskipTests=true -Psonatype -B -U