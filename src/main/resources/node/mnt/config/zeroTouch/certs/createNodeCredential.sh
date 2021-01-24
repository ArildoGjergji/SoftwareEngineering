#!/bin/bash

/opt/confd/bin/confd_cli -u admin -g admin -N <<EOF

config

cert-node-credential-create nodecredential $1 enrollmentauth 1 group 1 expiryAlarmThreshold $2 key $3 subjname $4

exit
exit

EOF


