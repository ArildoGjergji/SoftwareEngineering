#!/bin/bash
/opt/confd/bin/confd_cli -u admin -g admin -N <<EOF
config
swdl-package-confirm package $1
exit
exit
EOF
