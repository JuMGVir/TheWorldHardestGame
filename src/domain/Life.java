package domain;

public class Life extends Coin {

    // Crea una vida extra (no requerida para completar el nivel)
    public Life(double x, double y) {
        super(x, y, 8, 8);
    }

    @Override
    // Las vidas extra no cuentan para la condición de victoria
    public boolean isRequired() {
        return false;
    }

    @Override
    // Al recoger una vida: otorga un escudo al jugador
    public void onCollect(Player player, SoundCallback sound) {
        player.addShieldCharge();
        sound.playSfx();
    }

    @Override
    // Identifica esta moneda como una vida extra (renderizado especial)
    public boolean isLife() {
        return true;
    }
}
