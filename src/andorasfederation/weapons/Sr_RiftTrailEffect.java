package andorasfederation.weapons;

import java.awt.Color;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;

public class Sr_RiftTrailEffect extends BaseEveryFrameCombatPlugin {

	protected IntervalUtil interval = new IntervalUtil(0.1f, 0.3f);
	
	protected MissileAPI missile;
	protected String loopId;
	
	
	public Sr_RiftTrailEffect(MissileAPI missile, String loopId) {
		this.missile = missile;
		this.loopId = loopId;
	}

	@Override
	public void advance(float amount, List<InputEventAPI> events) {
		if (Global.getCombatEngine().isPaused()) return;
		
		if (loopId != null) {
			Global.getSoundPlayer().playLoop(loopId, missile, 1f, missile.getBrightness(), 
					missile.getLocation(), missile.getVelocity());
		}
		
		interval.advance(amount);
		if (interval.intervalElapsed()) {
			addParticles();
		}
		
		if (missile.isExpired() || missile.didDamage() || !Global.getCombatEngine().isEntityInPlay(missile)) {
			Global.getCombatEngine().removePlugin(this);
		}
	}
	

	public void addParticles() {
		CombatEngineAPI engine = Global.getCombatEngine();
		Color c = Sr_RiftLanceEffect.getColorForDarkening(Sr_RiftCascadeEffect.STANDARD_RIFT_COLOR);
		// subtracting the standard color looks better, makes the red a bit purplish
		// inverting red to substract doesn't look as good for the trails
//		MissileSpecAPI spec = missile.getSpec();
//		c = spec.getExplosionColor();
		
		Color undercolor = Sr_RiftCascadeEffect.EXPLOSION_UNDERCOLOR;
		
		float b = missile.getCurrentBaseAlpha();
		c = Misc.scaleAlpha(c, b);
		undercolor = Misc.scaleAlpha(undercolor, b);
		
		float baseDuration = 4f;
		float size = 30f;
		size = missile.getSpec().getGlowRadius() * 0.5f;
		
		Vector2f point = new Vector2f(missile.getLocation());
		Vector2f pointOffset = new Vector2f(missile.getVelocity());
		pointOffset.scale(0.1f);
		Vector2f.add(point, pointOffset, point);
		
		Vector2f vel = new Vector2f();
		
		for (int i = 0; i < 1; i++) {
			float dur = baseDuration + baseDuration * (float) Math.random();
			//float nSize = size * (1f + 0.0f * (float) Math.random());
			//float nSize = size * (0.75f + 0.5f * (float) Math.random());
			float nSize = size;
			Vector2f pt = Misc.getPointWithinRadius(point, nSize * 0.5f);
			Vector2f v = Misc.getUnitVectorAtDegreeAngle((float) Math.random() * 360f);
			v.scale(nSize + nSize * (float) Math.random() * 0.5f);
			v.scale(0.2f);
			Vector2f.add(vel, v, v);
			
			float maxSpeed = nSize * 1.5f * 0.2f; 
			float minSpeed = nSize * 1f * 0.2f; 
			float overMin = v.length() - minSpeed;
			if (overMin > 0) {
				float durMult = 1f - overMin / (maxSpeed - minSpeed);
				if (durMult < 0.1f) durMult = 0.1f;
				dur *= 0.5f + 0.5f * durMult;
			}
			engine.addNegativeNebulaParticle(pt, v, nSize * 1f, 2f,
											0.5f, 0f, dur, c);
		}
		
		float dur = baseDuration; 
		float rampUp = 0f;
		rampUp = 0.5f;
		c = undercolor;
		for (int i = 0; i < 2; i++) {
			Vector2f loc = new Vector2f(point);
			loc = Misc.getPointWithinRadius(loc, size * 1f);
			float s = size * 3f * (0.5f + (float) Math.random() * 0.5f);
			engine.addNebulaParticle(loc, vel, s, 1.5f, rampUp, 0f, dur, c);
		}
	}
	
}
