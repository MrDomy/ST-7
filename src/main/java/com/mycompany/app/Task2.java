package com.mycompany.app;

import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public final class Task2 {
    private Task2() {
    }

    public static String run() {
        ChromeDriver driver = null;
        try {
            driver = SeleniumSupport.createChromeDriver();
            driver.get("https://api.ipify.org/?format=json");
            String json = readJson(driver);

            JSONObject obj = (JSONObject) new JSONParser().parse(json);
            String ip = String.valueOf(obj.get("ip"));
            System.out.println(ip);
            return ip;
        } catch (Exception e) {
            System.out.println("Error");
            System.out.println(e.toString());
            return "";
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private static String readJson(ChromeDriver driver) {
        List<WebElement> pres = driver.findElements(By.tagName("pre"));
        if (!pres.isEmpty()) {
            String text = pres.get(0).getText();
            if (text != null && !text.trim().isEmpty()) {
                return text.trim();
            }
        }
        return driver.findElement(By.tagName("body")).getText().trim();
    }
}
