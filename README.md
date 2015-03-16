# CAR

Construction d’Applications Réparties

Auteur
======
Valentin Owczarek val.owczarek@gmail.com

PARTIE 1 serveur ftp
====================

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


How to use

<code>
	java -jar ftpServer.jar
</code>


PARTIE 2 passerelle rest
========================

Ce TP met en oeuvre une architecture repartie à trois niveaux : client REST, passerelle
REST/FTP, serveur FTP.

How to use
<code>
	java -jar FtpServer.jar
	java -jar RestPass.jar
</code>


Troubleshooting
===============

* FTP
  * Utilisation des port 3636 et 3637

  * Utilisateur "anonymous" sans mdp et "bilbon" mpd "hello_world"

  * L'utilisateur anonymous ne peux se deplacer de son dossier

  * Un dossier par utilisateur, qui doivent etre cree par l'utilisateur du serveur
    - mkdir -p server/anonymous
    - mkdir -p server/bilbon

  * Le serveur utilise une connection passive PASV

  * CWD ne prend que des arguments simple, (".." "images"), pas des arguments compose (../Images)

* REST
  * Lancer la paserelle apres le serveur FTP

  * Connection
    - [adresseDeLaMachineHote]:8080/rest/api/ftp