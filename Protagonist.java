abstract class Protagonist {
    protected String name;
    protected int healthPoints;
    protected int maxHealthPoints;
    protected int manaPoints;
    protected int maxManaPoints;
    protected int damage;
    protected int level;
    protected int experience;
    protected String backstory;
    protected CharacterStats stats;
    protected String specialty;

    public Protagonist(String name, int hp, int mp, int damage, String backstory, CharacterStats stats, String specialty) {
        this.name = name;
        this.healthPoints = hp;
        this.maxHealthPoints = hp;
        this.manaPoints = mp;
        this.maxManaPoints = mp;
        this.damage = damage;
        this.level = 1;
        this.experience = 0;
        this.backstory = backstory;
        this.stats = stats;
        this.specialty = specialty;
    }

    public Protagonist(String name, int healthPoints, int damage, String s) {
    }

    public void receiveDamage(int damage) {
        healthPoints -= damage;
    }

    public boolean isAlive() {
        return healthPoints > 0;
    }

    public String getName() {
        return name;
    }

    public String getBackstory() {
        return backstory;
    }

    public abstract void attack(Protagonist target);
    public abstract void useSkill(Protagonist target);
}
