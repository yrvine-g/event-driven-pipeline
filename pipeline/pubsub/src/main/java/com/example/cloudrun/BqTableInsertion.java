package com.example.cloudrun;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryException;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.ExternalTableDefinition;
import com.google.cloud.bigquery.TableId;

// Sample to load Avro data from Cloud Storage into a new BigQuery table
public class BqTableInsertion {


  public static void bqTableInsertion(String bucketName, String projectName, String datasetName, String tableName, String tableFormat) {
//   public static void bqTableInsertion() {  
//     String sourceUri = "gs://event-driven-pipeline-bucket/yrvine-rotation-demo/san_francisco_bikeshare/bikeshare_regions/bikeshare_regions*";
//     String tableFormat = "AVRO";
//     String projectName = "yrvine-rotation-demo";
//     String datasetName = "san_francisco_bikeshare";
//     String tableName = "bikeshare_regions";

    //create sourceUri with format --> gs://bucket/project/dataset/table/table*
    String sourceUri = String.format("gs://%s/%s/%s/%s/%s*",bucketName, projectName, datasetName,tableName, tableName);

    try {

      // Initialize client that will be used to send requests. This client only needs to be created
      // once, and can be reused for multiple requests.
      BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();

      FormatOptions format = FormatOptions.of(tableFormat); 
      
      ExternalTableDefinition externalTable = ExternalTableDefinition.newBuilder(sourceUri, format).build();
      String externalTableName = "externalTable";
      //TO DO: Define table schema

      String query = String.format("CREATE OR REPLACE TABLE `%s.%s.%s` AS SELECT * FROM %s", projectName, datasetName, tableName, externalTableName);
      QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).addTableDefinition(externalTableName, externalTable).build();
      JobInfo jobInfo = JobInfo.of(queryConfig);
      JobId jobId = jobInfo.getJobId();
      Job job = bigquery.create(jobInfo);
      
      job = job.waitFor();
      if (job.isDone()) {
        System.out.println("Avro from GCS successfully loaded in a table");
      } else {
        System.out.println(
            "BigQuery was unable to load into the table due to an error:"
                + job.getStatus().getError());
      }
        
    } catch (BigQueryException | InterruptedException e) {
      System.out.println("Exception occured during insertion \n" + e.toString());
    }
  }
}