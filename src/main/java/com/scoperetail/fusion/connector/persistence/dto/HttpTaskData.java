package com.scoperetail.fusion.connector.persistence.dto;

/*-
 * *****
 * fusion-connector
 * -----
 * Copyright (C) 2018 - 2022 Scope Retail Systems Inc.
 * -----
 * This software is owned exclusively by Scope Retail Systems Inc.
 * As such, this software may not be copied, modified, or
 * distributed without express permission from Scope Retail Systems Inc.
 * =====
 */

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpTaskData extends TaskData {

  private String url;
  private Map<String, String> queryParams;
  private String methodType;
  private Map<String, String> httpHeaders;
  private String port;

}
