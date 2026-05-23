package domain;

public class Coin extends Entity {
    private boolean collected = false;

    public Coin(double x, double y) {
        this(x, y, 6, 6);
    }

    protected Coin(double x, double y, double w, double h) {
        super(x, y, w, h);
    }

    // Marca la moneda como recogida
    public void collect() {
        collected = true;
    }

    // Establece el estado de recolección (usado al restaurar estado)
    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    // Devuelve true si la moneda ya fue recogida
    public boolean isCollected() {
        return collected;
    }

    // Las monedas normales son requeridas para completar el nivel
    public boolean isRequired() {
        return true;
    }

    // Efecto al recoger la moneda: reproduce sonido por defecto
    public void onCollect(Player player, SoundCallback sound) {
        sound.playCoin();
    }

    // Las monedas normales no son vidas extra
    public boolean isLife() {
        return false;
    }

    // Las monedas normales no tienen tipo de skin
    public PlayerType getSkinType() {
        return null;
    }
}
