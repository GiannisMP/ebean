package com.avaje.ebean.config;

/**
 * The mode to use for multi-tenancy.
 */
public enum TenantMode {

  /**
   * No multi-tenancy.
   */
  NONE(false),

  /**
   * Each Tenant has their own Database (javax.sql.DataSource)
   */
  DB(true),

  /**
   * Each Tenant has their own Database schema.
   */
  SCHEMA(true),

  /**
   * Tenants share tables but have a discriminator/partition column that partitions the data.
   */
  PARTITION(false);

  boolean dynamicDataSource;

  TenantMode(boolean dynamicDataSource) {
    this.dynamicDataSource = dynamicDataSource;
  }

  /**
   * Return true if the DataSource is not available on bootup.
   */
  public boolean isDynamicDataSource() {
    return dynamicDataSource;
  }
}
