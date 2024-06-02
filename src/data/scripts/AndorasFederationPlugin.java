package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.CampaignPlugin;
import com.fs.starfarer.api.combat.ShipAIPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import data.scripts.ai.RepairDroneAI;

public class AndorasFederationPlugin extends BaseModPlugin {
    @Override
    public PluginPick<ShipAIPlugin> pickShipAI(FleetMemberAPI member, ShipAPI ship) {
         switch (ship.getHullSpec().getHullId()){
            case "sr_repairdrone":
                return new PluginPick<ShipAIPlugin>(new RepairDroneAI(ship), CampaignPlugin.PickPriority.HIGHEST);
            default:
                return super.pickShipAI(member, ship);
        }
    }
}