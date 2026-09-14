#!/usr/bin/env bash
rm -rf build
mkdir -p build/classes build/docs build/libs

find src/main/java -name "*.java" -print0 | xargs -0 javac -d build/classes
javadoc -d build/docs -sourcepath src/main/java -subpackages ru -quiet
jar --create --file build/libs/app.jar --main-class ru.nsu.bogomolov.Main -C build/classes .
java -jar build/libs/app.jar