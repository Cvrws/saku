package cc.unknown.util.player.prediction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.Vec3;

@RequiredArgsConstructor
@Getter
public class PredictProcess {
    private final Vec3 position;
    private final float fallDistance;
    private final boolean onGround;
    private final boolean isCollidedHorizontally;
}
