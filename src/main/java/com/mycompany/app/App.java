package com.mycompany.app;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class App {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("[A-Za-z0-9!@#$%^&*()_+\\-=[\\]{};':\"\\\\|,.<>/?`~]{8,}");

    public static void main(String[] args) {
        ChromeDriver driver = null;
        try {
            driver = SeleniumSupport.createChromeDriver();
            String password = generatePassword(driver);
            System.out.println(password);
        } catch (Exception e) {
            System.out.println("Error");
            System.out.println(e.toString());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }

        Task2.run();
        Task3.run();
    }

    static String generatePassword(ChromeDriver driver) {
        driver.get("https://www.calculator.net/password-generator.html");
        clickGenerateIfPresent(driver);

        String generated = readBestTextCandidate(driver);
        if (generated != null && !generated.trim().isEmpty()) {
            return generated.trim();
        }

        return readViaJavaScript(driver);
    }

    private static void clickGenerateIfPresent(ChromeDriver driver) {
        List<WebElement> controls = driver.findElements(By.cssSelector("button, input[type='button'], input[type='submit']"));
        for (WebElement control : controls) {
            String label = control.getText();
            if (label == null || label.trim().isEmpty()) {
                label = control.getAttribute("value");
            }
            if (label != null && label.toLowerCase().contains("generate")) {
                control.click();
                return;
            }
        }
    }

    private static String readBestTextCandidate(ChromeDriver driver) {
        String best = null;
        List<WebElement> fields = driver.findElements(By.cssSelector("input, textarea"));
        for (WebElement field : fields) {
            String value = field.getAttribute("value");
            if (isPasswordLike(value) && (best == null || value.length() > best.length())) {
                best = value;
            }
            String text = field.getText();
            if (isPasswordLike(text) && (best == null || text.length() > best.length())) {
                best = text;
            }
        }
        return best;
    }

    private static String readViaJavaScript(ChromeDriver driver) {
        Object result = driver.executeScript(
                "var els = document.querySelectorAll('input, textarea');" +
                "var best = '';" +
                "for (var i = 0; i < els.length; i++) {" +
                "  var v = els[i].value || els[i].getAttribute('value') || '';" +
                "  if (v.length > best.length && /[A-Za-z0-9]/.test(v)) best = v;" +
                "}" +
                "return best;");
        String value = result == null ? "" : result.toString();
        if (isPasswordLike(value)) {
            return value;
        }

        Matcher matcher = PASSWORD_PATTERN.matcher(driver.getPageSource());
        return matcher.find() ? matcher.group() : "";
    }

    private static boolean isPasswordLike(String text) {
        return text != null
                && text.length() >= 8
                && PASSWORD_PATTERN.matcher(text).find();
    }
}
