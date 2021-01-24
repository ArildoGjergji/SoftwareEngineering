FROM alpine

RUN apk update; apk add dhcp
RUN touch /var/lib/dhcp/dhcpd.leases

EXPOSE 67/tcp 67/udp  546/udp 547/udp 

ENTRYPOINT ["/usr/sbin/dhcpd"]
CMD ["-4", "-f", "-d", "--no-pid", "-cf", "/etc/dhcp/dhcpd.conf"]
