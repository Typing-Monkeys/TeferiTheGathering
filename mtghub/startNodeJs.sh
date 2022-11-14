#!/bin/bash

killall node
. "$HOME/.nvm/nvm.sh"
nvm use "v0.11.13"
echo -e "Node Avviato"
node /home/teamdrools2018A/mtghub/server.js
