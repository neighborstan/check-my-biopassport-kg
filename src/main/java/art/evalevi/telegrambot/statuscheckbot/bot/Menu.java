package art.evalevi.telegrambot.statuscheckbot.bot;

import lombok.Getter;

@Getter
public enum Menu {

    START("/start", "Стартовое описание"),
    HELP("/help", "Информация, ссылки"),
    CONTACT("/contact", "Контакты");

    private final String name;
    private final String description;

    Menu(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
