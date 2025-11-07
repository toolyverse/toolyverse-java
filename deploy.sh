#!/bin/bash
# ./deploy_prod.sh 8080
# Production deployment script with graceful shutdown, active profile, and memory settings.

# --- Script Configuration ---
set -e # Exit immediately if a command exits with a non-zero status.
set -o pipefail # The return value of a pipeline is the status of the last command to exit with a non-zero status.

echo "Starting PRODUCTION deployment process..."

# --- Production Specific Settings ---
PORT=${1:-8080}
SPRING_PROFILE="prod"
JVM_OPTS="-Xms512m -Xmx1024m" # Set initial(Xms) and max(Xmx) heap size. Adjust as needed.
LOG_DIR="logs"

# --- Create log directory if it doesn't exist ---
mkdir -p $LOG_DIR
echo "Log directory set to: $LOG_DIR"

# --- Pull latest changes from git repository ---
echo "Pulling latest changes from git..."
git pull || { echo "Git pull failed!"; exit 1; }

# --- Find and gracefully shut down the existing Spring Boot application ---
echo "Looking for existing application on port $PORT..."
PID=$(lsof -t -i:$PORT || true)  # Added '|| true' to prevent script exit if no process found

if [ -n "$PID" ]; then
    echo "Found application running with PID: $PID"
    echo "Sending graceful shutdown signal (SIGTERM)..."
    kill -15 $PID || true

    # Wait for the application to shut down (with timeout)
    echo "Waiting for application to shut down gracefully (30s timeout)..."
    TIMEOUT=30
    # Use a loop to check if the process is still running
    while [ $TIMEOUT -gt 0 ] && kill -0 $PID 2>/dev/null; do
        sleep 1
        ((TIMEOUT--))
        echo -n "."
    done
    echo ""

    # If process is still running after timeout, force kill
    if kill -0 $PID 2>/dev/null; then
        echo "Application did not shut down gracefully. Force killing (SIGKILL)..."
        kill -9 $PID || true
        sleep 2  # Give it a moment after force kill
    else
        echo "Application shut down gracefully."
    fi
else
    echo "No application found running on port $PORT."
fi

# --- Build the project ---
echo "Building project with Maven (skipping tests)..."
mvn clean package -DskipTests=true

# --- Prepare Log File ---
LOG_FILE="$LOG_DIR/app_$(date '+%Y-%m-%d_%H-%M-%S').log"
echo "Log file will be: $LOG_FILE"

# --- Find the JAR file ---
JAR_FILE=$(find target -name "*.jar" -type f | head -n 1)
if [ -z "$JAR_FILE" ]; then
    echo "Error: No JAR file found in target directory!"
    exit 1
fi
echo "Found JAR file: $JAR_FILE"

# --- Run the Spring Boot application ---
echo "Starting Spring Boot application on port $PORT with profile '$SPRING_PROFILE'..."
echo "JVM Options: $JVM_OPTS"
nohup java $JVM_OPTS -Dspring.profiles.active=$SPRING_PROFILE -jar "$JAR_FILE" --server.port=$PORT > "$LOG_FILE" 2>&1 &

NEW_PID=$!
echo "Deployment completed! Application started with PID: $NEW_PID on port $PORT."
echo "Logs are being written to: $LOG_FILE"
echo "Use 'tail -f $LOG_FILE' to view logs"

# Optional: Wait a moment and verify the application started
sleep 3
if kill -0 $NEW_PID 2>/dev/null; then
    echo "✓ Application is running successfully!"
else
    echo "✗ Warning: Application may have failed to start. Check logs: $LOG_FILE"
fi