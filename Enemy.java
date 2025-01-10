class Enemy extends Protagonist {
    private boolean isDodging;
    private int accuracy;

    public Enemy(String name, int healthPoints, int damage) {
        super(name, healthPoints, damage, "A mysterious entity lurking in the shadows.");
        this.accuracy = 75;
        this.isDodging = false;
    }

    @Override
    public void attack(Protagonist target) {
        int hitChance = (int) (Math.random() * 100);
        if (hitChance < accuracy) {
            int finalDamage = damage;
            target.receiveDamage(finalDamage);
        }
    }

    @Override
    public void useSkill(Protagonist target) {

    }

    @Override
    public void receiveDamage(int incomingDamage) {
        int finalDamage = incomingDamage;
        if (isDodging) {
            finalDamage /= 2;
        }
        super.receiveDamage(finalDamage);
    }

    public void setDodging(boolean dodging) {
        this.isDodging = dodging;
    }

    public boolean isDodging() {
        return isDodging;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = Math.min(100, Math.max(0, accuracy));
    }

    public int getAccuracy() {
        return accuracy;
    }


    public void decideAction(Protagonist target) {
        if (healthPoints < 30 && Math.random() < 0.2) {
            setDodging(true);
        } else {
            attack(target);
        }
    }
}
