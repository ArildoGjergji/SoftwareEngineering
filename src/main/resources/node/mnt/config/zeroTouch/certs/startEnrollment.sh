#!/bin/bash

/opt/confd/bin/confd_cli -u admin -g admin -N <<EOF

config

cert-start-online-enrollment nodecredential $1 password $2 

exit
exit

EOF
