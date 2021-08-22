package collision.handlers;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import collision.packets.PacketCollisionS;
import net.minecraft.entity.player.EntityPlayer;

public class EventHandler
{
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void playerTick(PlayerTickEvent event) {
		if(event.phase == Phase.START || event.player == null || !event.player.world.isRemote) return;
		
		EntityPlayer player = event.player;
		
		double motionX = player.motionX;
		double motionZ = player.motionZ;
		double curMotionCombined = ((double)((int)(Math.sqrt((motionX*motionX)+(motionZ*motionZ)) * 20 * 100))) / 100;
		
		double prevMotionCombined = player.getEntityData().getDouble("prevMotionCombined");
		player.getEntityData().setDouble("prevMotionCombined", curMotionCombined);
		
		if(player.isElytraFlying()) return;//Ignore elytra since it already does its own calc
		
		double accel = prevMotionCombined-curMotionCombined;//invert for simplicity
		if(accel > 5 && player.collidedHorizontally) {//Only send if acceleration greater than 5m/s/s, to avoid spam
			PacketHandler.INSTANCE.sendToServer(new PacketCollisionS(accel));
		}
	}
}
