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
import com.google.cloud.bigquery.BigQueryError;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PubSubMessageData {

  private String insertId;
  private ProtoPayload protoPayload;

  public PubSubMessageData(){
  }

  public PubSubMessageData(String insertId, ProtoPayload protoPayload){
      this.insertId = insertId;
      this.protoPayload = protoPayload;
  }
  public String getInsertId() {
    return insertId;
  }
  public void setInsertId(String insertId) {
    this.insertId = insertId;
  }

  public ProtoPayload getProtoPayload() {
    return protoPayload;
  }
  public void setProtoPayload(ProtoPayload protoPayload) {
    this.protoPayload = protoPayload;
  }

  public class ProtoPayload {
      private Metadata metadata;
      private String resourceName;

      public ProtoPayload(){
      }

    //   public ProtoPayload(Metadata metadata, String resourceName){
    //     this.metadata = metadata;
    //     this.resourceName = resourceName;
    //   }
      public String getResourceName() {
        return resourceName;
      }
      public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
      }
    
      public Metadata getMetadata() {
        return metadata;
      }
      public void setMetadata(Metadata metadata) {
          this.metadata = metadata;
      }

      public class Metadata {

        private JobChange jobChange;
        
        public Metadata() {
        }

        public Metadata(JobChange jobChange) {
            this.jobChange = jobChange;
        }
        public JobChange getJobChange() {
            return this.jobChange;
        }
        public void setJobChange(JobChange jobChange) {
            this.jobChange = jobChange;
        }
        public class JobChange {
            private Job job;
            private String after;
            public JobChange(){

            }
            public Job getJob(){
                return this.job;
            }
            public void setJob(Job job) {
                this.job = job;
            }
            public String getAfter(){
                return this.after;
            }
            public void setAfter(String after) {
                this.after = after;
            }

            public class Job{
                private JobStatus jobStatus;
                private String jobName;

                public Job(){
                }

                public  Job(JobStatus jobStatus, String jobName){
                    this.jobName = jobName;
                    this.jobStatus = jobStatus;
                }
                public JobStatus getJobStatus(){
                    return this.jobStatus;
                }
                public void setJobStatus(JobStatus jobStatus) {
                    this.jobStatus = jobStatus;
                }
                public String getJobName(){
                    return this.jobName;
                }
                public void setJobName(String jobName) {
                    this.jobName = jobName;
                }
                public class JobStatus{
                    private String jobState;
                    private BigQueryError errorProto;
                    public JobStatus(){
                    }
                    public JobStatus(String jobState, BigQueryError errorProto) {
                        this.jobState = jobState;
                        this.errorProto = errorProto;
                    }
                    public String getJobState(){
                        return this.jobState;
                    }
                    public void setJobState(String jobState) {
                        this.jobState = jobState;
                    }
                    public BigQueryError getError(){
                        return this.errorProto;
                    }

                }
            }
        }
      }
      
  }
}
