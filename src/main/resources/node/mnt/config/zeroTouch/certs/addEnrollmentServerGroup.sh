#!/bin/bash

/opt/confd/bin/confd_cli -u admin -g admin -N <<EOF

config

cert-enrollment-server-group-server-add group 1 server 1 protocol $1 uri $2

exit
exit

EOF
