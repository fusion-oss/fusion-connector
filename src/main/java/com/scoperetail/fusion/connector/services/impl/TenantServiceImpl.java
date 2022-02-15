package com.scoperetail.fusion.connector.services.impl;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.scoperetail.fusion.connector.persistence.entity.Tenant;
import com.scoperetail.fusion.connector.persistence.repository.TenantRepository;
import com.scoperetail.fusion.connector.services.TenantService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TenantServiceImpl implements TenantService {

  @Autowired
  private TenantRepository tenantRepository;

  @Override
  public Map<String, String> getAuthDetails() {
    log.debug("Fetching auth details");
    List<Tenant> activeTenant = tenantRepository.findByIsEnabled(true);
    Map<String, String> authDetailsByTenant =
        activeTenant.stream().collect(Collectors.toMap(Tenant::getAuthName, tenant -> HttpHeaders
            .encodeBasicAuth(tenant.getAuthName(), tenant.getAuthPassword(), null)));

    log.debug("Found {} active tenants", activeTenant.size());
    return authDetailsByTenant;
  }
}
