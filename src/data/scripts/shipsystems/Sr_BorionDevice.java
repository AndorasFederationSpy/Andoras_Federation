package data.scripts.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import org.apache.log4j.Logger;

// The name is a joke I was thinking about naming it
// Supercritical Fuel Infusion please consider it
public class Sr_BorionDevice extends BaseShipSystemScript {

    public Sr_BorionDevice() {
        this.logger = Global.getLogger(Sr_BorionDevice.class);
    }

    private final Logger logger;
    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        super.apply(stats, id, state, effectLevel);
        logger.warn("Sr_BorionDevice::apply");
    }

    @Override
    public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
        logger.warn("Sr_BorionDevice::isUsable");
        return super.isUsable(system, ship);
    }


}
