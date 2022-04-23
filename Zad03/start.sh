#!/bin/sh
tmux kill-server
sleep 1
NAME=rabbit
TMUX= tmux new-session -d -s $NAME
tmux send-keys 'p3 team.py TEAM1' C-m
tmux split-window -h
tmux send-keys 'p3 admin.py' C-m
tmux split-window -h
tmux send-keys 'p3 supplier.py SUP2 backpack,oxygen' C-m
tmux split-window -v
tmux send-keys 'p3 supplier.py SUP1 boots,oxygen' C-m
tmux select-pane -t 0
tmux split-window -v
tmux send-keys 'p3 team.py TEAM2' C-m
tmux -2 attach-session -d