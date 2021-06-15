package collision.handlers;

import collisiondamage.core.CollisionDamage;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Config(modid = CollisionDamage.MODID)
public class ModConfig {

	@Config.Comment("Server Config")
	@Config.Name("Server")
	public static ServerConfig server = new ServerConfig();
	
	public static class ServerConfig{
		
		@Config.Comment("Collision Damage Config")
		@Config.Name("Collision Damage")
		public CollisionConfig collision = new CollisionConfig();
		
	}
	
	@Mod.EventBusSubscriber(modid = CollisionDamage.MODID)
	private static class EventHandler{
		
		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if(event.getModID().equals(CollisionDamage.MODID)) ConfigManager.sync(CollisionDamage.MODID, Config.Type.INSTANCE);
		}
		
	}
}
