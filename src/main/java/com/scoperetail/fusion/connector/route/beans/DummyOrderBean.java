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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import com.fasterxml.jackson.core.type.TypeReference;
import com.scoperetail.fusion.connector.common.JsonUtils;
import com.scoperetail.fusion.connector.persistence.dto.DummyOrder;

public class DummyOrderBean {

  @Autowired private ResourceLoader resourceLoader;

  public List<DummyOrder> getOrders() throws IOException {
    List<DummyOrder> orders = new ArrayList<>(1);
    final Resource resource = resourceLoader.getResource("classpath:dummyOrder.json");
    if (resource.exists()) {
      final String readString = Files.readString(Path.of(resource.getFile().getPath()));
      orders =
          JsonUtils.unmarshal(
              Optional.ofNullable(readString),
              Optional.of(new TypeReference<List<DummyOrder>>() {}));
    }
    return orders;
  }
}
