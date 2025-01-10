class Ranger extends Protagonist {
    public Ranger(String name) {
        super(name,
                95,  // HP
                60,  // MP
                13,  // Base damage
                "A wilderness expert skilled in ranged combat and survival.",
                new CharacterStats(12, 16, 10, 12, 12, 12),
                "Precision Shot");
    }

    @Override
    public void attack(Protagonist target) {

        target.receiveDamage(damage);
    }

    @Override
    public void useSkill(Protagonist target) {
        if (manaPoints >= 15) {
            if (target != null && target.isAlive()) {
                target.receiveDamage((int) (damage * 2.5));
            }
            manaPoints -= 15;
        }
    }
}

