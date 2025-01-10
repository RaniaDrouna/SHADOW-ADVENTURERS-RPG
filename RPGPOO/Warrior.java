class Warrior extends Protagonist {
    public Warrior(String name) {
        super(name,
                120, // HP
                50,  // MP
                12,  // Base damage
                "A battle-hardened warrior from the Northern Kingdoms, trained in the art of heavy armor and weapons.",
                new CharacterStats(18, 12, 8, 16, 10, 8),
                "Heavy Armor Mastery");
    }

    @Override
    public void attack(Protagonist target) {
      
        target.receiveDamage(damage);
    }

    @Override
    public void useSkill(Protagonist target) {
        if (manaPoints >= 15) {
            target.receiveDamage((int)(damage * 2.5));
            manaPoints -= 15;
        }
    }
}