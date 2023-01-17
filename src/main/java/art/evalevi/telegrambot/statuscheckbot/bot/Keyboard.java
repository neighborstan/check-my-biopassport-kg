package art.evalevi.telegrambot.statuscheckbot.bot;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class Keyboard {

    private final ReplyKeyboardMarkup keyboardMarkup;

    public Keyboard() {
        this.keyboardMarkup = new ReplyKeyboardMarkup();
        this.keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        row.add(new KeyboardButton(City.BISHKEK.name));
        row.add(new KeyboardButton(City.OSH.name));

        keyboardRows.add(row);

        this.keyboardMarkup.setKeyboard(keyboardRows);
    }
}
