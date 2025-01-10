class Mage extends Protagonist {
    public Mage(String name) {
        super(name,
                80,  // HP
                150, // MP
                15,
                "An arcane scholar from the Celestial Academy, wielding powerful elemental magic.",
                new CharacterStats(8, 10, 18, 10, 16, 10),
                "Elemental Mastery");
    }

    @Override
    public void attack(Protagonist target) {

        target.receiveDamage(damage);
    }

    @Override
    public void useSkill(Protagonist target) {
        if (manaPoints >= 25) {
            target.receiveDamage(damage * 3);
            manaPoints -= 25;
        }
    }
}