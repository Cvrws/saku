package cc.unknown.util.account;

public enum AccountType {
    MICROSOFT("Microsoft");

    private final String name;

    AccountType(String name) {
        this.name = name;
    }

    public static AccountType getByName(String name) {
        for (AccountType type : values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }

        return null;
    }

    public String getName() {
        return name;
    }
}
