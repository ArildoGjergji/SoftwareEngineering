#!/bin/sh
# execute configuration script passed as parameter
#
{ /opt/confd/bin/confd_cli -u admin -g admin << EOF;
unhide cli
cliexpert
ManagedElement 1 config-script execute script $1
EOF
} | grep -q 'result OK'
if [ $? != 0 ]; then
  result=fail
  echo "Script execution FAIL"
  exit 1
else
  result=running
fi
#
# wait for execution end
#
while [ "$result" = running ] ; do
{ /opt/confd/bin/confd_cli -u admin -g admin << EOF;
unhide cli
cliexpert
show ManagedElement 1 config-script progress-report result
EOF
} | grep -q 'NOT_AVAILABLE'
  if [ $? != 0 ]; then
    result=done
  else
    sleep 1
  fi
done
#
# and finally check result
#
{ /opt/confd/bin/confd_cli -u admin -g admin << EOF;
unhide cli
cliexpert
show ManagedElement 1 config-script progress-report result
EOF
} | grep -q 'SUCCESS'
if [ $? != 0 ]; then
  result=fail
  echo "Script execution FAIL"
  exit 1
else
  result=success
  echo "Script execution SUCCESS"
  exit 0
fi
