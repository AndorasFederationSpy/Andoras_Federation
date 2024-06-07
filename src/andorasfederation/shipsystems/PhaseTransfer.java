package andorasfederation.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.ui.V;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import static org.lazywizard.lazylib.combat.CombatUtils.spawnShipOrWingDirectly;

public class PhaseTransfer extends BaseShipSystemScript {
    final static String PHASE_SHADOW_TAG = "PHASE_SHADOW";
    Logger logger = Global.getLogger(PhaseTransfer.class);
    ShipAPI phaseShadow;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, ShipSystemStatsScript.State state, float effectLevel) {
        logger.warn(String.format("PhaseTransfer::apply(%s)", effectLevel));
        if (stats.getEntity() == null) {
            return;
        }

        ShipAPI ship = (ShipAPI) stats.getEntity();

//        if(ship.hasTag(PHASE_SHADOW_TAG) && !ship.isPhased()){
//            phaseJump(Global.getCombatEngine(), ship);
//        }

        if(phaseShadow == null && state == State.IN) {
            if (ship.hasTag(PHASE_SHADOW_TAG)) {
//                transferToOriginalShip(Global.getCombatEngine(), ship);
            } else {
                phaseShadow = spawnPhantom(Global.getCombatEngine(), ship);
                ship.setShipSystemDisabled(true);
            }
        }
    }

    void transferToOriginalShip(CombatEngineAPI combatEngine, ShipAPI ship) {
        for(ShipAPI otherShip: combatEngine.getShips()) {
            if(ship != otherShip && ship.getHullSpec().getHullId().equals(ship.getHullSpec().getHullId())) {
                combatEngine.setPlayerShipExternal(otherShip);
            }
        }
        if(combatEngine.getPlayerShip() != ship) {
            combatEngine.removeEntity(ship);
        }
    }

    void phaseJump(CombatEngineAPI combatEngine, ShipAPI ship) {
        for(ShipAPI otherShip: combatEngine.getShips()) {
            if(ship != otherShip && ship.getHullSpec().getHullId().equals(ship.getHullSpec().getHullId())) {
                combatEngine.removeEntity(otherShip);
            }
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        super.unapply(stats, id);
        logger.warn("PhaseTransfer::unapply()");
        ShipAPI ship = (ShipAPI) stats.getEntity();
//        phaseJump(Global.getCombatEngine(), ship);
        removePhantom();
    }

    private void removePhantom() {
//        Global.getCombatEngine().setPlayerShipExternal(ship);
        Global.getCombatEngine().removeEntity(phaseShadow);
        phaseShadow = null;
    }

    private ShipAPI spawnPhantom(CombatEngineAPI combatEngine, ShipAPI ship) {
        ShipVariantAPI variant = ship.getVariant().clone();
        combatEngine.getFleetManager(ship.getOwner()).setSuppressDeploymentMessages(true);
        ShipAPI phaseShadow = spawnShipOrWingDirectly(variant.getHullVariantId(), FleetMemberType.SHIP, FleetSide.PLAYER, 1f, new Vector2f(100000f, 100000f), ship.getFacing());
        combatEngine.getFleetManager(ship.getOwner()).setSuppressDeploymentMessages(false);

        phaseShadow.addTag("PHASE_SHADOW_TAG");

        combatEngine.addEntity(phaseShadow);

        // Implement 2 options by velocity if velocity is high and by facing if velocity is low
        Vector2f spawnLocation = new Vector2f(ship.getLocation().x + ship.getVelocity().x,
                ship.getLocation().y + ship.getVelocity().y);
        phaseShadow.getLocation().set(spawnLocation);
        phaseShadow.getVelocity().set(ship.getVelocity());
//        combatEngine.setPlayerShipExternal(phaseShadow);

        phaseShadow.setPhased(true);

        return phaseShadow;
    }
}




