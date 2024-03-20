# Remove previously compiled code
rm ./bin/*.class ./bin/bakery/*.class ./bin/util/*.class ./bin/test/*/*.class
# Compile the game
javac src/main/*.java src/main/bakery/*.java src/main/util/*.java -d ./bin/
# Compile the tests... we'll compile all three types of test separately to maximise the chances of (at least partial) success
javac -cp .:junit-platform-console-standalone.jar --source-path ./src/main/ ./src/test/test/structural/*.java -d ./bin/
javac -cp .:junit-platform-console-standalone.jar --source-path ./src/main/ ./src/test/test/functional/*.java -d ./bin/
javac -cp .:junit-platform-console-standalone.jar --source-path ./src/main/ ./src/test/test/javadoc/*.java -d ./bin/
# Run the tests
java -jar junit-platform-console-standalone.jar --class-path ./bin/ --scan-class-path --fail-if-no-tests
