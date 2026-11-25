🎙️ VoiceTask — Gestionnaire de tâches intelligent par commande vocale
📌 Présentation du projet
VoiceTask est une application mobile Android conçue pour faciliter la création, l'organisation et le suivi des tâches quotidiennes grâce à la reconnaissance vocale.

L'utilisateur pourra simplement exprimer une tâche à voix haute. L'application analysera ensuite la phrase prononcée afin d'en extraire les informations importantes, notamment le titre de la tâche, la date et l'heure d'exécution.

L'objectif est de proposer une solution simple, pratique et accessible permettant de gérer ses activités sans avoir à saisir manuellement toutes les informations.

🎯 Objectifs du projet
VoiceTask a pour principaux objectifs de :

Simplifier la création des tâches grâce à la commande vocale.
Convertir les instructions vocales en informations structurées.
Permettre l'enregistrement et la consultation des tâches.
Organiser les tâches selon leurs dates et leurs horaires.
Rappeler à l'utilisateur les tâches programmées.
Proposer une interface simple et intuitive.
✨ Fonctionnalités prévues
Fonctionnalité	Description
🎤 Reconnaissance vocale	Transformer la parole de l'utilisateur en texte.
🧠 Extraction des informations	Identifier le titre, la date et l'heure d'une tâche.
📝 Gestion des tâches	Ajouter, consulter et gérer les tâches.
💾 Stockage local	Enregistrer les tâches dans une base de données SQLite.
🔔 Rappels et notifications	Avertir l'utilisateur à l'heure prévue.
📱 Interface utilisateur	Faciliter la navigation et l'utilisation de l'application.
Exemple d'utilisation :

L'utilisateur prononce :

« Rappelle-moi de rencontrer le directeur demain à 10 heures. »

VoiceTask devra interpréter cette instruction et extraire :

Titre : Rencontrer le directeur
Date : Demain
Heure : 10 h 00
La tâche pourra ensuite être enregistrée et associée à un rappel.

🛠️ Technologies utilisées
Technologie	Utilisation
Java	Développement de la logique de l'application.
XML	Conception des interfaces Android.
Android Studio	Environnement de développement.
SpeechRecognizer	Reconnaissance vocale.
SQLite	Stockage local des tâches.
AlarmManager	Programmation des rappels.
Git et GitHub	Gestion du code source et travail collaboratif.
🏗️ Organisation du développement
Le développement est réparti entre cinq membres afin de faciliter le travail collaboratif.

Membre	Responsabilité	Fichiers principaux
Akeh	Interfaces et navigation	Interfaces XML et navigation
Habiba	Reconnaissance vocale	SpeechManager.java
Kako	Extraction et interprétation	DateParser.java, TaskParser.java
Halima Tou	Gestion de la base de données	DatabaseHelper.java, Task.java
Yakawa	Rappels et notifications	ReminderManager.java, ReminderReceiver.java
📂 Organisation du projet
Les principaux éléments du projet Android sont organisés de la manière suivante :

VoiceTask/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/example/voicetask/
│           │       ├── MainActivity.java
│           │       ├── DateParser.java
│           │       ├── TaskParser.java
│           │       ├── SpeechManager.java
│           │       ├── DatabaseHelper.java
│           │       ├── Task.java
│           │       ├── ReminderManager.java
│           │       └── ReminderReceiver.java
│           │
│           ├── res/
│           │   ├── layout/
│           │   ├── drawable/
│           │   └── values/
│           │
│           └── AndroidManifest.xml
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
Cette arborescence présente l'organisation cible du développement. Certains fichiers seront ajoutés progressivement par les membres de l'équipe.

⚙️ Installation et configuration
Prérequis
Avant de lancer le projet, il faut disposer de :

Android Studio.
JDK compatible avec la configuration Gradle du projet.
Git.
Un appareil Android ou un émulateur Android.
1. Cloner le dépôt
Ouvrir un terminal et exécuter :

git clone https://github.com/akehlaila-del/VoiceTask.git
2. Accéder au dossier
cd VoiceTask
3. Récupérer la branche de développement
git switch dev
git pull origin dev
4. Ouvrir le projet
Lancer Android Studio.
Sélectionner Open.
Choisir le dossier VoiceTask.
Attendre la synchronisation Gradle.
5. Exécuter l'application
Connecter un appareil Android ou démarrer un émulateur, puis cliquer sur Run dans Android Studio.

🌿 Gestion des branches Git
Le projet utilise une organisation basée sur plusieurs branches.

Branche	Rôle
main	Version stable du projet.
dev	Branche commune de développement.
interface-utilisateur	Développement des interfaces.
reconnaissance-vocale	Développement de la reconnaissance vocale.
extraction-informations	Développement de l'extraction des informations.
base-de-donnees	Développement de la base de données.
notifications-rappels	Développement des rappels et notifications.
Règles de collaboration
Chaque membre travaille sur sa propre branche.
Les branches personnelles sont créées à partir de dev.
Les modifications doivent être enregistrées avec des commits explicites.
Les modifications sont envoyées sur GitHub avec Push.
L'intégration dans dev se fait par Pull Request après vérification.
La branche main est réservée aux versions stables.
🚧 État actuel du projet
VoiceTask est actuellement en phase de développement.

Les prochaines étapes consistent à :

Développer les différents modules de l'application.
Assurer la communication entre les modules.
Tester la reconnaissance et l'interprétation des commandes vocales.
Vérifier l'enregistrement et la récupération des tâches.
Tester le fonctionnement des rappels et des notifications.
Intégrer progressivement les fonctionnalités dans la branche dev.
👥 Équipe de développement
Projet réalisé dans le cadre d'un travail collaboratif universitaire par une équipe de cinq étudiants :

Akeh — Interfaces et navigation.
Habiba — Reconnaissance vocale.
Kako — Extraction des informations.
Halima Tou — Base de données.
Yakawa — Rappels et notifications.
📄 Licence
Projet académique réalisé à des fins pédagogiques.

Les conditions de réutilisation et de distribution du code pourront être précisées ultérieurement.

VoiceTask — Votre voix, vos tâches, votre organisation.
