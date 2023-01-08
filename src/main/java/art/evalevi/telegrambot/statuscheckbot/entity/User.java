package art.evalevi.telegrambot.statuscheckbot.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "tg_user")
public class User {

    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username")
    private String userName;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @CreationTimestamp
    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "requests_count")
    private Integer requestsCount;

    @Column(name = "last_request")
    private String lastRequest;

    @Column(name = "selected_city")
    private String selectedCity;
}
