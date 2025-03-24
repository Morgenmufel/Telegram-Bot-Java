package renatius.node.entity;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpComingEvents {

    private String eventName;
    private String eventDate = "Уточняйте на сайте!";
    private String eventTime = "Уточняйте на сайте!";
    private String eventLocation = "Уточняйте на сайте!";
    private String eventPrice = "Уточняйте на сайте!";
    private String MPAA;
    private String link;


    @Override
    public String toString() {
        return "Название: " + this.eventName + "\n" +
                "Дата: "  + this.eventDate + "\n" +
                "Время: " + this.eventTime + "\n" +
                "Место: " + this.eventLocation + "\n" +
                "Цена: " + this.eventPrice + "\n" +
                "Возрастной рейтинг: " + this.MPAA + "\n" +
                "Ссылка: " + "\n" + this.link + "\n" + "\n";
    }
}
