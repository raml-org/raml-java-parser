#!/usr/bin/env bash

TRAVIS_TAG=$1

echo "${TRAVIS_TAG}-pre"
date = `date`
echo "amusement $date" > just_a_test.txt
git add just_a_test.txt
git commit -a -m "Testing $date"
git push origin travis
#mvn --settings ~/settings.xml org.codehaus.mojo:versions-maven-plugin:2.1:set -DnewVersion=$TRAVIS_TAG
#mvn clean deploy --settings ~/settings.xml -DskipTests=true -B -U
#mvn clean deploy --settings ~/settings.xml -DskipTests=true -Psonatype -B -U