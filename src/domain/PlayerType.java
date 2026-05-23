package domain;

public enum PlayerType {
    BLINKY {
        public double getSpeedMultiplier() { return 1.0; }
        public double getWidth() { return 10; }
        public double getHeight() { return 10; }
        public boolean hasInnateShield() { return false; }
        public PlayerType onTakeDamage() { return null; }
    },
    INKY {
        public double getSpeedMultiplier() { return 1.5; }
        public double getWidth() { return 15; }
        public double getHeight() { return 15; }
        public boolean hasInnateShield() { return false; }
        public PlayerType onTakeDamage() { return null; }
    },
    CLYDE {
        public double getSpeedMultiplier() { return 1.0; }
        public double getWidth() { return 10; }
        public double getHeight() { return 10; }
        public boolean hasInnateShield() { return true; }
        public PlayerType onTakeDamage() { return PENALIZED_CLYDE; }
    },
    PENALIZED_CLYDE {
        public double getSpeedMultiplier() { return 0.7; }
        public double getWidth() { return 10; }
        public double getHeight() { return 10; }
        public boolean hasInnateShield() { return false; }
        public PlayerType onTakeDamage() { return null; }
    };

    public abstract double getSpeedMultiplier();
    public abstract double getWidth();
    public abstract double getHeight();
    public abstract boolean hasInnateShield();
    public abstract PlayerType onTakeDamage();

    public String getDisplayColor() {
        return switch (this) {
            case BLINKY -> "180,24,27";
            case INKY -> "25,41,149";
            case CLYDE, PENALIZED_CLYDE -> "40,127,47";
        };
    }
}
