package renatius.node.service;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import renatius.node.entity.UpComingEvents;

import java.io.IOException;
import java.util.ArrayList;

public interface ParsingService {
    Document getHTMLDocument(String html) throws IOException;

    Elements getTickets(Document document);

    public ArrayList<UpComingEvents> getUpComingEvents(Elements elements);
}
