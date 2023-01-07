package art.evalevi.telegrambot.statuscheckbot.dto;

import lombok.Data;

@Data
public class PassportStatus {
    private Integer id;
    private String name;
    private String description;
    private String color;
    private Boolean subscription;
}
