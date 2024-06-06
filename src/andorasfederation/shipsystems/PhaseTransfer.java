package andorasfederation.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import static org.lazywizard.lazylib.combat.CombatUtils.spawnShipOrWingDirectly;

public class PhaseTransfer extends BaseShipSystemScript {
//    final static String PHASE_SHADOW_TAG = "PHASE_SHADOW";
    Logger logger = Global.getLogger(PhaseTransfer.class);
    private ShipAPI ship;
    ShipAPI phaseShadow;

    private boolean activated = false;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, ShipSystemStatsScript.State state, float effectLevel) {
        logger.warn(String.format("PhaseTransfer::apply(%s)", effectLevel));
        if (stats.getEntity() == null) {
            return;
        }

        ship = (ShipAPI) stats.getEntity();
        ShipSystemAPI system = ship.getSystem();

        if (activated && (state == ShipSystemStatsScript.State.COOLDOWN || state == ShipSystemStatsScript.State.IDLE)) {
            activated = false;
        }

        if (!activated && system.getState() == ShipSystemAPI.SystemState.IN) {
            activated = true;
            phaseShadow = spawnPhantom();
            phaseShadow.getVelocity().set(new Vector2f(ship.getVelocity()));
            phaseShadow.getLocation().set(MathUtils.getPointOnCircumference(ship.getLocation(), 10f, ship.getFacing() + 110));
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        super.unapply(stats, id);
        logger.warn("PhaseTransfer::unapply()");
        removePhantom();
    }

    private void removePhantom() {
//        Global.getCombatEngine().setPlayerShipExternal(ship);
        Global.getCombatEngine().removeEntity(phaseShadow);
        phaseShadow = null;
    }

    private ShipAPI spawnPhantom() {
        ShipVariantAPI variant = ship.getVariant().clone();
        ShipAPI phaseShadow = spawnShipOrWingDirectly(variant.getHullVariantId(), FleetMemberType.SHIP, FleetSide.PLAYER, 1f, new Vector2f(100000f, 100000f), ship.getFacing());

        Global.getCombatEngine().addEntity(phaseShadow);

        return phaseShadow;
    }
}




