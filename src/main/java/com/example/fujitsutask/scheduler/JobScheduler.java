package com.example.fujitsutask.scheduler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.example.fujitsutask.dto.StationDto;
import com.example.fujitsutask.service.StationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@EnableScheduling
@RequiredArgsConstructor
@Component
public class JobScheduler {

    private final StationService stationService;

    /**
     * Read the XML file by URL and save the station objects to the database.
     * Required station names: Tallinn-Harku, Tartu-Tõravere, Pärnu.
     * Activate every hour in 15 minutes (HH:15:00).
     */
    @Scheduled(cron = "0 15 * * * *")
    public void getObservations() {

        String url = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

        try {
            // Connecting URL
            URLConnection connection = new URL(url).openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();

            // Creating objects to read the XML document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            // Get the root element of the XML document and its timestamp
            Element observations = document.getDocumentElement();
            Long timestamp = Long.parseLong(observations.getAttribute("timestamp"));
            log.info("Timestamp: {}", timestamp);

            // Get a list of stations only the ones we need
            NodeList nodeList = observations.getElementsByTagName("station");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element station = (Element) nodeList.item(i);
                String name = station.getElementsByTagName("name").item(0).getTextContent();
                if (name.equals("Tallinn-Harku") || name.equals("Tartu-Tõravere") || name.equals("Pärnu") ) {

                    // Get station weather data and create DTO object
                    Integer wmoCode = parseInteger(station.getElementsByTagName("wmocode").item(0).getTextContent());
                    Double airTemperature = parseDouble(station.getElementsByTagName("airtemperature").item(0).getTextContent());
                    Double windSpeed = parseDouble(station.getElementsByTagName("windspeed").item(0).getTextContent());
                    String phenomenon = station.getElementsByTagName("phenomenon").item(0).getTextContent();

                    StationDto stationDto = StationDto.builder()
                            .name(name)
                            .wmoCode(wmoCode)
                            .airTemperature(airTemperature)
                            .windSpeed(windSpeed)
                            .phenomenon(phenomenon)
                            .timestamp(timestamp).build();

                    // Save the weather data to the database
                    stationService.addStation(stationDto);
                    log.info("Saved station {}", stationDto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse the string into integer, if empty return null.
     */
    private static Integer parseInteger(String s) {
        if (s == null || s.isEmpty()) return null;
        return Integer.parseInt(s);
    }

    /**
     * Parse the string into double, if empty return null.
     */
    private static Double parseDouble(String s) {
        if (s == null || s.isEmpty()) return null;
        return Double.parseDouble(s);
    }
}
