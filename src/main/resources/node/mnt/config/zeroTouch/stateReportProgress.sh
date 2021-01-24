#!/bin/bash
/opt/confd/bin/confd_cli -u admin -g admin -N <<EOF
unhide cli
cliexpert
show running-config ManagedElement 1 SystemFunctions 1 SwM 1 UpgradePackage $1 state
exit
EOF
