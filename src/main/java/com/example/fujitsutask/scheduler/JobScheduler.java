package com.example.fujitsutask.scheduler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.example.fujitsutask.dto.StationDto;
import com.example.fujitsutask.service.StationService;
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

@EnableScheduling
@RequiredArgsConstructor
@Component
public class JobScheduler {

    private final StationService stationService;

    @Scheduled(fixedDelay = 1000 * 60 * 60)
    public void getObservations() {

        String url = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

        try {
            URLConnection connection = new URL(url).openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            Element observations = document.getDocumentElement();
            Long timestamp = Long.parseLong(observations.getAttribute("timestamp"));
            System.out.println("Timestamp: " + timestamp);

            NodeList nodeList = observations.getElementsByTagName("station");
            for (int i = 0; i < nodeList.getLength(); i++) {

                Element station = (Element) nodeList.item(i);

                String name = station.getElementsByTagName("name").item(0).getTextContent();
                if (name.equals("Tallinn-Harku") || name.equals("Tartu-Tõravere") || name.equals("Pärnu") ) {

                    Integer wmocode = parseInteger(station.getElementsByTagName("wmocode").item(0).getTextContent());
                    Double airtemperature = parseDouble(station.getElementsByTagName("airtemperature").item(0).getTextContent());
                    Double windspeed = parseDouble(station.getElementsByTagName("windspeed").item(0).getTextContent());
                    String phenomenon = station.getElementsByTagName("phenomenon").item(0).getTextContent();

                    StationDto stationDto = StationDto.builder()
                            .name(name)
                            .wmocode(wmocode)
                            .airtemperature(airtemperature)
                            .windspeed(windspeed)
                            .phenomenon(phenomenon)
                            .timestamp(timestamp).build();

                    stationService.addStation(stationDto);

                    System.out.println("-----");
                    System.out.println("Name: " + name);
                    System.out.println("WMO code: " + wmocode);
                    System.out.println("Air temperature: " + airtemperature);
                    System.out.println("Wind speed: " + windspeed);
                    System.out.println("Weather phenomenon: " + phenomenon);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Integer parseInteger(String s) {
        if (s == null || s.isEmpty()) return null;
        return Integer.parseInt(s);
    }

    private static Double parseDouble(String s) {
        if (s == null || s.isEmpty()) return null;
        return Double.parseDouble(s);
    }
}
