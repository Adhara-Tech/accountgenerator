/*
 * Copyright 2020 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.pegasys.accountgenerator.core.http;

import tech.pegasys.accountgenerator.core.jsonrpc.response.JsonRpcResponse;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;

public class HttpResponseFactory {

  private static final String JSON = HttpHeaderValues.APPLICATION_JSON.toString();

  public void create(
      final HttpServerRequest httpRequest, final int statusCode, final JsonRpcResponse body) {
    final HttpServerResponse response = httpRequest.response();

    response.putHeader("Content", JSON);
    response.setStatusCode(statusCode);
    response.setChunked(false);
    response.end(Json.encodeToBuffer(body));
  }
}
