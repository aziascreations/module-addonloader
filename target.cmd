@echo off
echo "Cleaning..."
call mvn clean > build.log
echo "Executing mvn package..."
call mvn package > build.log