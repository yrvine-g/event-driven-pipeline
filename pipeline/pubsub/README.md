# Cloud Run Event Driven Pipeline Tutorial

This sample shows how to create an event driven pipeline on Cloud Run via Pub/Sub

<---- **EDIT** ---->

[![Run in Google Cloud][run_img]][run_link]

[run_img]: https://storage.googleapis.com/cloudrun/button.svg
[run_link]: https://deploy.cloud.run/?git_repo=https://github.com/yrvine-g/event-driven-pipeline&dir=pipeline/pubsub&cloudshell_tutorial=pipeline/pubsub/README.md
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
export PROJECT=$(gcloud config get-value project)
#export MY_GCS_BUCKET="$(gcloud config get-value project)-gcs-bucket"
export MY_GCS_BUCKET="event-driven-pipeline-bucket"
export REGION=us-central1
export SERVICE_ACCOUNT=cloud-run-pubsub-invoker1
```

## Quickstart

Use the [Jib Maven Plugin](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin) to build and push container image:

```sh
mvn compile jib:build -Dimage=gcr.io/$Project/$MY_RUN_CONTAINER
```

Deploy Cloud Run service:
```sh
gcloud config set run/region $REGION
gcloud run deploy $MY_RUN_SERVICE \
--image gcr.io/$MY_PROJECT/$MY_RUN_CONTAINER \
--no-allow-unauthenticated
```

Create PubSub Topic and GCS notification
```sh
gcloud pubsub topics create pipelineNotification

gsutil notification create -f json -t pipelineNotification -e OBJECT_FINALIZE gs://"$MY_GCS_BUCKET"
```

Create Pub/Sub Subscription and Service Account
```sh
export RUN_SERVICE_URL=$(gcloud run services describe $MY_RUN_SERVICE --format='value(status.url)')
gcloud iam service-accounts create $SERVICE_ACCOUNT \
--display-name "Cloud Run Pub/Sub Invoker"
gcloud run services add-iam-policy-binding $MY_RUN_SERVICE  \ 
--member=serviceAccount:$SERVICE_ACCOUNT@$PROJECT.iam.gserviceaccount.com \
--role=roles/run.invoker
gcloud pubsub subscriptions create pipelineTrigger --topic pipelineNotification \  
 --push-endpoint=$RUN_SERVICE_URL \
 --push-auth-service-account=$SERVICE_ACCOUNT@$PROJECT.iam.gserviceaccount.com \
 [--ack-deadline=600]
```

Create log sink
```shell
gcloud logging sinks create bq-job-completed \
pubsub.googleapis.com/projects/yrvine-rotation-demo/topics/pipelineNotification \
 --log-filter='resource.type="bigquery_project" severity=INFO protoPayload.metadata.jobChange.job.jobStatus.jobState="DONE"'

```
