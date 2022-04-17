package org.contan_lang;

public class TestClass {

    public TestClass (String text) {
        test = new TestChildClass(text);
    }

    public TestChildClass test;

    public static class TestChildClass {

        public TestChildClass(String text) {
            this.text = text;
        }

        public String text;

        @Override
        public String toString() {
            return "TestChildClass{" +
                    "text='" + text + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TestClass{" +
                "test=" + test +
                '}';
    }
}
