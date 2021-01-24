#!/bin/bash

/opt/confd/bin/confd_cli -u admin -g admin -N <<EOF

config

cert-enrollment-auth-create auth-id 1 name $1 fingerprint $2


exit
exit

EOF
