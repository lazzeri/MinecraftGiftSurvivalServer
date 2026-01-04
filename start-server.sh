#!/bin/bash

# Amount of RAM
MIN_RAM=2G
MAX_RAM=2G

# Server jar file
JAR="finalJar.jar"

# Java flags (safe defaults)
JAVA_FLAGS="-Xms$MIN_RAM -Xmx$MAX_RAM -jar $JAR nogui"

# Run the server
java $JAVA_FLAGS
