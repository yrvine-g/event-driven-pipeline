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

import java.util.Map;
import com.google.pubsub.v1.PubsubMessage;

// Body.Message is the payload of a Pub/Sub event. Please refer to the docs for
// additional information regarding Pub/Sub events.
public class PubSubMessageBody {

  private PubsubMessage message;

  public PubSubMessageBody() {
  }

  public PubsubMessage getMessage() {
    return message;
  }

  public void setMessage(PubsubMessage message) {
    this.message = message;
  }

}
