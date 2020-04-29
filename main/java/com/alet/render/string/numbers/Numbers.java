package com.alet.render.string.numbers;

import java.util.ArrayList;

import com.alet.render.string.DrawCharacter.Facing;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class Numbers {
	
	public BufferBuilder bufferbuilder;
	public RenderWorldLastEvent event;
	public int numOfVecs;
	public Facing facing;
	
	public Numbers(BufferBuilder bufferbuilder, RenderWorldLastEvent event, Facing facing) {
		this.bufferbuilder = bufferbuilder;
		this.event = event;
		this.facing = facing;
	}
	
	public ArrayList<Vec3d> line(double startX, double startY, double startZ) {
		ArrayList<Vec3d> vec = new ArrayList<Vec3d>(1);
		vec.add(new Vec3d(0 + startX, 0 + startY, 0 + startZ));
		vec.add(new Vec3d(0.08 + startX, 0 + startY, 0 + startZ));
		return vec;
	}
	
	public void setBuffer(BufferBuilder bufferbuilder) {
		this.bufferbuilder = bufferbuilder;
	}
	
	public void setEvent(RenderWorldLastEvent event) {
		this.event = event;
	}
	
	public void character(char input, Vec3d start, float red, float green, float blue, float alpha) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		
		double minX = start.x;
		double minY = start.y;
		double minZ = start.z;
		
		double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
		double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
		double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();
		
		switch (input) {
		case '1':
			One one = new One(start, this.facing, 1, bufferbuilder, event);
			break;
		case '2':
			Two two = new Two(start, this.facing, 1, bufferbuilder, event);
			break;
		case '3':
			Three three = new Three(start, this.facing, 1, bufferbuilder, event);
			break;
		case '4':
			Four four = new Four(start, this.facing, 1, bufferbuilder, event);
			break;
		case '5':
			Five five = new Five(start, this.facing, 1, bufferbuilder, event);
			break;
		case '6':
			Six six = new Six(start, this.facing, 1, bufferbuilder, event);
			break;
		case '7':
			Seven seven = new Seven(start, this.facing, 1, bufferbuilder, event);
			break;
		case '8':
			Eight eight = new Eight(start, this.facing, 1, bufferbuilder, event);
			break;
		case '9':
			Nine nine = new Nine(start, this.facing, 1, bufferbuilder, event);
			break;
		case '0':
			Zero zero = new Zero(start, this.facing, 1, bufferbuilder, event);
			break;
		default:
			break;
		}
		
	}
}