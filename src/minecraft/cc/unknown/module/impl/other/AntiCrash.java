package cc.unknown.module.impl.other;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.BooleanValue;
import lombok.Getter;

@ModuleInfo(aliases = "Anti Crash", description = "Evita cualquier ataque dirigido al cliente.", category = Category.OTHER)
public class AntiCrash extends Module {

	private final BooleanValue 
	demoCheck = new BooleanValue("Demo Check", this, true),
	explosionCheck = new BooleanValue("Explosion Check", this, true),
	log4jCheck = new BooleanValue("Log4J Check", this, true),
	particlesCheck = new BooleanValue("Particles Check", this, true),
	resourceCheck = new BooleanValue("Resource RCE Check", this, true),
	teleportCheck = new BooleanValue("Teleport Check", this, true),
	bookCheck = new BooleanValue("Book Check", this, true);
	
	public Boolean getDemo() {
		return demoCheck.getValue();
	}
	
	public Boolean getExplosion() {
		return explosionCheck.getValue();
	}
	
	public Boolean getLog4j() {
		return log4jCheck.getValue();
	}
	
	public Boolean getParticles() {
		return particlesCheck.getValue();
	}
	
	public Boolean getResource() {
		return resourceCheck.getValue();
	}
	
	public Boolean getTeleport() {
		return teleportCheck.getValue();
	}
	
	public Boolean getBook() {
		return bookCheck.getValue();
	}
}