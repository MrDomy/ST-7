package com.mycompany.app;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public final class Task3 {
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final Path OUTPUT_FILE = Paths.get("result", "forecast.txt");

    private Task3() {
    }

    public static String run() {
        ChromeDriver driver = null;
        try {
            driver = SeleniumSupport.createChromeDriver();
            driver.get("https://api.open-meteo.com/v1/forecast?latitude=56&longitude=44&hourly=temperature_2m,rain&current=cloud_cover&timezone=Europe%2FMoscow&forecast_days=1&wind_speed_unit=ms");
            String json = readJson(driver);
            String table = buildForecastTable(json);
            Files.createDirectories(OUTPUT_FILE.getParent());
            Files.write(OUTPUT_FILE, table.getBytes(StandardCharsets.UTF_8));
            System.out.println(table);
            return table;
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

    static String buildForecastTable(String json) throws Exception {
        JSONObject root = (JSONObject) new JSONParser().parse(json);
        JSONObject hourly = (JSONObject) root.get("hourly");
        JSONArray times = (JSONArray) hourly.get("time");
        JSONArray temperatures = (JSONArray) hourly.get("temperature_2m");
        JSONArray rain = (JSONArray) hourly.get("rain");

        StringBuilder builder = new StringBuilder();
        builder.append("| № | Дата/время | Температура | Осадки (мм) |\n");
        builder.append("| -- | ------------- | ----------- | ------------ |\n");
        for (int i = 0; i < times.size(); i++) {
            String time = LocalDateTime.parse(times.get(i).toString(), INPUT_FORMAT).format(OUTPUT_FORMAT);
            builder.append("| ")
                    .append(i + 1)
                    .append(" | ")
                    .append(time)
                    .append(" | ")
                    .append(temperatures.get(i))
                    .append(" | ")
                    .append(rain.get(i))
                    .append(" |\n");
        }
        return builder.toString();
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
