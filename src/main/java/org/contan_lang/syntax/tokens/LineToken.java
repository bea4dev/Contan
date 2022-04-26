package org.contan_lang.syntax.tokens;

public class LineToken {

    private final StringBuilder stringBuilder = new StringBuilder();

    public final int line;

    public LineToken(int line) {
        this.line = line;
    }

    private String lineText = "";

    public void append(char c) {stringBuilder.append(c);}

    public void append(String s) {stringBuilder.append(s);}

    public void build() {
        lineText = stringBuilder.toString();}

    public String getLineText() {return lineText;}

}
