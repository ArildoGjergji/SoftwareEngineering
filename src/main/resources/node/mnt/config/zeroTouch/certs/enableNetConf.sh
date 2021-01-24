#!/bin/bash

/opt/confd/bin/confd_cli -u admin -g admin -N <<EOF

config

netconf-tls-edit admin-status UNLOCKED nodecredential $1 trustCategory  $2

exit
exit

EOF
