package parsing;

public interface MyParser<Result> {
    Result parse(String text) throws ParseException;
    void reset();
}
