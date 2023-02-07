package art.evalevi.telegrambot.statuscheckbot.exception;

public class ServerDoesNotResponseException extends ServiceBotException {
    public ServerDoesNotResponseException() {
        super("Server with passport status data does not response");
    }
}
