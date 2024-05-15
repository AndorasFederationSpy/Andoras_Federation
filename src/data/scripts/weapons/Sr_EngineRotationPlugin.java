package data.scripts.weapons;
import com.fs.starfarer.api.combat.*;

public class Sr_EngineRotationPlugin implements EveryFrameWeaponEffectPlugin {
    ShipAPI engineDrone = null;
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if(engineDrone == null) {
            this.engineDrone = createEngineDrone(engine, weapon);
        }

        ShipAPI ship = weapon.getShip();
        float angularVelocity = ship.getAngularVelocity() / ship.getMaxTurnRate();
        float angle = angularVelocity * 45f;

        for (ShipEngineControllerAPI.ShipEngineAPI shipEngine : ship.getEngineController().getShipEngines()) {
            shipEngine.getEngineSlot().setAngle(180f - angle);
            ship.getEngineController().setFlameLevel(shipEngine.getEngineSlot(), 1f + angularVelocity);
        }
        weapon.setCurrAngle(ship.getFacing() - angle);
    }

    private ShipAPI createEngineDrone(CombatEngineAPI engine, WeaponAPI weapon) {
        return null;
    }
}