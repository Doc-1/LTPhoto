package com.ltphoto.render.string.alphabet;

import java.util.ArrayList;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class A extends Char {
	
	public A(Facing facing, BufferBuilder bufferbuilder, RenderWorldLastEvent event) {
		
	}
	
	public A(Vec3d start, Facing facing, double scale, BufferBuilder bufferbuilder, RenderWorldLastEvent event) {
		this.scale = scale;
		this.facing = facing;
		this.bufferbuilder = bufferbuilder;
		this.start = start;
		d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
		d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
		d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();
		morph(vecList());
	}
	
	public ArrayList<Vec3d> vecList() {
		ArrayList<Vec3d> vec = new ArrayList<Vec3d>();
		
		vec.add(new Vec3d(0, 0, 0));
		vec.add(new Vec3d(0.08, 0, 0.025));
		vec.add(new Vec3d(0, 0, 0.05));
		vec.add(new Vec3d(0.03, 0, 0.04));
		vec.add(new Vec3d(0.03, 0, 0.01));
		
		return vec;
	}
	
}
