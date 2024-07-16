package andorasfederation.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import org.apache.log4j.Logger;


public class PhaseTransfer extends BaseShipSystemScript {
    Logger logger = Global.getLogger(PhaseTransfer.class);
    ShipAPI phaseShadow;
    private long longStartMillis;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, ShipSystemStatsScript.State state, float effectLevel) {
        logger.warn(String.format("PhaseTransfer::apply(%s) state == %s", effectLevel, state));

        if (stats.getEntity() == null ) {
            return;
        }
        
        CombatEngineAPI combatEngine = Global.getCombatEngine();
        ShipAPI ship = (ShipAPI)stats.getEntity();

        if (state == State.IN) {
            if(phaseShadow == null) {
                phaseShadow = spawnPhantom(combatEngine, ship);
                longStartMillis = System.currentTimeMillis();
                ship.setCollisionClass(CollisionClass.NONE);
                ship.setAlphaMult(0.5f);
                ship.setApplyExtraAlphaToEngines(true);
                ship.getMutableStats().getMaxSpeed().modifyMult("CloakEffect", 4f);
                ship.getMutableStats().getAcceleration().modifyMult("CloakEffect", 10f);
                ship.getMutableStats().getMaxTurnRate().modifyMult("CloakEffect", 4f);
                ship.getMutableStats().getTurnAcceleration().modifyMult("CloakEffect", 10f);
            }
        } else if(state == State.OUT) {
            if(phaseShadow != null) {
                if(System.currentTimeMillis() - longStartMillis > 5000) {
                    ship.getLocation().set(phaseShadow.getLocation());
                    ship.setFacing(phaseShadow.getFacing());
                    ship.getVelocity().set(phaseShadow.getVelocity());
                }
                ship.setCollisionClass(CollisionClass.SHIP);
                ship.setAlphaMult(1f);
                ship.getMutableStats().getMaxSpeed().modifyMult("CloakEffect", 1f);
                ship.getMutableStats().getAcceleration().modifyMult("CloakEffect", 1f);
                ship.getMutableStats().getMaxTurnRate().modifyMult("CloakEffect", 1f);
                ship.getMutableStats().getTurnAcceleration().modifyMult("CloakEffect", 1f);
                removePhantom(Global.getCombatEngine(), ship);
            }
        }
    }
    private void removePhantom(CombatEngineAPI combatEngine, ShipAPI ship) {
        for(FighterWingAPI fighter: phaseShadow.getAllWings()) {
            combatEngine.getFleetManager(ship.getOwner()).removeDeployed(fighter, true);
        }

        combatEngine.removeEntity(phaseShadow);
        phaseShadow = null;
    }

    private ShipAPI spawnPhantom(CombatEngineAPI combatEngine, ShipAPI ship) {
        ShipVariantAPI variant = ship.getVariant().clone();

        for(WeaponAPI weaponAPI : ship.getAllWeapons()) {
            if(!weaponAPI.getSlot().isBuiltIn()) {
                variant.addWeapon(weaponAPI.getSlot().getId(), weaponAPI.getId());
            }
        }
        variant.autoGenerateWeaponGroups();

        combatEngine.getFleetManager(ship.getOwner()).setSuppressDeploymentMessages(true);
        FleetMemberType type = FleetMemberType.SHIP;

        FleetMemberAPI member = Global.getFactory().createFleetMember(type, variant);
        member.setOwner(ship.getOwner());
        member.getCrewComposition().addCrew(member.getNeededCrew());

        phaseShadow = combatEngine.getFleetManager(ship.getOwner()).spawnFleetMember(member, ship.getLocation(), ship.getFacing(), 0f);

        phaseShadow.setCRAtDeployment(ship.getCRAtDeployment());
        phaseShadow.setCurrentCR(ship.getCurrentCR());

        phaseShadow.getVelocity().set(ship.getVelocity());

        phaseShadow.setShipSystemDisabled(true);
        phaseShadow.getMutableStats().getArmorDamageTakenMult().modifyMult("Phantom", 0.3f);
        phaseShadow.getMutableStats().getHullDamageTakenMult().modifyMult("Phantom", 0.3f);

        combatEngine.addEntity(phaseShadow);
        combatEngine.getFleetManager(ship.getOwner()).setSuppressDeploymentMessages(false);

        return phaseShadow;
    }
}




