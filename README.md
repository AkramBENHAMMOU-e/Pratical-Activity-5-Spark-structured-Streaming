# Rapport TP Spark Structured Streaming

  

Ce TP met en place un mini-cluster Hadoop/Spark en conteneurs, charge des fichiers CSV dans HDFS puis exécute un job Spark Structured Streaming pour agréger les commandes.

  

## Architecture et prérequis

- `docker-compose.yaml` : lance HDFS (namenode/datanode + Yarn) et un cluster Spark standalone (master + worker).

- Données d'entrée : `orders1.csv`, `orders2.csv`, `orders3.csv` copiés dans HDFS sous `/data`.

- Jar de l'application : `target/untitled-1.0-SNAPSHOT.jar` construit via Maven.

  

![Jar généré](./screenShots/Capture%20d%E2%80%99%C3%A9cran%202025-12-05%20110246.png)

  

## Mise en route

1. Démarrer l'environnement : `docker-compose up -d`.

2. Copier les CSV dans le conteneur namenode puis dans HDFS :

   ```bash

   docker cp orders2.csv untitled-namenode-1:/tmp/

   docker exec -it untitled-namenode-1 hdfs dfs -put /tmp/orders2.csv /data/

   # Répéter pour orders1.csv et orders3.csv

   ```

   ![Copie HDFS orders2](./screenShots/Capture%20d%E2%80%99%C3%A9cran%202025-12-05%20111106.png)

   ![Copie HDFS orders3](./screenShots/Capture%20d%E2%80%99%C3%A9cran%202025-12-05%20111321.png)

3. Vérifier la présence des fichiers via l'UI HDFS ou `hdfs dfs -ls /data` :

   ![UI namenode montrant orders1.csv](./screenShots/copie-orders-csv.png)

  

## Lancement du job Spark

Soumission depuis le conteneur master :

```bash

docker exec -it spark-master /opt/spark/bin/spark-submit \

  --class com.tp.Main \

  --master spark://spark-master:7077 \

  /tmp/app.jar

```

![spark-submit](../screenShots/Capture%20d%E2%80%99%C3%A9cran%202025-12-05%20111144.png)

  

## Résultats du streaming

- Lecture de `/data` en streaming, affichage console en mode `complete`.

- Chaque batch correspond à l'arrivée d'un fichier CSV.

  

Batch 0 (orders1) :

![Batch 0](../screenShots/Capture%20d%E2%80%99%C3%A9cran%202025-12-05%20111216.png)

  

Batch 1 (orders2) :

![Batch 1](../screenShots/Capture%20d%E2%80%99%C3%A9cran%202025-12-05%20111233.png)

  

Batch 2 (orders3) :

![Batch 2](../screenShots/Capture%20d%E2%80%99%C3%A9cran%202025-12-05%20111405.png)

  

## Agrégation finale

Le job regroupe par `order_id` et calcule `sum(total)`. Résumé observé dans la console :

![Sommes par order_id](../screenShots/Capture%20d%E2%80%99%C3%A9cran%202025-12-05%20113502.png)

  

