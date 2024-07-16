package andorasfederation.combat;

import com.fs.starfarer.api.combat.*;


public class Sr_AmmoRotationPlugin implements OnFireEffectPlugin {

        private static final String explosiveWeaponId = "sr_kitchensink_dummy1";	//Id of the HE projectile version of the weapon
	private static final String energyWeaponId = "sr_kitchensink_dummy2"; //Id of the BURN projectile version of the weapon
	private static final String fragWeaponId = "sr_kitchensink_dummy3";	//Id of the FRAG projectile version of the weapon
	private int fireCounter = 0;
	
	
	public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
		ShipAPI ship = weapon.getShip();
		
		if (fireCounter >= 7 && fireCounter < 14) {
			if (engine.isEntityInPlay(projectile) && !projectile.didDamage()) {
				DamagingProjectileAPI newProj = (DamagingProjectileAPI) engine.spawnProjectile(ship, weapon, explosiveWeaponId, projectile.getLocation(), projectile.getFacing(), ship.getVelocity());
				//newProj.setDamageAmount(projectile.getDamageAmount());
				engine.removeEntity(projectile);
				fireCounter++;
			}		
		} else if (fireCounter >= 14 && fireCounter < 21) {
			if (engine.isEntityInPlay(projectile) && !projectile.didDamage()) {
				DamagingProjectileAPI newProj = (DamagingProjectileAPI) engine.spawnProjectile(ship, weapon, energyWeaponId, projectile.getLocation(), projectile.getFacing(), ship.getVelocity());
				//newProj.setDamageAmount(projectile.getDamageAmount());
				engine.removeEntity(projectile);
				fireCounter++;
			}			
		} else if (fireCounter >= 21) {
			if (engine.isEntityInPlay(projectile) && !projectile.didDamage()) {
				DamagingProjectileAPI newProj = (DamagingProjectileAPI) engine.spawnProjectile(ship, weapon, fragWeaponId, projectile.getLocation(), projectile.getFacing(), ship.getVelocity());
				//newProj.setDamageAmount(projectile.getDamageAmount());
				engine.removeEntity(projectile);
				fireCounter++;
			}
			if (fireCounter >= 24) {
				fireCounter = 0;
			}
		} else fireCounter++;
	}
	
	
}




