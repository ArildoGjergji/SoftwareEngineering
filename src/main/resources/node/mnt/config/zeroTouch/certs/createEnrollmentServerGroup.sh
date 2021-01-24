#!/bin/bash

/opt/confd/bin/confd_cli -u admin -g admin -N <<EOF

config

cert-enrollment-server-group-create group 1  

exit
exit

EOF
