/*
 * Copyright 2015-2016 inventivetalent. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and contributors and should not be interpreted as representing official policies,
 *  either expressed or implied, of anybody else.
 */

package de.inventivegames.npc.entity.insentient.villager;

import de.inventivegames.npc.NPC;
import de.inventivegames.npc.entity.NPCEntityNMS;
import de.inventivegames.npc.event.NPCControlEvent;
import de.inventivegames.npc.event.NPCInteractEvent;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class NPCVillagerEntity_v1_8_R3 extends NPCVillagerEntityBase {

	public NPCVillagerEntity_v1_8_R3(@Nonnull World world, @Nonnull Location location) throws Exception {
		super(world, location);

		this.theEntity = new NPCEntityVillager((WorldServer) this.worldServer);

		this.initialize(world, location);
	}

	protected class NPCEntityVillager extends EntityVillager implements NPCEntityNMS {

		@Override
		public NPC getNPC() {
			return NPCVillagerEntity_v1_8_R3.this;
		}

		public NPCEntityVillager(net.minecraft.server.v1_8_R3.World world) {
			super(world);
		}

		public NPCEntityVillager(net.minecraft.server.v1_8_R3.World world, int i) {
			super(world, i);
		}

		@Override
		public void t_() {
			super.t_();
			this.K();

			if (!NPCVillagerEntity_v1_8_R3.this.isFrozen()) {
				// Apply velocity
				this.motY = this.onGround ? Math.max(0.0, this.motY) : this.motY;
				this.move(this.motX, this.motY, this.motZ);
				this.motX *= 0.800000011920929;
				this.motY *= 0.800000011920929;
				this.motZ *= 0.800000011920929;
				if (NPCVillagerEntity_v1_8_R3.this.hasGravity() && !this.onGround) {
					this.motY -= NPCVillagerEntity_v1_8_R3.this.getGravity();
				}
			}

			((EntityLiving) this).m();

			NPCVillagerEntity_v1_8_R3.this.onUpdate();
		}

		@Override
		public boolean a(EntityHuman entityhuman) {
			if (NPCVillagerEntity_v1_8_R3.this.onInteract(entityhuman, NPCInteractEvent.InteractType.RIGHT_CLICK)) { return super.a(entityhuman); }
			return false;
		}

		@Override
		public boolean damageEntity(DamageSource damagesource, float f) {
			Object damager = null;
			if (damagesource instanceof EntityDamageSource) {
				damager = ((EntityDamageSource) damagesource).getEntity();
			}
			if (NPCVillagerEntity_v1_8_R3.this.onDamage(damagesource, f, damager)) { return super.damageEntity(damagesource, f); }
			return false;
		}

		@Override
		public void collide(Entity arg0) {
			if (NPCVillagerEntity_v1_8_R3.this.onCollide(arg0.getBukkitEntity())) {
				super.collide(arg0);
			}
		}

		@Override
		public void g(double d0, double d1, double d2) {
			if (NPCVillagerEntity_v1_8_R3.this.onMotion(d0, d1, d2)) {
				super.g(d0, d1, d2);
			}
		}

		@Override
		public void g(float motionSide, float motionForward) {
			if (this.passenger == null || !(this.passenger instanceof EntityHuman)) {
				super.g(motionSide, motionForward);
				this.S = 0.5f;
				return;
			}

			motionSide = ((EntityHuman) this.passenger).aZ * 0.5f;
			motionForward = ((EntityHuman) this.passenger).ba;

			if (motionForward <= 0f) {
				motionForward *= 0.25f;
			}
			motionSide *= 0.75f;

			NPCControlEvent event = new NPCControlEvent((NPC) NPCVillagerEntity_v1_8_R3.this, (Player) NPCVillagerEntity_v1_8_R3.this.getPassenger(), motionSide, motionForward);
			if (!NPCVillagerEntity_v1_8_R3.this.onControl(motionSide, motionForward, event)) { return; }
			if (event.isCancelled()) { return; }

			motionSide = event.getSidewaysMotion();
			motionForward = event.getForwardMotion();

			this.lastYaw = this.yaw = this.passenger.yaw;
			this.pitch = this.passenger.pitch - 0.5f;

			this.setYawPitch(this.yaw, this.pitch);// A non-obfuscated method?! What's wrong here??
			this.aK = this.aI = this.yaw;

			this.S = 1.0f;

			float speed = 0.25f;
			this.k(speed);
			super.g(motionSide, motionForward);
		}

		@Override
		public void die() {
			if (NPCVillagerEntity_v1_8_R3.this.onDespawn()) {
				super.die();
			}
		}

	}
}
