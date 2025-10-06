#!/bin/bash
#./deploy.sh 8080
# Improved deployment script with graceful shutdown
echo "Starting deployment process..."

# Set default port or use the value passed as an argument
PORT=${1:-8080}

# Pull latest changes from git repository
echo "Pulling latest changes from git..."
git pull

# Find and gracefully shut down the existing Spring Boot application
echo "Looking for existing Spring Boot application on port $PORT..."
PID=$(lsof -t -i:$PORT)
if [ -n "$PID" ]; then
    echo "Found application running with PID: $PID"
    echo "Sending graceful shutdown signal..."
    kill -15 $PID  # SIGTERM for graceful shutdown

    # Wait for the application to shut down (with timeout)
    echo "Waiting for application to shut down gracefully..."
    TIMEOUT=30
    while [ $TIMEOUT -gt 0 ] && kill -0 $PID 2>/dev/null; do
        sleep 1
        ((TIMEOUT--))
        echo -n "."
    done
    echo ""

    # If process is still running after timeout, force kill
    if kill -0 $PID 2>/dev/null; then
        echo "Application did not shut down gracefully within timeout. Force killing..."
        kill -9 $PID
    else
        echo "Application shut down gracefully."
    fi
else
    echo "No application found running on port $PORT."
fi

# Build the project with package by default
echo "Building project with Maven..."
mvn clean package -DskipTests=true

# Create timestamped log file
LOG_FILE="app_$(date '+%Y-%m-%d_%H-%M-%S').log"
echo "Created log file: $LOG_FILE"

# Find the first JAR file in the target directory
JAR_FILE=$(find target -name "*.jar" -type f | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo "Error: No JAR file found in target directory!"
    exit 1
fi

echo "Found JAR file: $JAR_FILE"

# Run the Spring Boot application in the background with the specified port
echo "Starting Spring Boot application on port $PORT..."
nohup java -jar "$JAR_FILE" --server.port=$PORT > "$LOG_FILE" 2>&1 &

echo "Deployment completed! Application is running in the background on port $PORT."
echo "Logs are being written to: $LOG_FILE"
echo "Use 'tail -f $LOG_FILE' to view logs"