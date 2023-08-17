# DiscoveryEventGenerator

exemple de ligne de commande: (nécessite un JVM)

*./DiscoveryEventGenerator -s "https://server/api/v1.9/" -u USERNAME -p PASSWORD -e "myApp" -t "TSO_Event" -a "{'To Do':'Make It Done'}" -x --debug=info*


Il s'agit d'un programme en ligne de commande qui permet de générer un évènement dans BMC Discovery


##Usage: discovery-event-generator [\<options>]

  Enregistre un évènement dans BMC Discovery

###Options:
  **-s, --server**=\<text>    URL API du serveur Discovery , (https et termine avec
                         '/') généralement https://server/api/v1.1/
                         
  **-u, --username**=\<text>  Nom de l'utilisateur
  
  **-p, --password**=\<text>  Mot de Passe de l'utilisateur
  
  **-x, --unsecure**         pas de vérification du certificat SSL (permet
                         l'utilisation de certificat auto signé)
                         
  **-e, --event**=\<text>     nom de la source de l'évènement

  **--token**=\<text>         Jeton d'identification, (prioritaire par rapport à User/Password)

  **--type**=\<text>      type de l'évènement
  
  **-a, --params**=\<text>    Parametres additionnels (string format JSON)

  **-d, --debug**=(off | trace | debug | info | error | fatal | all)
                         Niveau du debug
                         
  **--version**              Show the version and exit
  
  **-h, --help**             Show this message and exit
