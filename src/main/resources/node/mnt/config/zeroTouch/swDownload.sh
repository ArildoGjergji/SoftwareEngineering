#!/bin/bash
if [ ! -f "$2" ]; then
  tftp -r $1 -l $2 -g $5
fi
if [ ! -f "$4" ]; then
  tftp -r $3 -l $4 -g $5
fi
