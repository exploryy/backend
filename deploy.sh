#!/bin/bash

git pull
docker compose down --remove-orphans --volumes --rmi all

cd db || exit
docker build -t migration .
cd ../ || exit

docker compose up -d db migration
docker compose up -d