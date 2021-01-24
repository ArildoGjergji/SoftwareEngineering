**CLASSI:**

Server,ICF,Configuration: Classi per la lettura del file di configurazione (ICF.json) e la gestione dei dati presenti in esso.

dhcp.java Utility functions: -Richiesta IP 
			      -Controllo presenza IP
			      -ICF download
			      
CertificatesManagement: Comunicazione verso il Fronthaul, gestione procedura dei certificati.

SoftwareManagement:Gestione procedura software download e sua relativa installazione sul nodo.

Per comunicare verso la Cli del nodo (confd_cli) utilizzo di Runtime.getRuntime().
Ogni processo(funzione) invocato da JRE esegue un shell script verso il nodo con tipica struttura:

```
#!/bin/bash

/opt/confd/bin/confd_cli -u admin -g admin -N <<EOF 

config

cert-enrollment-server-group-server-add group 1 server 1 protocol $1 uri $2

exit
exit

EOF			      

```

Ogni shell script si connette alla cli ed esegue una serie di comandi tramite l'utilizzo di un here-document.

L'output e l'errore degli script sono gestiti nell'ambiente java invece che direttamente nello shell script. 


