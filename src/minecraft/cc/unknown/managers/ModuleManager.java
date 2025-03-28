package cc.unknown.managers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cc.unknown.Sakura;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.impl.combat.*;
import cc.unknown.module.impl.ghost.*;
import cc.unknown.module.impl.latency.*;
import cc.unknown.module.impl.move.*;
import cc.unknown.module.impl.other.*;
import cc.unknown.module.impl.player.*;
import cc.unknown.module.impl.visual.*;
import cc.unknown.module.impl.world.*;
import cc.unknown.util.structure.AdaptiveMap;

public final class ModuleManager {

    private AdaptiveMap<Class<Module>, Module> moduleMap = new AdaptiveMap<>();
    
    public void init() {

        // Combat
        this.put(KillAura.class, new KillAura());
        this.put(Velocity.class, new Velocity());
        this.put(Criticals.class, new Criticals());
        this.put(HitBox.class, new HitBox());
        this.put(Regen.class, new Regen());
        this.put(Misplace.class, new Misplace());
        
        // Ghost
        this.put(AimAssist.class, new AimAssist());
        this.put(HitSelect.class, new HitSelect());
        this.put(AutoClicker.class, new AutoClicker());
        this.put(JumpReset.class, new JumpReset());
        this.put(Reach.class, new Reach());
        this.put(AutoBlock.class, new AutoBlock());
        this.put(GuiClicker.class, new GuiClicker());
        this.put(KeepSprint.class, new KeepSprint());
        this.put(WTap.class, new WTap());
        
        // Latency
        this.put(PingSpoof.class, new PingSpoof());
        this.put(BackTrack.class, new BackTrack());
        this.put(TimerRange.class, new TimerRange());
        this.put(Ping.class, new Ping());
        this.put(LagRange.class, new LagRange());
        this.put(TimerManipulation.class, new TimerManipulation());
        this.put(PerfectCriticals.class, new PerfectCriticals());
        
        // Movement
        this.put(Flight.class, new Flight());
        this.put(InventoryMove.class, new InventoryMove());
        this.put(NoClip.class, new NoClip());
        this.put(AntiFire.class, new AntiFire());
        this.put(NoSlow.class, new NoSlow());
        this.put(NoJumpDelay.class, new NoJumpDelay());
        this.put(Speed.class, new Speed());
        this.put(Sneak.class, new Sneak());
        this.put(Sprint.class, new Sprint());
        this.put(Strafe.class, new Strafe());
        this.put(Clipper.class, new Clipper());
        this.put(Phase.class, new Phase());
        this.put(Parkour.class, new Parkour());
        this.put(Stuck.class, new Stuck());
        this.put(Spider.class, new Spider());
        
        // World
        this.put(Scaffold.class, new Scaffold());
        this.put(FastBreak.class, new FastBreak());
        this.put(FastPlace.class, new FastPlace());
        this.put(BridgeAssist.class, new BridgeAssist());
        this.put(LegitScaffold.class, new LegitScaffold());
        this.put(Spammer.class, new Spammer());
        this.put(CancelPackets.class, new CancelPackets());
        this.put(Breaker.class, new Breaker());
        
        // Other
        this.put(AntiCrash.class, new AntiCrash());
        this.put(AntiAFK.class, new AntiAFK());
        this.put(AutoPlay.class, new AutoPlay());
        this.put(AutoRefill.class, new AutoRefill());
        this.put(AntiStaff.class, new AntiStaff());
        this.put(AutoText.class, new AutoText());
        this.put(FPSBoost.class, new FPSBoost());
        this.put(ClientSpoofer.class, new ClientSpoofer());
        this.put(DiscordRPC.class, new DiscordRPC());
        this.put(WeedHack.class, new WeedHack());
        this.put(AutoSword.class, new AutoSword());
        this.put(MidClick.class, new MidClick());
        this.put(Insults.class, new Insults());
        this.put(MusicPlayer.class, new MusicPlayer());
        this.put(AutoLeave.class, new AutoLeave());
        this.put(MurderMystery.class, new MurderMystery());
        this.put(ChatBypass.class, new ChatBypass());
        this.put(NoGuiClose.class, new NoGuiClose());
        this.put(ChatLogger.class, new ChatLogger());
        
        // Player
        this.put(AntiBoat.class, new AntiBoat());
        this.put(AntiFireBall.class, new AntiFireBall());
        this.put(AutoPot.class, new AutoPot());
        this.put(FastBow.class, new FastBow());
        this.put(NoRotate.class, new NoRotate());
        this.put(FakeHackers.class, new FakeHackers());
        this.put(AutoArmor.class, new AutoArmor());
        this.put(NoPlaceDelay.class, new NoPlaceDelay());
        this.put(Clutch.class, new Clutch());
        this.put(AutoTool.class, new AutoTool());
        this.put(Respawn.class, new Respawn());
        this.put(Blink.class, new Blink());
        this.put(GhostHand.class, new GhostHand());
        this.put(Derp.class, new Derp());
        this.put(AntiCheat.class, new AntiCheat());
        this.put(NoClickDelay.class, new NoClickDelay());
        this.put(FastUse.class, new FastUse());
        this.put(BlockIn.class, new BlockIn());
        this.put(InventoryManager.class, new InventoryManager());
        this.put(Timer.class, new Timer());
        this.put(ChestStealer.class, new ChestStealer());
        
        // Render
        this.put(Ambience.class, new Ambience());
        this.put(Animations.class, new Animations());
        this.put(StickersAnimation.class, new StickersAnimation());
        this.put(ChestESP.class, new ChestESP());
        this.put(ClickGUI.class, new ClickGUI());
        this.put(CPSDisplay.class, new CPSDisplay());
        this.put(FPSDisplay.class, new FPSDisplay());
        this.put(FreeCam.class, new FreeCam());
        this.put(AntiBlind.class, new AntiBlind());
        this.put(FreeLook.class, new FreeLook());
        this.put(FullBright.class, new FullBright());
        this.put(NoHurtCamera.class, new NoHurtCamera());
        this.put(TargetESP.class, new TargetESP());
        this.put(PingDisplay.class, new PingDisplay());
        this.put(ArmorDisplay.class, new ArmorDisplay());
        this.put(HUD.class, new HUD());
        this.put(PacketDisplay.class, new PacketDisplay());
        this.put(KeepTabList.class, new KeepTabList());
        this.put(MotionBlur.class, new MotionBlur());
        this.put(Invisibles.class, new Invisibles());
        this.put(Trajectories.class, new Trajectories());
        this.put(ItemESP.class, new ItemESP());
        this.put(ItemPhysics.class, new ItemPhysics());
        this.put(Chams.class, new Chams());
        this.put(NameTags.class, new NameTags());
        this.put(NoCameraClip.class, new NoCameraClip());
        this.put(Stickers.class, new Stickers());
        this.put(ESP.class, new ESP());
        this.put(Streamer.class, new Streamer());
        this.put(Tracers.class, new Tracers());
        this.put(UnlimitedChat.class, new UnlimitedChat());
        this.put(ChinaHat.class, new ChinaHat());
        this.put(PointerESP.class, new PointerESP());
        this.put(Cosmetics.class, new Cosmetics());
        this.put(AppleSkin.class, new AppleSkin());
    	
        this.getAll().stream().filter(module -> module != null && module.getModuleInfo() != null && module.getModuleInfo().autoEnabled()).forEach(module -> module.setEnabled(true));
        Sakura.instance.getEventBus().register(this);
    }

    public ArrayList<Module> getAll() {
        return this.moduleMap.values();
    }

    public <T extends Module> T get(final Class<T> clazz) {
    	return (T) this.moduleMap.get(clazz);
    }
    
    public List<Module> getModulesByCategory(Category category) {
        return getAll().stream().filter(m -> m.getModuleInfo().category() == category).collect(Collectors.toList());
    }
    
    public <T extends Module> T get(final String name) {
        return (T) this.getAll().stream().filter(module -> module != null && module.getAliases() != null && Arrays.stream(module.getAliases()).anyMatch(alias -> alias.replace(" ", "").equalsIgnoreCase(name.replace(" ", "")))).findAny().orElse(null);
    }

    public void put(Class clazz, Module module) {
        this.moduleMap.put(clazz, module);
    }
    
    public void put(Class<? extends Module>[] clazzArray, Module[] moduleArray) {
        if (clazzArray.length != moduleArray.length) {
            throw new IllegalArgumentException("Class and Module arrays must have the same length");
        }
        
        for (int i = 0; i < clazzArray.length; i++) {
            this.moduleMap.put((Class<Module>) clazzArray[i], moduleArray[i]);
        }
    }

    public void remove(Module key) {
        this.moduleMap.removeValue(key);
    }

    public boolean add(final Module module) {
        this.moduleMap.put(module);
        return true;
    }
}