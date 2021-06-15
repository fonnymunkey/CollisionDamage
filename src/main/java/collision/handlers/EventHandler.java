package collision.handlers;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;

public class EventHandler
{
	//Why can't Mojang just use a proper physics system?
	@SubscribeEvent
	public void livingUpdateStart(PlayerTickEvent event)
	{
		if(event.phase == Phase.END || event.player == null || event.player.world.isRemote || event.player.ticksExisted %2 == 1) return;
		
		EntityPlayer player = event.player;
		
		double currPosZ = player.posZ;
		double prevPosZ = player.getEntityData().getDouble("prevPosZCol");
		double currVelZ = currPosZ - prevPosZ;

		double currPosX = player.posX;
		double prevPosX = player.getEntityData().getDouble("prevPosXCol");
		double currVelX = currPosX - prevPosX;
		
		double prevVelCombined = player.getEntityData().getDouble("prevVelCombinedCol");
		double prevAccel = player.getEntityData().getDouble("prevAccelCol");
		double prevAccelSample = player.getEntityData().getDouble("prevAccelSampleCol");
		
		//Stupid compat for grapple mod because it overrides position but only updates every 3 ticks
		if(Loader.isModLoaded("grapplemod")) {
			if(currVelZ == 0.0D && currVelX == 0.0D) {
				int timeout = player.getEntityData().getInteger("grappleCollisionTimeout");
				if(timeout<3) {
					timeout++;
					player.getEntityData().setInteger("grappleCollisionTimeout", timeout);
					return;
				}
			}
			player.getEntityData().setInteger("grappleCollisionTimeout", 0);
		}
		
		player.getEntityData().setDouble("prevPosZCol", currPosZ);
		player.getEntityData().setDouble("prevPosXCol", currPosX);
		
		double currVelCombined = Math.sqrt((currVelZ * currVelZ)+(currVelX * currVelX));
		double currAccel = prevVelCombined - currVelCombined;
		double currAccelSample = prevAccel + currAccel;
		
		//System.out.println(currVelCombined*10 + " " + currAccel*10);
		
		//Mitigate damage by teleporting/respawning/wierd movement/lag (Hopefully)
		if((currAccel + currVelCombined == 0.0D && currAccel != 0.0D && currAccel < -1.0D && prevAccelSample != 0.0D) //Teleport or respawn
				|| (prevAccel < -0.5D && currAccel > 0.5D)) //Lag or grapple
		{
			currAccel = prevAccelSample - prevAccel;
			currAccelSample = currAccel;
			//System.out.println("mitigate: " + currVelCombined*10 + " " + currAccel*10);
		}
		
		player.getEntityData().setDouble("prevVelCombinedCol", currVelCombined);
		player.getEntityData().setDouble("prevAccelCol", currAccel);
		player.getEntityData().setDouble("prevAccelSampleCol", currAccelSample);
		
		if(player.getEntityData().getInteger("prevAccelSampleMitigateCol") == 0) {
			//System.out.println(player.world.getCollisionBoxes(player, player.getEntityBoundingBox().grow(0.1D, 0.0D, 0.1D)).size());
			if(player.isElytraFlying()) return;
			if(prevAccelSample>currAccelSample) {
				if(prevAccelSample*10>ModConfig.server.collision.accelerationThreshold) {
					//System.out.println(prevAccelSample*10);
					if(player.world.getCollisionBoxes(player, player.getEntityBoundingBox().grow(0.1D, 0.0D, 0.1D)).size()>0) {
						player.getEntityData().setInteger("prevAccelSampleMitigateCol", 1); //Stops the sampling from damaging twice without breaking other calculations
						float damageValue = (((float)Math.round((prevAccelSample*10 - ModConfig.server.collision.accelerationThreshold)*4*ModConfig.server.collision.damageMultiplier))/4); //Trim off damage below threshold, add multiplier, then round to nearest 0.25 damage
						player.playSound(damageValue > 4 ? SoundEvents.ENTITY_GENERIC_BIG_FALL : SoundEvents.ENTITY_GENERIC_SMALL_FALL, 1.0F, 1.0F);
						if(ModConfig.server.collision.damageTypeWall) player.attackEntityFrom(DamageSource.FLY_INTO_WALL, damageValue);
						else player.attackEntityFrom(DamageSource.FALL, damageValue);
			            //System.out.println(damageValue);
					}
				}
			}
		}
		else {
			player.getEntityData().setInteger("prevAccelSampleMitigateCol", 0);
		}
    }
}
