#!/bin/bash
/opt/confd/bin/confd_cli -u admin -g admin -N <<EOF
unhide cli
cliexpert
show ManagedElement 1 SystemFunctions 1 SwM 1 UpgradePackage $1 reportProgress
exit
EOF
