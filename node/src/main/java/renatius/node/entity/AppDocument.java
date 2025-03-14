package renatius.node.entity;


import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "app_document")
public class AppDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String telegramField;
    private String docName;
    @OneToOne
    private BinaryContent binaryContent;
    private String mimeType;
    private Long fileSize;
}
