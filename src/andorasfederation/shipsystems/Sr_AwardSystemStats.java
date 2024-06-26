package andorasfederation.shipsystems;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;

import java.awt.*;

public class Sr_AwardSystemStats extends BaseShipSystemScript {

	public static float MANEUVER_BONUS_ACCEL = 200f;
	public static float MANEUVER_BONUS_TURN_FLAT = 15f;
	public static float MANEUVER_BONUS_TURN_PERCENT = 100f;
	public static float ROF_BONUS = 1f;
	public static float BEAM_DAMAGE = 50f;
	public static float RECOIL_BONUS = 50f;
	public static float WEAPON_TURN_BONUS_PERCENT = 50f;
        public static float FLUX_REDUCTION = 0.4f;
	
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship = null;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
		} else {
			return;
		}
		
		if (state == ShipSystemStatsScript.State.OUT) {
			stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
			stats.getMaxTurnRate().unmodify(id);
		} else {
			stats.getAcceleration().modifyPercent(id, MANEUVER_BONUS_ACCEL * effectLevel);
			stats.getDeceleration().modifyPercent(id, MANEUVER_BONUS_ACCEL * effectLevel);
			stats.getWeaponTurnRateBonus().modifyPercent(id, WEAPON_TURN_BONUS_PERCENT * effectLevel);
			
			stats.getTurnAcceleration().modifyFlat(id, MANEUVER_BONUS_TURN_FLAT * 2f * effectLevel);
			stats.getTurnAcceleration().modifyPercent(id, MANEUVER_BONUS_TURN_PERCENT * 2f * effectLevel);
			stats.getMaxTurnRate().modifyFlat(id, MANEUVER_BONUS_TURN_FLAT);
			stats.getMaxTurnRate().modifyPercent(id, MANEUVER_BONUS_TURN_PERCENT);
		}
		
		float mult = 1f + ROF_BONUS * effectLevel;
		stats.getBallisticWeaponFluxCostMod().modifyMult(id, 1 - FLUX_REDUCTION * effectLevel);
		stats.getBallisticRoFMult().modifyMult(id, mult);
		stats.getEnergyRoFMult().modifyMult(id, mult);
		stats.getMissileRoFMult().modifyMult(id, mult);
		stats.getBeamWeaponDamageMult().modifyPercent(id, BEAM_DAMAGE);
		stats.getMaxRecoilMult().modifyMult(id, 1f - (0.01f * RECOIL_BONUS));
		stats.getRecoilPerShotMult().modifyMult(id, 1f - (0.01f * RECOIL_BONUS));
		stats.getRecoilDecayMult().modifyMult(id, 1f - (0.01f * RECOIL_BONUS));
		
		Color c =  new Color(255, 50, 50);
		c = new Color(255, 97, 62, 255);
		for (WeaponAPI w : ship.getAllWeapons()) {
			if (w.isDecorative()) continue;
			w.setGlowAmount(effectLevel, c);
			w.setWeaponGlowWidthMult(0.5f);
			w.setWeaponGlowHeightMult(0.5f);
		}

//		ship.getEngineController().fadeToOtherColor(this, c, null, effectLevel, 1f);
//		ship.getEngineController().extendFlame(this, 0.0f, -1f, 1f);
//		for (ShipEngineAPI curr : ship.getEngineController().getShipEngines()) {
//			curr.
//		}
		
		
	}
	public void unapply(MutableShipStatsAPI stats, String id) {
		stats.getMaxSpeed().unmodify(id);
		stats.getMaxTurnRate().unmodify(id);
		stats.getTurnAcceleration().unmodify(id);
		stats.getAcceleration().unmodify(id);
		stats.getDeceleration().unmodify(id);
		stats.getWeaponTurnRateBonus().unmodify(id);

		stats.getBallisticWeaponFluxCostMod().unmodify(id);
		stats.getBallisticRoFMult().unmodify(id);
		stats.getEnergyRoFMult().unmodify(id);
		stats.getMissileRoFMult().unmodify(id);
		stats.getBeamWeaponDamageMult().unmodify(id);
		stats.getMaxRecoilMult().unmodify(id);
		stats.getRecoilPerShotMult().unmodify(id);
		stats.getRecoilDecayMult().unmodify(id);
	}
	
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		float rofMult = 1f + ROF_BONUS * effectLevel;
		int rof = (int) Math.round((rofMult - 1f) * 100f);
		
		int recoil = (int) Math.round(RECOIL_BONUS * effectLevel);
		int beam = (int) Math.round(BEAM_DAMAGE * effectLevel);
		
		if (index == 0) {
			return new StatusData("improved maneuverability", false);
		} else if (index == 1) {
			return new StatusData("rate of fire +" + (int) rof + "%", false);
		} else if (index == 2) {
			return new StatusData("recoil -" + (int) recoil + "%", false);
		} else if (index == 3) {
			return new StatusData("beam damage +" + (int) beam + "%", false);
		}
		
		return null;
	}
}





