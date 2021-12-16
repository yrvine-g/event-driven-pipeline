package com.example.cloudrun;

import com.example.cloudrun.PubSubMessageBody.PubSubMessage;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import java.util.Base64;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.JsonProcessingException;


@Log4j2
public class PubSubMessageParser {

  public static PubSubMessageProperties parsePubSubMessage(PubSubMessage message) {

    Map<String, String> attributes = message.getAttributes();
    if (attributes == null) {
      throw new RuntimeException("No attributes in the pubsub message");
    }
    // log.info("begin attributes");
    // attributes.forEach((key, value) -> log.info(key + " : " + value));
    // log.info("end attributes");

    // String data = message.getData();
    // String dataStr =
    //     !StringUtils.isEmpty(data) ? new String(Base64.getDecoder().decode(data)) : "";
    // log.info("begin data");
    // log.info(dataStr);
    // log.info("end data");

    // PubSubMessageData data = message.getData();
    // if (data.getInsertId() != null) {
    //     log.info("**insert id**");
    //     log.info(data.getInsertId());
    //     log.info("Resource Name:{}", data.getPayload().getResourceName());
    //     //log.info("Job status:{}" ,data.getPayload().getMetadata().getJobChange().getJob().getJobStatus().getJobState());
    // } else {
    //     log.info("**** insert id is null****");
    // }

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

    return PubSubMessageProperties.builder()
        .bucketId(bucketId).project(project).dataset(dataset).table(table)
        .triggerFile(triggerFileName).build();
  }

  public static PubSubMessageData parsePubSubMessageData(String data) throws JsonProcessingException{
    String dataStr =
        !StringUtils.isEmpty(data) ? new String(Base64.getDecoder().decode(data)) : "";
    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    PubSubMessageData dataObj = mapper.readValue(dataStr, PubSubMessageData.class);
    return dataObj; 

  }

}