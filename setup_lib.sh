#!/bin/bash
JUNIT="https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar"
HAMCREST="https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar"
OPENJFX="https://download2.gluonhq.com/openjfx/17.0.9/openjfx-17.0.9_linux-x64_bin-sdk.zip"
OPENJFX_ZIP="openjfx-17.0.9_linux-x64_bin-sdk.zip"
JSON="https://search.maven.org/remotecontent?filepath=org/json/json/20231013/json-20231013.jar"
mkdir lib
cd lib
curl -O $JUNIT
curl -O $HAMCREST
curl -O $OPENJFX
curl -O $JSON -L
unzip $OPENJFX_ZIP
