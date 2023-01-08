#!/bin/bash

# Pull new changes
#git pull

# Add environment variables
export BOT_NAME=$1
export BOT_TOKEN=$2
export DB_USERNAME=$3
export DB_PASSWORD=$4

# Prepare Jar
mvn clean package

# Ensure, that docker-compose stopped
docker-compose down

# Start new deployment
docker-compose up --build