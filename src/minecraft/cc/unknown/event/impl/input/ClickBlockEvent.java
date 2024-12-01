package cc.unknown.event.impl.input;

import cc.unknown.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@RequiredArgsConstructor
@Getter
public class ClickBlockEvent implements Event {
    private final BlockPos clickedBlock;
    private final EnumFacing enumFacing;
}
