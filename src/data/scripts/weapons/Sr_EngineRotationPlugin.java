package data.scripts.weapons;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SettingsAPI;
import com.fs.starfarer.api.combat.*;
import org.lwjgl.util.vector.Vector2f;

import java.util.List;
import java.util.Map;

public class Sr_EngineRotationPlugin implements EveryFrameWeaponEffectPlugin {
    FakeEngineAPI fakeEngine = null;
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        ShipAPI ship = weapon.getShip();
        if(fakeEngine == null) {
            this.fakeEngine = createFakeEngine(ship, engine);
        }

        float angularVelocity = ship.getAngularVelocity() / ship.getMaxTurnRate();
        float angleDegrees = ship.getFacing() + angularVelocity * 35f;

        weapon.setCurrAngle(angleDegrees);
        float halfWeaponHeight = weapon.getSprite().getHeight() * 0.4f;
        float dx = - halfWeaponHeight * ((float)Math.cos(Math.PI * angleDegrees / 180.0));
        float dy = - halfWeaponHeight * ((float)Math.sin(Math.PI * angleDegrees / 180.0));

        Vector2f nozzleLocation = new Vector2f(weapon.getLocation().x + dx, weapon.getLocation().y + dy);

        float levelCopy = ship.getVelocity().length() / ship.getMaxSpeed();

        fakeEngine.setAbsoluteValues(nozzleLocation, angleDegrees, levelCopy);
    }

    // Movable ship engine for implementing rotation and other fancy tricks
    static class FakeEngineAPI {
        private final ShipAPI engineDrone;
        private final EngineSlotAPI engineSlot;

        public FakeEngineAPI(ShipAPI owner, CombatEngineAPI engine, SettingsAPI settingsAPI) {
            ShipVariantAPI engineDroneVariant = Global.getSettings().createEmptyVariant("sr_engine_drone", settingsAPI.getHullSpec("sr_engine_drone"));
            this.engineDrone = engine.createFXDrone(engineDroneVariant);
            engineDrone.setLayer(CombatEngineLayers.FIGHTERS_LAYER);
            engineDrone.setCollisionClass(CollisionClass.NONE);
            engineDrone.setOwner(owner.getOwner());
            List<ShipEngineControllerAPI.ShipEngineAPI> engineSlots = engineDrone.getEngineController().getShipEngines();
            if(engineSlots.isEmpty()) {
                throw new RuntimeException("Engine Drone suppose to have an engine");
            } else if(engineSlots.size() > 1) {
                throw new RuntimeException("Engine Drone suppose to have only one engine");
            }
            this.engineSlot = engineSlots.get(0).getEngineSlot();
            engine.addEntity(engineDrone);
        }

        public void setAbsoluteValues(Vector2f position, float rotationDegrees, float normalisedEngineLevel){
            engineDrone.getLocation().set(position);
            engineDrone.setFacing(rotationDegrees);
            engineDrone.getEngineController().setFlameLevel(engineSlot, normalisedEngineLevel);
        }
    }

    private FakeEngineAPI createFakeEngine(ShipAPI ship, CombatEngineAPI engine) {
        return new FakeEngineAPI(ship, engine, Global.getSettings());
    }
}