#!/bin/sh
# udhcpc Interface Configuration
# Based on http://lists.debian.org/debian-boot/2002/11/msg00500.html
# udhcpc script edited by Tim Riker <Tim@Rikers.org>

[ -z "$1" ] && echo "Error: should be called from udhcpc" && exit 1
DHCP_IF_FILE=/tmpnr/dhcp_if

RESOLV_CONF="/etc/resolv.conf"
[ -n "$broadcast" ] && BROADCAST="broadcast $broadcast"
[ -n "$subnet" ] && NETMASK="netmask $subnet"
case "$1" in
  deconfig)
    /sbin/ifconfig fm1-mac3 | grep 'inet addr' | cut -d: -f2 | awk '{print $1}' > $DHCP_IF_FILE
    /sbin/ifconfig $interface 0.0.0.0
    ;;
  renew|bound)
    if [ -f $DHCP_IF_FILE ] ; then
      OLD_IP_ADDRESS=`cat $DHCP_IF_FILE`
      if [ -z $OLD_IP_ADDRESS ] ; then
        echo "EMPTY address"
      elif [ $OLD_IP_ADDRESS != $ip ] ; then
        echo "CHANGED IP ADDRESS from $OLD_IP_ADDRESS to $ip." >> /var/log/udhcp.log
        shutdown -r now
      fi
    fi
    /sbin/ifconfig $interface $ip $BROADCAST $NETMASK
    if [ -n "$router" ] ; then
      while route del default gw 0.0.0.0 dev $interface ; do
        true
      done

      for i in $router ; do
        route add default gw $i dev $interface
      done
    fi
    echo -n > $RESOLV_CONF
    [ -n "$domain" ] && echo search $domain >> $RESOLV_CONF
    for i in $dns ; do
      echo nameserver $i >> $RESOLV_CONF
    done
    echo "Write bootFileName $boot_file, tftpAddress $siaddr, to /mnt/config/ztcInfo.txt"
    echo "bootFileName:$boot_file" > /mnt/config/ztcInfo.txt
    echo "tftpAddress:$siaddr" >> /mnt/config/ztcInfo.txt
    iptables -F
    ;;
esac
exit 0
