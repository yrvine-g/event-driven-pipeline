/*
 * Copyright 2019 Google LLC
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

// [START cloudrun_pubsub_handler]
// [START run_pubsub_handler]
import java.util.Base64;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

// PubsubController consumes a Pub/Sub message.
@RestController
public class PubSubController {
  @RequestMapping(value = "/", method = RequestMethod.POST)
  public ResponseEntity receiveMessage(@RequestBody Body body) {
    // Get PubSub message from request body.
    Body.Message message = body.getMessage();
    if (message == null) {
      String msg = "Bad Request: invalid Pub/Sub message format";
      System.out.println(msg);
      return new ResponseEntity(msg, HttpStatus.BAD_REQUEST);
    }

    Map<String, String> attributes = message.getAttributes();
    if (attributes != null) { 

        attributes.forEach((key, value) -> System.out.println(key + " : " + value));
        String bucket = attributes.get("bucketId");
        String objectId = attributes.get("objectId");

        String triggerFilename = "trigger.txt";
        String[] parsedObjectId = objectId.split("/");
        
        if (parsedObjectId.length >= 4) {
            String project = parsedObjectId[0];
            String dataset = parsedObjectId[1];
            String table = parsedObjectId[2];
            String name = parsedObjectId[3];

            if (triggerFilename.equals(name)) {
                System.out.println("trigger file");
                //call bq insert function
                String tableFormat = "AVRO";
                BqTableInsertion.bqTableInsertion(bucket, project, dataset, table, tableFormat);
                return new ResponseEntity("triggered successfully", HttpStatus.OK);
            } else {
                System.out.println("Not trigger file");
                return new ResponseEntity("Not trigger file", HttpStatus.OK);
            }
        } else {
            System.out.println("The object id is formatted incorrectly");
            return new ResponseEntity("The object id is formatted incorrectly", HttpStatus.OK);
        }
    
    } else {
        System.out.println("No attributes");
        return new ResponseEntity("No attributes", HttpStatus.OK);
    }
    
  }
}
// [END run_pubsub_handler]
// [END cloudrun_pubsub_handler]