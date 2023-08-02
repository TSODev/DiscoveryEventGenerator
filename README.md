# DiscoveryEventGenerator

exemple de ligne de commande: (nécessite un JVM)

**./DiscoveryEventGenerator -s "https://server/api/v1.9/" -u Allen -p Password_1234 -e "myApp" -t "custom" -a"{"detail1":"detail1"}" -xv**


Il s'agit d'un programme en ligne de commande qui permet de générer un évènement dans BMC Discovery


**usage**: [-h] [-x] [-v] -s SERVER -u USERNAME -p PASSWORD -e EVENT -t TYPE [-a PARAMS]

Discovery Event Generator :


required arguments:
**-s SERVER,--server SERVER** :            
       URL API du serveur Discovery , (https et termine avec '/') généralement https://server/api/v1.1/

**-u USERNAME, --username USERNAME** :         Login - Nom de l'utilisateur


**-p PASSWORD, --password PASSWORD** :          Login - Mot de passe


**-e EVENT, --event EVENT** :             nom de la source de l'évènement


**-t TYPE, --type TYPE** :  type de l'évènement




optional arguments:

**-a PARAMS, --params PARAMS** :  Parametres additionels (string format JSON)

**-h, --help** :            show this help message and exit

**-v, --verbose** :         valide le mode verbeux

**-x, --unsecure** :        pas de vérification du certificat SSL (accepte les certificats auto signés)
