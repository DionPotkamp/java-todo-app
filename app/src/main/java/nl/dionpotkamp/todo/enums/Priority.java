package nl.dionpotkamp.todo.enums;

public enum Priority {
    High(0),
    Medium(1),
    Low(2);

    private final int value;

    Priority(int i) {
        value = i;
    }

    public int getValue() {
        return value;
    }
}
