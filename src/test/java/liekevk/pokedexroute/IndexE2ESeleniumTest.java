package liekevk.pokedexroute;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IndexE2ESeleniumTest {
    private WebDriver driver;

    @LocalServerPort
    private int port;

    @BeforeAll
    public static void setUpClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        var chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless=new");
        driver = new ChromeDriver(chromeOptions);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class E2ESeleniumTestConfiguration {
        @Bean
        MSSQLServerContainer mssqlServerContainer() {
            return new MSSQLServerContainer<>(DockerImageName.parse(""));
        }
    }

    @Test
    public void testIndexHasCorrectTitle() {
        driver.navigate().to(String.format("http://localhost:%s/", port));

        String title = driver.getTitle();

        assertEquals("Pokedex Route", title);
    }

    @Test
    public void testGetPokedexButtonShowsPokemonInDex() {
        driver.navigate().to(String.format("http://localhost:%s/", port));

        var pokedexData = driver.findElement(By.id("pokedexData"));
        var getPokedexButton = driver.findElement(By.id("getPokedex"));

        assertEquals(0, pokedexData.findElements(By.cssSelector(":scope > *")).size());
        assertEquals("", pokedexData.getText());

        getPokedexButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector(":scope > *"), 153));

        assertEquals(153, pokedexData.findElements(By.cssSelector(":scope > *")).size());
    }
}
