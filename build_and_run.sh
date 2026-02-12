#!/bin/bash

# Compile the engine
echo "Compiling engine..."
javac -d build -cp . src/engine/collision/*.java src/engine/*.java

# Compile the example
echo "Compiling example..."
javac -d build -cp build GameEngineExample.java

# Run the example
echo "Running example..."
java -cp build GameEngineExample