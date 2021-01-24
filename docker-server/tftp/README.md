**Container con semplice Tftp Server**

Il dockerfile crea un container da Alpine con Tftp Server.
Le porte necessarie vengono esposte (69), il container esegue il servizio tftp.
Il server contiene il file ICF.json.

Tipica esecuzione:

```
sudo docker container run --rm -d --net net1 -v /"path-to-folder"/tftpboot:/var/tftpboot --name tftp tftp
```


