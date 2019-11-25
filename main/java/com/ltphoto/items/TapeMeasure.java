package com.ltphoto.items;

import java.util.List;

import javax.annotation.Nullable;

import com.creativemd.littletiles.common.tiles.vec.LittleTilePos;
import com.creativemd.littletiles.common.tiles.vec.LittleTileVec;
import com.creativemd.littletiles.common.utils.grid.LittleGridContext;
import com.ltphoto.tiles.SelectLittleTile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TapeMeasure extends Item {
	
	public LittleTilePos firstPos;
	public LittleTilePos secondPos;
	
	public Minecraft mc = Minecraft.getMinecraft();
	public EntityPlayer player;
	public World world;
	
	public Vec3d a;
	public Vec3d b;
	
	public SelectLittleTile select = new SelectLittleTile();
	public SelectLittleTile select_2 = new SelectLittleTile();
	
	public double differenceX;
	public double differenceZ;
	public double differenceY;
	
	public TapeMeasure() {
		
	}
	
	public TapeMeasure(String name) {
		setUnlocalizedName(name);
		setRegistryName(name);
		setMaxStackSize(1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add("THIS IS WIP!");
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		LittleGridContext context = LittleGridContext.get(16);
		RayTraceResult res = player.rayTrace(4.0, (float) 0.1);
		LittleTileVec vec = new LittleTileVec(context, res);
		
		this.player = player;
		this.world = worldIn;
		double cont = 1 / context.size;
		
		if (player.isSneaking()) {
			a = res.hitVec;
			firstPos = new LittleTilePos(res, context);
			select = new SelectLittleTile(firstPos, context, facing);
		} else {
			b = res.hitVec;
			secondPos = new LittleTilePos(res, context);
			select_2 = new SelectLittleTile(secondPos, context, facing);
		}
		
		if (firstPos != null && secondPos != null) {
			String side = facing.toString();
		}
		
		return EnumActionResult.PASS;
	}
	
}