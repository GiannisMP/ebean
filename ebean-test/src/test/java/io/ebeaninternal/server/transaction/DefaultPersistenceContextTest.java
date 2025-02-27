package io.ebeaninternal.server.transaction;

import io.ebean.bean.PersistenceContext;
import io.ebeaninternal.server.deploy.PersistenceContextUtil;
import org.junit.jupiter.api.Test;
import org.tests.model.basic.*;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPersistenceContextTest {

  private final Customer customer42;
  private final Car car1;

  DefaultPersistenceContextTest() {
    customer42 = new Customer();
    customer42.setId(42);
    car1 = new Car();
    car1.setId(1);
  }

  private DefaultPersistenceContext pc() {
    return new DefaultPersistenceContext();
  }

  private DefaultPersistenceContext pcWith42() {
    DefaultPersistenceContext pc = pc();
    pc.put(Customer.class, 42, customer42);
    return pc;
  }

  Class<?> root(Class<?> cls) {
    return PersistenceContextUtil.root(cls);
  }

  @Test
  void put_get_withInheritance() {
    PersistenceContext pc = pc();
    pc.put(root(Vehicle.class), 1, car1);

    Object found = pc.get(root(Car.class), 1);
    assertThat(found).isSameAs(car1);
  }

  @Test
  void put_get() {
    PersistenceContext pc = pc();
    pc.put(Customer.class, customer42.getId(), customer42);

    Object found = pc.get(Customer.class, 42);
    assertThat(found).isSameAs(customer42);
  }

  @Test
  void putIfAbsent_when_absent() {
    PersistenceContext pc = pc();
    Object existing = pc.putIfAbsent(Customer.class, customer42.getId(), customer42);

    assertThat(existing).isNull();
  }

  @Test
  void putIfAbsent_when_notAbsent() {
    PersistenceContext pc = pcWith42();
    Object existing = pc.putIfAbsent(Customer.class, customer42.getId(), new Customer());

    assertThat(existing).isSameAs(customer42);
  }

  @Test
  void get_when_empty() {
    PersistenceContext pc = pc();
    Object found = pc.get(Customer.class, 42);
    assertThat(found).isNull();
  }

  @Test
  void get_when_there() {
    PersistenceContext pc = pcWith42();
    Object found = pc.get(Customer.class, 42);
    assertThat(found).isSameAs(customer42);
  }

  @Test
  void getWithOption_when_empty() {
    PersistenceContext pc = pc();
    PersistenceContext.WithOption withOption = pc.getWithOption(Customer.class, 42);
    assertThat(withOption).isNull();
  }

  @Test
  void getWithOption_when_there() {
    PersistenceContext pc = pcWith42();

    PersistenceContext.WithOption withOption = pc.getWithOption(Customer.class, 42);
    assertThat(withOption.getBean()).isSameAs(customer42);
  }

  @Test
  void getWithOption_when_deleted() {
    PersistenceContext pc = pcWith42();
    pc.deleted(Customer.class, 42);

    PersistenceContext.WithOption withOption = pc.getWithOption(Customer.class, 42);
    assertThat(withOption.isDeleted()).isTrue();
    assertThat(withOption.getBean()).isNull();
  }

  @Test
  void size_when_empty() {
    PersistenceContext pc = pc();
    assertThat(pc.size(Customer.class)).isEqualTo(0);
  }

  @Test
  void size_when_some() {
    PersistenceContext pc = pcWith42();
    assertThat(pc.size(Customer.class)).isEqualTo(1);
  }

  @Test
  void clear() {
    PersistenceContext pc = pcWith42();
    pc.clear();
    assertThat(pc.size(Customer.class)).isEqualTo(0);
  }

  @Test
  void clearClass() {
    PersistenceContext pc = pcWith42();
    pc.clear(Customer.class);
    assertThat(pc.size(Customer.class)).isEqualTo(0);
  }

  @Test
  void clearClassAndId() {
    PersistenceContext pc = pcWith42();
    pc.put(Customer.class, 43, new Customer());

    pc.clear(Customer.class, 42);
    assertThat(pc.size(Customer.class)).isEqualTo(1);

    pc.clear(Customer.class, 43);
    assertThat(pc.size(Customer.class)).isEqualTo(0);
  }

  @Test
  void beginIterate() {
    final DefaultPersistenceContext pc = pcWith42();
    final Object origCustomer42 = pc.get(Customer.class, 42);

    // act
    pc.beginIterate();
    assertThat(pc.size(Customer.class)).isEqualTo(1);

    // assert same instance (bean effectively transferred to iterator persistence context
    final Object customer42 = pc.get(Customer.class, 42);
    assertThat(customer42).isSameAs(origCustomer42);
    final PersistenceContext.WithOption option = pc.getWithOption(Customer.class, 42);
    assertThat(option.getBean()).isSameAs(origCustomer42);
    pc.endIterate();
  }

  @Test
  void beginIterate_many() throws InterruptedException {
    DefaultPersistenceContext pc = new DefaultPersistenceContext();
    addCustomers(pc, 1, 100);
    addContacts(pc, 1, 1010);
    assertThat(pc.size(Customer.class)).isEqualTo(100);
    assertThat(pc.size(Contact.class)).isEqualTo(1010);

    // act
    pc.beginIterate();
    assertThat(pc.size(Customer.class)).isEqualTo(100);
    assertThat(pc.size(Contact.class)).isEqualTo(1010);
    
    addCustomers(pc, 200, 100);
    addContacts(pc, 2000, 1010);
    
    assertThat(pc.size(Customer.class)).isEqualTo(200);
    assertThat(pc.size(Contact.class)).isEqualTo(2020);
    pc.endIterate();
    
    System.gc();
    Thread.sleep(50); // give the GC some time
    
    assertThat(pc.size(Customer.class)).isEqualTo(100);
    assertThat(pc.size(Contact.class)).isEqualTo(1010);
  }

  @Test
  void toString_sillyTest() {
    DefaultPersistenceContext pc = pcWith42();
    assertThat(pc.toString()).contains("org.tests.model.basic.Customer");
  }

  private void addCustomers(PersistenceContext pc, int start, int loop) {
    for (int i = start; i < start + loop; i++) {
      Customer bean = new Customer();
      bean.setId(i);
      pc.put(Customer.class, i, bean);
    }
  }

  private void addContacts(PersistenceContext pc, int start, int loop) {
    for (int i = start; i < start + loop; i++) {
      Contact bean = new Contact();
      bean.setId(i);
      pc.put(Contact.class, i, bean);
    }
  }
}
