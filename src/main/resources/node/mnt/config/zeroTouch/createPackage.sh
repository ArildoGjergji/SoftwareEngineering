#!/bin/bash
/opt/confd/bin/confd_cli -u admin -g admin -N <<EOF
config
swdl-create-package url downloadFile:///$1
exit
exit
EOF
