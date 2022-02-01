package com.example.cloudrun;

//import com.example.cloudrun.PubSubMessageBody.PubSubMessage;
import java.util.Map;

import com.google.protobuf.ByteString;
import lombok.extern.log4j.Log4j2;
import java.util.Base64;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.pubsub.v1.PubsubMessage;


@Log4j2
public class PubSubMessageParser {

  public static PubSubMessageProperties parsePubSubProperties(PubsubMessage message) {

    Map<String, String> attributes = message.getAttributes();
    if (attributes == null) {
      throw new RuntimeException("No attributes in the pubsub message");
    }

    String bucketId = attributes.get("bucketId");
    String objectId = attributes.get("objectId");

    //return null if there is no objectId
    if (objectId == null) {
        return null;
    }

    String[] parsedObjectId = objectId.split("/");

    if (parsedObjectId.length < 4) {
      throw new RuntimeException("The object id is formatted incorrectly");
    }
    String project = parsedObjectId[0];
    String dataset = parsedObjectId[1];
    String table = parsedObjectId[2];
    String triggerFileName = parsedObjectId[3];

    log.info("Project: " + project);
    log.info("dataset: " + dataset);
    log.info("table: " + table);
    log.info("triggerFIleName: " + triggerFileName);

    return PubSubMessageProperties.builder()
        .bucketId(bucketId).project(project).dataset(dataset).table(table)
        .triggerFile(triggerFileName).build();
  }

  public static PubSubMessageData parsePubSubData(ByteString data) throws JsonProcessingException{
    String dataStr = data.toStringUtf8();
    //String dataStr =
        //!StringUtils.isEmpty(data) ? new String(Base64.getDecoder().decode(data)) : "";
    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    PubSubMessageData dataObj = mapper.readValue(dataStr, PubSubMessageData.class);
    return dataObj; 

  }

}