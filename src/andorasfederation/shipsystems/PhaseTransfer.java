package andorasfederation.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import org.apache.log4j.Logger;
import org.lwjgl.util.vector.Vector2f;

public class PhaseTransfer extends BaseShipSystemScript {
    final static String PHASE_SHADOW_TAG = "PHASE_SHADOW";
    final static String ORIGINAL_SHIP_TAG = "ORIGINAL_SHIP";
    Logger logger = Global.getLogger(PhaseTransfer.class);
    ShipAPI phaseShadow;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, ShipSystemStatsScript.State state, float effectLevel) {
        logger.warn(String.format("PhaseTransfer::apply(%s)", effectLevel));
        if (stats.getEntity() == null) {
            return;
        }

        if(phaseShadow == null && state == State.IN) {
            ShipAPI ship = (ShipAPI) stats.getEntity();
            ship.addTag(ORIGINAL_SHIP_TAG);

            if (!ship.hasTag(PHASE_SHADOW_TAG)) {
                phaseShadow = spawnPhantom(Global.getCombatEngine(), ship);
            } else {
                logger.warn("Dunno, do smthng");
            }
        }
    }

//    void transferToOriginalShip(CombatEngineAPI combatEngine, ShipAPI ship) {
//        for(ShipAPI otherShip: combatEngine.getShips()) {
//            if(ship != otherShip && ship.hasTag(ORIGINAL_SHIP_TAG) && ship.getHullSize() != FIGHTER) {
//                combatEngine.setPlayerShipExternal(otherShip);
//                if(ship.hasTag(PHASE_SHADOW_TAG)) {
//                    logger.warn("Ship with tag PHASE_SHADOW_TAG was found");
//                }
//            }
//        }
//        if(combatEngine.getPlayerShip() != ship) {
//            combatEngine.removeEntity(ship);
//        }
//    }

//    void phaseJump(CombatEngineAPI combatEngine, ShipAPI ship) {
//        for(ShipAPI otherShip: combatEngine.getShips()) {
//            if(ship != otherShip && ship.hasTag(ORIGINAL_SHIP_TAG)) {
//                combatEngine.removeEntity(otherShip);
//            }
//        }
//    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        super.unapply(stats, id);
        logger.warn("PhaseTransfer::unapply()");

        ShipAPI ship = (ShipAPI) stats.getEntity();
//        transferToOriginalShip(Global.getCombatEngine(), ship);
        if(phaseShadow != null) {
            removePhantom(Global.getCombatEngine(), ship);
        }
    }

    private void removePhantom(CombatEngineAPI combatEngine, ShipAPI ship) {
        combatEngine.setPlayerShipExternal(ship);
        for(FighterWingAPI fighter: phaseShadow.getAllWings()) {
            combatEngine.getFleetManager(ship.getOwner()).removeDeployed(fighter, true);
        }

        combatEngine.removeEntity(phaseShadow);
        phaseShadow = null;
    }

    private ShipAPI spawnPhantom(CombatEngineAPI combatEngine, ShipAPI ship) {
        ShipVariantAPI variant = ship.getVariant().clone();
        combatEngine.getFleetManager(ship.getOwner()).setSuppressDeploymentMessages(true);
//        ShipAPI phaseShadow = spawnShipOrWingDirectly(variant.getHullVariantId(), FleetMemberType.SHIP, FleetSide.PLAYER, 1f, new Vector2f(100000f, 100000f), ship.getFacing());
        FleetMemberType type = FleetMemberType.SHIP;

        FleetMemberAPI member = Global.getFactory().createFleetMember(type, variant.getHullVariantId());
        member.setOwner(ship.getOwner());
        member.getCrewComposition().addCrew(member.getNeededCrew());

        Vector2f spawnLocation = new Vector2f(ship.getLocation().x + 5 * ship.getVelocity().x,
                ship.getLocation().y + 5 * ship.getVelocity().y);

        phaseShadow = combatEngine.getFleetManager(ship.getOwner()).spawnFleetMember(member, spawnLocation, ship.getFacing(), 0f);

        phaseShadow.setCRAtDeployment(ship.getCRAtDeployment());
        phaseShadow.setCurrentCR(ship.getCurrentCR());

        combatEngine.getFleetManager(ship.getOwner()).setSuppressDeploymentMessages(false);

        phaseShadow.addTag(PHASE_SHADOW_TAG);

        combatEngine.addEntity(phaseShadow);

        // Implement 2 options by velocity if velocity is high and by facing if velocity is low

        phaseShadow.getVelocity().set(ship.getVelocity());
        combatEngine.setPlayerShipExternal(phaseShadow);
        phaseShadow.giveCommand(ShipCommand.TOGGLE_SHIELD_OR_PHASE_CLOAK, null, 0);

        return phaseShadow;
    }
}




