package art.evalevi.telegrambot.statuscheckbot.bot;


public enum City {
    BISHKEK("Бишкек", "99601"),
    OSH("Ош", "99602");

    public final String name;
    public final String code;

    City(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
