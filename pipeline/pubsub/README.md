# Cloud Run Event Driven Pipeline Tutorial

This sample shows how to create an event driven pipeline on Cloud Run via Pub/Sub

<---- **EDIT** ---->

[![Run in Google Cloud][run_img]][run_link]

[run_img]: https://storage.googleapis.com/cloudrun/button.svg
[run_link]: https://deploy.cloud.run/?git_repo=https://github.com/GoogleCloudPlatform/java-docs-samples&dir=run/pubsub
<---- **EDIT** ---->


## Dependencies

* **Spring Boot**: Web server framework.
* **Jib**: Container build tool.

## Setup
Create  GCS bucket and a folder path with the following format:
gs://bucket/project/dataset/table_name/*.avro

Enable following APIs: 
* container registry 
* cloud run handler


Configure environment variables:

```sh
export MY_RUN_SERVICE=run-service
export MY_RUN_CONTAINER=run-container
export MY_GCS_BUCKET="$(gcloud config get-value project)-gcs-bucket"
export PROJECT=$(gcloud config get-value project)
export REGION="us-central1"
export SERVICE_ACCOUNT=
```

Create PubSub Topic and GCS notification
```sh
gcloud pubsub topics create gcsObjectCreation

gsutil notification create -f json -t gcsObjectCreation -e OBJECT_FINALIZE gs://"$MY_GCS_BUCKET"
```

Build and Deploy Cloud Run (EDIT)
```sh
gcloud config set project yrvine-rotation-demo
gcloud config set run/region us-central1
gcloud auth configure-docker
```
```sh
mvn compile jib:build -Dimage=gcr.io/$Project/$MY_RUN_CONTAINER
gcloud run deploy $MY_RUN_SERVICE \
--image gcr.io/$MY_PROJECT/$MY_RUN_CONTAINER \
--no-allow-unauthenticated
```


Create Pub/Sub Subscription and Service Account
```sh
gcloud iam service-accounts create cloud-run-pubsub-invoker \
--display-name "Cloud Run Pub/Sub Invoker"
gcloud run services add-iam-policy-binding $MY_RUN_SERVICE  \ 
--member=serviceAccount:cloud-run-pubsub-invoker@${PROJECT}.iam.gserviceaccount.com \
--role=roles/run.invoker
gcloud pubsub subscriptions create runSubscription --topic gcsObjectCreation  
 --push-endpoint=https://pubsub-tutorial-luu2a4ccma-uc.a.run.app/ 
 --push-auth-service-account=cloud-run-pubsub-invoker@yrvine-rotation-demo.iam.gserviceaccount.com
```

Create log sink
```shell
gcloud logging sinks create bq-job-completed \
pubsub.googleapis.com/projects/yrvine-rotation-demo/topics/gcsObjectCreation --log-filter='resource.type="bigquery_project"
severity=INFO
protoPayload.metadata.jobChange.job.jobStatus.jobState="DONE"
protoPayload.authenticationInfo.principalEmail="143207692610-compute@developer.gserviceaccount.com"'

```
