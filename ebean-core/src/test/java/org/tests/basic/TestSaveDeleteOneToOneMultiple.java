package org.tests.basic;

import io.ebean.BaseTestCase;
import io.ebean.Ebean;
import org.tests.model.basic.PFile;
import org.tests.model.basic.PFileContent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestSaveDeleteOneToOneMultiple extends BaseTestCase {

//	public void testCreateDeletePFile() {
//		PFile persistentFile = new PFile("test.txt",
//				new PFileContent("test".getBytes()));
//
//		Ebean.save(persistentFile);
//		Ebean.delete(persistentFile);
//	}

  @Test
  public void testCreateLoadDeletePFile() {
    PFile persistentFile = new PFile("test.txt",
      new PFileContent("test".getBytes()));

    Ebean.save(persistentFile);

    persistentFile = Ebean.find(PFile.class, persistentFile.getId());

    PFileContent persistentFileContent = persistentFile.getFileContent();

    assertNotNull(persistentFileContent);
    assertNotNull(persistentFileContent.getContent());
    Ebean.delete(persistentFile);
  }
}
