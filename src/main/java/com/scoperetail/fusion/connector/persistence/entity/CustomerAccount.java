package com.scoperetail.fusion.connector.persistence.entity;

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

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "customer_account")
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CustomerAccount {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "customer_name")
  private String customerName;

  @Column(name = "auth_name")
  private String authName;

  @Column(name = "auth_password")
  private String authPassword;

  @Column(name = "is_enabled")
  private boolean isEnabled;

  @CreationTimestamp
  @Column(name = "create_ts")
  private LocalDateTime createTs;
  
}
