package collision.packets;

import collision.handlers.ModConfig;
import collisiondamage.core.CollisionDamage;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCollisionS implements IMessage {

	private double accel;
	
	public double getAccel() {
		return accel;
	}
	
	public PacketCollisionS() {
		this.accel = 0;
	}
	
	public PacketCollisionS(double accel) {
		this.accel = accel;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.accel = buf.readDouble();
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(accel);
	}
	
	public static class CollisionMessageHandler implements IMessageHandler<PacketCollisionS, IMessage> {
		@Override
		public IMessage onMessage(PacketCollisionS message, MessageContext ctx) {
			final EntityPlayer player = CollisionDamage.proxy.getPlayer(ctx);
			if(player==null) return null;
			
			final double accel = message.getAccel();
			
			if(accel > ModConfig.server.accelerationThreshold) {
				float damageValue = (((float)Math.round((accel - ModConfig.server.accelerationThreshold)*4*ModConfig.server.damageMultiplier))/4);//Should round to nearest 0.25 after multiplier
				
				player.playSound(damageValue > 4 ? SoundEvents.ENTITY_GENERIC_BIG_FALL : SoundEvents.ENTITY_GENERIC_SMALL_FALL, 1.0F, 1.0F);
				player.attackEntityFrom(ModConfig.server.damageTypeWall ? DamageSource.FLY_INTO_WALL : DamageSource.FALL, damageValue);
			}
			return null;
		}
	}
}
