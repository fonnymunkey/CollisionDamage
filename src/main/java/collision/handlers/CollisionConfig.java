package collision.handlers;

import net.minecraftforge.common.config.Config;

public class CollisionConfig {

	@Config.Comment("How large the player's deceleration must be before they will begin taking damage. (Measured in meters per second per second")
	@Config.Name("Acceleration Threshold")
	@Config.RangeDouble(min=0.0D, max=100.0D)
	public double accelerationThreshold= 8.0D;
	
	@Config.Comment("Multiplies the damage taken when over the threshold. Default 1.0x is 1 damage per 1m/s/s over threshold.")
	@Config.Name("Damage Multiplier")
	@Config.RangeDouble(min=0.0D, max=100.0D)
	public double damageMultiplier = 1.0D;
	
	@Config.Comment("Use damage type FLY_INTO_WALL? If false, will instead use FALL. (Set this to false if you want stuff like feather-falling to affect collision damage as well)")
	@Config.Name("Damage Type Wall")
	public boolean damageTypeWall = true;
	
}
