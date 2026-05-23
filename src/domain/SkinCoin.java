package domain;

public class SkinCoin extends Coin {
    private PlayerType skinType;

    // Crea una moneda que cambia la skin del jugador al recogerla
    public SkinCoin(double x, double y, PlayerType skinType) {
        super(x, y);
        this.skinType = skinType;
    }

    @Override
    // Al recoger: reproduce sonido, revierte la skin anterior y aplica la nueva
    public void onCollect(Player player, SoundCallback sound) {
        super.onCollect(player, sound);
        player.revertToOriginalSkin();
        player.applyTemporarySkin(skinType);
    }

    @Override
    // Devuelve el tipo de skin que aplica esta moneda
    public PlayerType getSkinType() {
        return skinType;
    }
}
