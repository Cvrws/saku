package cc.unknown.cosmetics.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CosmeticType {
    DIMMADOME("Dimmadome"),
    TOP("Top"),
    WHITER("Whiter"),
    DOG("Dog"),
    BANDANA("Bandana"),
    GALAXY("Galaxy"),
    CRYSTAL("Crystal"),
    WITCH("Witch"),
    BLAZE("Blaze"),
    CREEPER("Creeper"),
    ENCHANTING("Enchanting"),
    ORBIT("Orbit");

    private final String name;
}