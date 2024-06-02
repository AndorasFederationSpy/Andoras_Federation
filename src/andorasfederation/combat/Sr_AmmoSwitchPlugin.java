package andorasfederation.combat;

import com.fs.starfarer.api.combat.*;


public class Sr_AmmoSwitchPlugin implements OnFireEffectPlugin {

	private static final String explosiveWeaponId = "sr_livyatan_dummy";	//Id of the HE projectile version of the weapon
	private int fireCounter = 0;
	
	
	public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
		ShipAPI ship = weapon.getShip();
		
		if (fireCounter >= 2) {
			if (engine.isEntityInPlay(projectile) && !projectile.didDamage()) {
				DamagingProjectileAPI newProj = (DamagingProjectileAPI) engine.spawnProjectile(ship, weapon, explosiveWeaponId, projectile.getLocation(), projectile.getFacing(), ship.getVelocity());
				newProj.setDamageAmount(projectile.getDamageAmount());
				engine.removeEntity(projectile);
			}			
			fireCounter = 0;
		} else fireCounter++;
	}
	
	
}




