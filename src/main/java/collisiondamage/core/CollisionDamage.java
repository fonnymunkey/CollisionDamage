package collisiondamage.core;

import org.apache.logging.log4j.Logger;

import collision.handlers.PacketHandler;
import collisiondamage.core.proxies.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;

@Mod(modid = CollisionDamage.MODID, version = CollisionDamage.VERSION, name = CollisionDamage.NAME)
public class CollisionDamage
{
    public static final String MODID = "collisiondamage";
    public static final String VERSION = "1.2.1";
    public static final String NAME = "CollisionDamage";
    public static final String PROXY = "collisiondamage.core.proxies";
    public static final String CHANNEL = "COLLISIONDAMAGE";
	
	@Instance(MODID)
	public static CollisionDamage instance;
	
	@SidedProxy(clientSide = PROXY + ".ClientProxy", serverSide = PROXY + ".CommonProxy")
	public static CommonProxy proxy;
	public static Logger logger;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	logger = event.getModLog();
    	PacketHandler.registerPacket();
    }
    
    @EventHandler
    public void onInit(FMLInitializationEvent event)
    {
    	proxy.registerHandlers(); 
    }
}
