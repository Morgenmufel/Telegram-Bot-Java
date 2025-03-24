package renatius.node.service.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import renatius.node.entity.UpComingEvents;
import renatius.node.service.ParsingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

@Service
public class ParsingServiceImpl implements ParsingService {

    private static final String html = "https://www.ticketpro.by/";

    @Override
    public Document getHTMLDocument(String html)  {
        Document doc;
        try {
            doc = Jsoup.connect(html).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 YaBrowser/24.1.0.0 Safari/537.36").get();
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        return doc;
    }

    @Override
    public Elements getTickets(Document document) {
        Elements elements = document.getElementsByClass("upcoming-events");
        return elements;
    }
    @Override
    public ArrayList<UpComingEvents> getUpComingEvents(Elements elements) {
        Element section = elements.select("section").first();
        ArrayList<UpComingEvents> listOfEvents = new ArrayList<>();
        Elements ticketBoxes = section.getElementsByClass("ticket-box");
        Iterator<Element> elementsIterator = ticketBoxes.iterator();
        Element tmpTitile;
        Element tmpDate;
        Element tmpTime;
        Element tmpLocation;
        Element tmpPrice;
        Element tmpMPAA;
        String tmpLink;
        while (elementsIterator.hasNext()) {
            UpComingEvents  upComingEvents = new UpComingEvents();
            Element ticketBox = elementsIterator.next();
            try {
                tmpTitile = ticketBox.getElementsByClass("ticket-box__title").getFirst();
                upComingEvents.setEventName(tmpTitile.hasText() && tmpTitile != null ? tmpTitile.text() : "Уточните на сайте");
            } catch (NoSuchElementException e) {}

            try{
                tmpDate = ticketBox.getElementsByClass("ticket-box__date").getFirst();
                upComingEvents.setEventDate(tmpDate.hasText() && tmpDate != null ? tmpDate.text() : "Уточните на сайте");
            }catch (NoSuchElementException e){}

            try {
                tmpDate = ticketBox.getElementsByClass("ticket-box__date").getFirst();
                upComingEvents.setEventDate(tmpDate.hasText() && tmpDate != null ? tmpDate.text() : "Уточните на сайте");
            } catch (NoSuchElementException e) {}
            try {
                tmpTime = ticketBox.getElementsByClass("ticket-box__time").getFirst();
                upComingEvents.setEventTime(tmpTime.hasText() && tmpTime != null ? tmpTime.text() : "Уточните на сайте");
            }catch (NoSuchElementException e){}

            try {
                tmpLocation = ticketBox.getElementsByClass("ticket-box__place").getFirst();
                upComingEvents.setEventLocation(tmpLocation.hasText() && tmpLocation != null ? tmpLocation.text() : "Уточните на сайте");
            }catch (NoSuchElementException e){}
            try {
                tmpPrice = ticketBox.getElementsByClass("ticket-box__price").getFirst();
                upComingEvents.setEventPrice(tmpPrice.hasText() && tmpPrice != null ?
                        tmpPrice.text() : "Уточните на сайте");
            }catch (NoSuchElementException e){}
            try {
                tmpMPAA = ticketBox.getElementsByClass("ticket-box__age").getFirst();
                upComingEvents.setMPAA(tmpMPAA.hasText() ?
                        tmpMPAA.text() : "Уточните на сайте");
            } catch (NoSuchElementException e){}
            try {
                tmpLink = "https://www.ticketpro.by" + ticketBox.select("a").attr("href");
                upComingEvents.setLink(!tmpLink.isEmpty() && tmpLink != null ?
                        tmpLink : "Уточните на сайте");
            } catch (NoSuchElementException e){}
            listOfEvents.add(upComingEvents);
            elementsIterator.remove();
        }
        return listOfEvents;
    }
}

