class Paladin extends Protagonist {
    public Paladin(String name) {
        super(name,
                110, // HP
                80,  // MP
                11,  // Base damage
                "A holy knight devoted to the Light, combining martial prowess with divine magic.",
                new CharacterStats(16, 10, 12, 14, 14, 8),
                "Divine Protection");
    }

    @Override
    public void attack(Protagonist target) {

        target.receiveDamage(damage);
    }

    @Override
    public void useSkill(Protagonist target) {
        if (manaPoints >= 20) {
            target.receiveDamage(damage * 2);
            this.healthPoints += damage;
            manaPoints -= 20;
        }
    }
}