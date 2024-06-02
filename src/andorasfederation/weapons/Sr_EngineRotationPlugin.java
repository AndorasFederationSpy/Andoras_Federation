package andorasfederation.weapons;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SettingsAPI;
import com.fs.starfarer.api.combat.*;
import org.lwjgl.util.vector.Vector2f;

import java.util.List;

public class Sr_EngineRotationPlugin implements EveryFrameWeaponEffectPlugin {
    FakeEngineAPI fakeEngine = null;
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI rotatingEngineDecorative) {
        ShipAPI ship = rotatingEngineDecorative.getShip();
        if(fakeEngine == null) {
            this.fakeEngine = createFakeEngine(engine, rotatingEngineDecorative);
        }

        float angularVelocity = ship.getAngularVelocity() / ship.getMaxTurnRate();
        float rotationDegrees = ship.getFacing() - angularVelocity * 35f;

        float normalisedEngineLevel = ship.getVelocity().length() / ship.getMaxSpeed();

        if(normalisedEngineLevel < 0.4f) {
            normalisedEngineLevel = 0.4f;
        } else if (1f < normalisedEngineLevel){
            normalisedEngineLevel = 1f;
        }

        fakeEngine.setAbsoluteValues(rotationDegrees, normalisedEngineLevel, ship.isAlive());
        fakeEngine.setPhased(ship.isPhased());
    }

    private FakeEngineAPI createFakeEngine(CombatEngineAPI engine, WeaponAPI rotatingEngineDecorative) {
        return new FakeEngineAPI(engine, Global.getSettings(), rotatingEngineDecorative);
    }

    // Movable ship engine for implementing rotation and other fancy tricks
    static class FakeEngineAPI {
        private final ShipAPI engineDrone;

        private float engineLevel = 1f;
        private final WeaponAPI rotatingEngineDecorative;
        private final EngineSlotAPI engineSlot;

        private final float nozzleOffset;

        public FakeEngineAPI(CombatEngineAPI engine, SettingsAPI settingsAPI, WeaponAPI rotatingEngineDecorative) {
            ShipVariantAPI engineDroneVariant = Global.getSettings().createEmptyVariant("sr_engine_drone", settingsAPI.getHullSpec("sr_engine_drone"));
            this.engineDrone = engine.createFXDrone(engineDroneVariant);
            this.rotatingEngineDecorative = rotatingEngineDecorative;
            this.nozzleOffset = rotatingEngineDecorative.getSprite().getHeight() * 0.4f;

            engineDrone.setLayer(CombatEngineLayers.FIGHTERS_LAYER);
            engineDrone.setCollisionClass(CollisionClass.NONE);
            engineDrone.setOwner(rotatingEngineDecorative.getShip().getOwner());
            List<ShipEngineControllerAPI.ShipEngineAPI> engineSlots = engineDrone.getEngineController().getShipEngines();
            if(engineSlots.isEmpty()) {
                throw new RuntimeException("Engine Drone suppose to have an engine");
            } else if(engineSlots.size() > 1) {
                throw new RuntimeException("Engine Drone suppose to have only one engine");
            }
            this.engineSlot = engineSlots.get(0).getEngineSlot();
            engine.addEntity(engineDrone);
        }

        public void setPhased(boolean isPhased) {
            engineDrone.setPhased(isPhased);
        }

        public void setAbsoluteValues(float rotationDegrees, float normalisedEngineLevel, boolean engineActive){
            if(!engineActive) {
                List<ShipEngineControllerAPI.ShipEngineAPI> engineSlots = engineDrone.getEngineController().getShipEngines();
                engineSlots.get(0).disable();
                return;
            }

            rotatingEngineDecorative.setCurrAngle(rotationDegrees);

            engineDrone.getLocation().set(getNozzleLocation(rotatingEngineDecorative.getLocation(), nozzleOffset, rotationDegrees));
            engineDrone.setFacing(rotationDegrees);

            setEngineLevel(engineDrone, engineSlot, normalisedEngineLevel);
        }

        private Vector2f getNozzleLocation(Vector2f rotatingEngineLocation, float nozzleOffset, float rotationDegrees) {
            float dx = - nozzleOffset * ((float)Math.cos(Math.PI * rotationDegrees / 180.0));
            float dy = - nozzleOffset * ((float)Math.sin(Math.PI * rotationDegrees / 180.0));

            return new Vector2f(rotatingEngineLocation.x + dx, rotatingEngineLocation.y + dy);
        }

        private void setEngineLevel(ShipAPI engineDrone, EngineSlotAPI engineSlot, float targetEngineLevel) {
            engineLevel = (9f * engineLevel + targetEngineLevel) / 10f;
            engineDrone.getEngineController().setFlameLevel(engineSlot, engineLevel);
        }
    }
}