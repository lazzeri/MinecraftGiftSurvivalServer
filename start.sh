#usr/bin/bash
# Allocated memory for the Minecraft server
MIN_RAM="8192M"
MAX_RAM="8192M"
# Name of your server jar file
JAR_FILE="finalJar.jar"
# Function to start the server
start_server() {
    echo "Starting Minecraft server with $MIN_RAM of RAM..."
    java -Xms$MIN_RAM -Xmx$MAX_RAM -jar $JAR_FILE nogui
}
# Function to pull the latest updates from the git repository
update_server_files() {
    echo "Pulling latest updates from the repository..."
    git pull
    if [ $? -ne 0 ]; then
        echo "Git pull failed. Please check your network connection or repository settings."
        exit 1
    fi
}
# Function to ask for user input to stop restart
stop_restart() {
    read -t 10 -p "Press ENTER to stop automatic restart or wait 10 seconds to continue... " input
    if [ "$input" != "" ]; then
        echo "Stopping server restarts..."
        exit 0
    fi
}
# Main loop to restart server if it crashes
while true; do
    # Update the server files from git before starting the server
    update_server_files
    
    # Start the Minecraft server
    start_server

    echo "Minecraft server crashed or stopped."
    stop_restart

    echo "Restarting server in 5 seconds..."
    sleep 5
done
