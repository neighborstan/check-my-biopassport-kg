#!/bin/bash

# Ensure, that docker-compose stopped
docker-compose down

# Ensure, that the old application won't be deployed again.
mvn clean