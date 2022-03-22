package com.scoperetail.fusion.connector.route.beans;

/*-
 * *****
 * fusion-connector
 * -----
 * Copyright (C) 2018 - 2022 Scope Retail Systems Inc.
 * -----
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * =====
 */

import static com.scoperetail.fusion.connector.common.Constants.CORRELATION_ID;
import java.time.LocalDateTime;
import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import com.scoperetail.fusion.connector.common.TaskStatus;
import com.scoperetail.fusion.connector.persistence.entity.Task;
import com.scoperetail.fusion.connector.persistence.entity.TaskLog;
import com.scoperetail.fusion.connector.persistence.repository.TaskLogRepository;
import com.scoperetail.fusion.connector.persistence.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostProcessorBean {

  @Autowired private TaskRepository taskRepository;

  @Autowired private TaskLogRepository logRepository;

  public void setTasks(Exchange exchange) {
    Task task = exchange.getProperty("tenantTask", Task.class);

    String response = exchange.getIn().getBody(String.class);
    String correlationId = exchange.getProperty(CORRELATION_ID, String.class);
    log.info("Response :: {}", response);
    saveLogIEntity(
        (LocalDateTime) exchange.getProperty("fromTime"),
        (LocalDateTime) exchange.getProperty("toTime"),
        task,
        response,
        correlationId);
    task.setLatestCheckpoint((LocalDateTime) exchange.getProperty("toTime"));
    taskRepository.save(task);
    exchange.getIn().setBody(response);
  }

  private void saveLogIEntity(
      final LocalDateTime from,
      final LocalDateTime to,
      final Task task,
      String response,
      String correlationId) {
    Integer orderCount = new JSONArray(response).length();
    log.info(
        "TenantId :: {}, TaskId :: {}, CorrelationId :: {},  OrderCount :: {}",
        task.getTenant().getId(),
        task.getId(),
        correlationId,
        orderCount);
    TaskLog taskLog =
        TaskLog.builder()
            .task(task)
            .from(from)
            .to(to)
            .correlationId(correlationId)
            .taskStatus(TaskStatus.SUCCESS.name())
            .build();
    logRepository.save(taskLog);
    log.info("Saved successfully");
  }
}
