#!/bin/bash

/opt/confd/bin/confd_cli -u admin -g admin -N <<EOF

config

cert-trusted-install-from-uri uri $1

exit
exit

EOF

