#!/usr/bin/env bash

NEW_RELEASE=$1
mvn --settings ~/settings.xml org.codehaus.mojo:versions-maven-plugin:2.1:set -DnewVersion=$NEW_RELEASE && \
   mvn clean install --settings ~/settings.xml -B -U || exit 127

git commit -a -m "Preparing next release ${NEW_RELEASE}" && git push origin master || exit 127