package net.lkrnac.book.eiws.chapter02.rmi.spring;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import net.lkrnac.book.eiws.ProcessExecutor;
import net.lkrnac.book.eiws.RetryHandler;
import net.lkrnac.book.eiws.chapter02.rmi.spring.client.BarService;
import net.lkrnac.book.eiws.chapter02.rmi.spring.client.ClientConfiguration;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.testng.annotations.Test;

public class RmiE2ETest {

  private static final int RETRY_TIMEOUT = 4000;

  @Test
  public void testRmiCall_JavaConfig() throws IOException, InterruptedException {
    performE2eTestOnContext(ClientConfiguration.class);
  }

  @Test
  public void testRmiCall_XmlConfig() throws IOException, InterruptedException {
    performE2eTestOnContext(new ClassPathResource("foo-client-context.xml"));
  }

  private void performE2eTestOnContext(Object contextToTest)
      throws IOException, InterruptedException {
    Process process = null;
    try {
      process = new ProcessExecutor().execute("0202-spring-rmi-service.jar");

      RetryHandler<Object, ApplicationContext> retryHandler = new RetryHandler<>();
      ApplicationContext context = retryHandler.retry(SpringApplication::run,
          contextToTest, RETRY_TIMEOUT);
      BarService barService = context.getBean(BarService.class);

      String response = barService.serveBar("E2E test");
      assertEquals(response, "Bar service reponse to parameter: E2E test");
    } finally {
      process.destroyForcibly();
      process.waitFor();
    }
  }
}