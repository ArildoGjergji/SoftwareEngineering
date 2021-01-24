#!/bin/bash

/opt/confd/bin/confd_cli -u admin -g admin -N <<EOF

config

cert-trust-category-create id $1 trustedCertificates $2,$3,$4,$5

exit
exit

EOF
