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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "task_log")
public class TaskLog {

  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "task_id")
  private Task task;

  @Column(name = "from_date")
  @Builder.Default
  private LocalDateTime from = LocalDateTime.now();

  @Column(name = "to_date")
  @Builder.Default
  private LocalDateTime to = LocalDateTime.now();
  
  @Column(name = "correlation_id")
  private String correlationId;

  @Column(name = "task_status")
  private String taskStatus;

  @Column(name = "error_reason")
  private String errorReason;
  
  @CreationTimestamp
  @Column(name = "create_ts")
  private LocalDateTime createTs;
}
