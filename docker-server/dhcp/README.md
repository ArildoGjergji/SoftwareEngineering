**Container con Dhcp Server**

Il dockerfile crea un container da Alpine con Dhcp Server.
Le porte necessarie vengono esposte, il container esegue il servizio dhcp in background seguendo la configurazione fornita.

Tipica esecuzione:

```
sudo docker container run --rm -d --net host -v /"path-to-folder"/dhcpd.conf:/etc/dhcp/dhcpd.conf:ro --name dhcp dhcp
```

Nel file di configurazione sono presenti le opzioni 66 e 67:

option tftp-server-name  (option 66)
option bootfile-name	(option 67)
