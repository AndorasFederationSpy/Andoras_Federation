package andorasfederation.weapons;

import com.fs.starfarer.api.combat.*;

public class Sr_RougeDroneEffect extends BaseCombatLayeredRenderingPlugin implements OnFireEffectPlugin, EveryFrameWeaponEffectPlugin {
   
    
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
    }
    
    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        //weapon.getShip().setHitpoints(1f);
        //Global.getCombatEngine().applyDamage(weapon.getShip(), weapon.getShip().getLocation(), 500_000, DamageType.OTHER, 500_000, true, false, null);
        if (weapon.getShip() != null && weapon.getShip().getWing() != null && weapon.getShip().getWing().getSource() != null && weapon.getShip().getWing().getSource().getCurrRate() >= 0.34f) {
                weapon.getShip().getWing().getSource().setCurrRate(weapon.getShip().getWing().getSource().getCurrRate()*0.9f);
        }
        engine.removeEntity(weapon.getShip());
    }
}