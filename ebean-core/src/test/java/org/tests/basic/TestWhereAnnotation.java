package org.tests.basic;

import io.ebean.Ebean;
import io.ebean.Query;
import io.ebean.TransactionalTestCase;

import org.tests.model.basic.Customer;
import org.tests.model.basic.Order;
import org.tests.model.basic.ResetBasicData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestWhereAnnotation extends TransactionalTestCase {

  @Test
  public void testWhere() {

    Customer custTest = ResetBasicData.createCustAndOrder("testWhereAnn");

    Customer customer = Ebean.find(Customer.class, custTest.getId());
    List<Order> orders = customer.getOrders();

    assertTrue(!orders.isEmpty());

    Query<Customer> q1 = Ebean.find(Customer.class).setUseCache(false).fetch("orders").where()
      .idEq(1).query();

    q1.findOne();
    String s1 = q1.getGeneratedSql();
    assertThat(s1).contains("t1.order_date is not null");
  }
}
