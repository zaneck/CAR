# CAR-TP1
Creer un serveur ftp.

Auteur
======
Valentin Owczarek val.owczarek@gmail.com

Enonce
======

Une classe Serveur avec une methode main

* ecoutant les demandes de connexion sur un port TCP > 1023
* donnant acces aux fichiers presents dans un repertoire du systeme de fichier. La valeur de ce repertoire est precisee et initialisee par une valeur passee en argument au moment du lancement du serveur FTP.
* delegant a l aide d un thread le traitement d une requete entrante a un objet de la classe FtpRequest

Une classe FtpRequest comportant

* une methode processRequest effectuant des traitements generaux concernant une requete entrante et deleguant le traitement des commandes
* une methode processUSER se chargeant de traiter la commande USER
* une methode processPASS se chargeant de traiter la commande PASS
* une methode processRETR se chargeant de traiter la commande RETR
* une methode processSTOR se chargeant de traiter la commande STOR
* une methode processLIST se chargeant de traiter la commande LIST
* une methode processQUIT se chargeant de traiter la commande QUIT
