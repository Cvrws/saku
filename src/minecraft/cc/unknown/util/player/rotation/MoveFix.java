package cc.unknown.util.player.rotation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
public enum MoveFix {
    OFF("Off"),
    SILENT("Silent"),
    STRICT("Strict");
	
    final String name;
    
    @Override
    public String toString() {
    	return name;
    }
}