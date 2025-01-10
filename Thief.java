class Thief extends Protagonist {
    public Thief(String name) {
        super(name,
                90,  // HP
                70,  // MP
                14,  // Base damage
                "A shadow walker from the Thieves' Guild, specializing in stealth and precision strikes.",
                new CharacterStats(12, 18, 10, 12, 8, 16),
                "Critical Strike Mastery");
    }

    @Override
    public void attack(Protagonist target) {

        target.receiveDamage(damage);
    }

    @Override
    public void useSkill(Protagonist target) {
        if (manaPoints >= 20) {
            target.receiveDamage(damage * 3);
            manaPoints -= 20;
        }
    }
}