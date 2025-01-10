class Item {
    String name;
    String description;
    ItemType type;
    int value;

    public Item(String name, String description, ItemType type, int value) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
    }
}

enum ItemType {
    HEALTH_POTION,
    MANA_POTION,
    DAMAGE_BOOST,
    DEFENSE_BOOST
}
