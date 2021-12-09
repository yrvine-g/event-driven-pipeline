package com.example.cloudrun;

import com.example.cloudrun.PubSubMessageBody.PubSubMessage;
import java.util.Map;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PubSubMessageParser {

  public static PubSubMessageProperties parsePubSubMessage(PubSubMessage message) {

    Map<String, String> attributes = message.getAttributes();
    if (attributes == null) {
      throw new RuntimeException("No attributes in the pubsub message");
    }

    attributes.forEach((key, value) -> log.info(key + " : " + value));
    String bucketId = attributes.get("bucketId");
    String objectId = attributes.get("objectId");

    String[] parsedObjectId = objectId.split("/");

    if (parsedObjectId.length < 4) {
      throw new RuntimeException("The object id is formatted incorrectly");
    }
    String project = parsedObjectId[0];
    String dataset = parsedObjectId[1];
    String table = parsedObjectId[2];
    String triggerFileName = parsedObjectId[3];

    return PubSubMessageProperties.builder()
        .bucketId(bucketId).project(project).dataset(dataset).table(table)
        .triggerFile(triggerFileName).build();
  }

}
