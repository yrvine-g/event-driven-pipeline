/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.cloudrun;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryException;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.ExternalTableDefinition;
import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.QueryJobConfiguration;
import lombok.extern.log4j.Log4j2;

@Log4j2

public class BQAccessor {

  private static final String EXTERNAL_TABLE_NAME = "externalTable";

  public static void insertIntoBQ(PubSubMessageProperties pubSubMessageProperties,
      String fileFormat) {
//   public static void bqTableInsertion() {  
//     String sourceUri = "gs://event-driven-pipeline-bucket/yrvine-rotation-demo/san_francisco_bikeshare/bikeshare_regions/bikeshare_regions*";
//     String tableFormat = "AVRO";
//     String projectName = "yrvine-rotation-demo";
//     String datasetName = "san_francisco_bikeshare";
//     String tableName = "bikeshare_regions";

    //create sourceUri with format --> gs://bucket/project/dataset/table/table*
    String sourceUri = String.format("gs://%s/%s/%s/%s/%s*", pubSubMessageProperties.getBucketId(),
        pubSubMessageProperties.getProject(), pubSubMessageProperties.getDataset(),
        pubSubMessageProperties.getTable(), pubSubMessageProperties.getTable());
    log.info("source URI is: {}", sourceUri);

    try {

      // Initialize client that will be used to send requests. This client only needs to be created
      // once, and can be reused for multiple requests.
      BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();

      FormatOptions format = FormatOptions.of(fileFormat);

      ExternalTableDefinition externalTable = ExternalTableDefinition.newBuilder(sourceUri, format)
          .build();
      //TO DO: Define table schema

      log.info("external table config: {}", externalTable);

      String query = String.format("CREATE OR REPLACE TABLE `%s.%s.%s` AS SELECT * FROM %s",
          pubSubMessageProperties.getProject(), pubSubMessageProperties.getDataset(), pubSubMessageProperties.getTable(), EXTERNAL_TABLE_NAME);
      QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query)
          .addTableDefinition(EXTERNAL_TABLE_NAME, externalTable).build();
      log.info("query we fired: {}" ,query);
      JobInfo jobInfo = JobInfo.of(queryConfig);
      Job job = bigquery.create(jobInfo);

      job = job.waitFor();
      if (job.isDone()) {
        log.info("Avro from GCS successfully loaded in a table");
      } else {
        log.info(
            "BigQuery was unable to load into the table due to an error:"
                + job.getStatus().getError());
        throw new RuntimeException("BigQuery was unable to load into the table due to an error: " + job.getStatus().getError().getMessage());
      }

    } catch (BigQueryException | InterruptedException e) {
      throw new RuntimeException("Exception occured during insertion to BQ", e);
    }
  }
}