package com.alet.items;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.alet.client.ALETClient;
import com.alet.client.gui.SubGuiTapeMeasure;
import com.alet.common.packet.PacketUpdateNBT;
import com.alet.common.util.TapeMeasureKeyEventHandler;
import com.alet.render.tapemeasure.TapeRenderer;
import com.alet.tiles.SelectLittleTile;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.common.utils.math.Rotation;
import com.creativemd.littletiles.LittleTiles;
import com.creativemd.littletiles.client.LittleTilesClient;
import com.creativemd.littletiles.client.gui.configure.SubGuiConfigure;
import com.creativemd.littletiles.common.api.ILittlePlacer;
import com.creativemd.littletiles.common.container.SubContainerConfigure;
import com.creativemd.littletiles.common.item.ItemMultiTiles;
import com.creativemd.littletiles.common.tile.math.vec.LittleAbsoluteVec;
import com.creativemd.littletiles.common.tile.preview.LittlePreviews;
import com.creativemd.littletiles.common.util.grid.LittleGridContext;
import com.creativemd.littletiles.common.util.place.IMarkMode;
import com.creativemd.littletiles.common.util.place.MarkMode;
import com.creativemd.littletiles.common.util.place.PlacementPosition;
import com.creativemd.littletiles.common.util.place.PlacementPreview;
import com.creativemd.littletiles.common.util.tooltip.IItemTooltip;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTapeMeasure extends Item implements ILittlePlacer, IItemTooltip {
	
	public static PosData data;
	public static int measurementType = 0;
	
	public void clear(ItemStack stack) {
		writeNBTData(stack, new NBTTagCompound());
	}
	
	/*** @param stack
	 *            The TapeMeasure the player is using
	 * @param index
	 *            What index the player is selected in the GUI */
	public void clear(ItemStack stack, int index, EntityPlayer player) {
		NBTTagCompound nbt = stack.getTagCompound();
		
		List<Integer> allIndexes = new ArrayList<Integer>();
		List<String> allMatches = new ArrayList<String>();
		index *= 2;
		
		Matcher m1 = Pattern.compile("[a-zA-Z]+" + (index + 1)).matcher(nbt.toString());
		while (m1.find()) {
			allMatches.add(m1.group());
		}
		
		Matcher m2 = Pattern.compile("[a-zA-Z]+" + (index)).matcher(nbt.toString());
		while (m2.find()) {
			allMatches.add(m2.group());
		}
		
		for (String key : allMatches) {
			if (!key.contains("context") && !key.contains("color") && !key.contains("shape"))
				nbt.removeTag(key);
		}
		PacketHandler.sendPacketToServer(new PacketUpdateNBT(stack));
	}
	
	public ItemTapeMeasure() {
		setMax(50);
	}
	
	public ItemTapeMeasure(String name) {
		setUnlocalizedName(name);
		setRegistryName(name);
		setMaxStackSize(1);
		setCreativeTab(LittleTiles.littleTab);
		
	}
	
	public void readNBTData(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
	}
	
	public NBTTagCompound getNBTData(ItemStack stack) {
		return stack.getTagCompound();
	}
	
	public void writeNBTData(ItemStack stack, NBTTagCompound nbt) {
		stack.setTagCompound(nbt);
	}
	
	public static void setMax(int maxMeasurements) {
		
	}
	
	public static int getMax() {
		return 50;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add("THIS IS IN ALPHA!\n" + "Design Will Change.\n");
	}
	
	@Override
	public boolean onRightClick(World world, EntityPlayer plr, ItemStack stack, PlacementPosition position, RayTraceResult result) {
		int index = 0;
		int contextSize = 1;
		List<String> list = LittleGridContext.getNames();
		NBTTagCompound nbt = new NBTTagCompound();
		if (stack.hasTagCompound()) {
			nbt = getNBTData(stack);
			index = nbt.getInteger("index");
			
			if (!nbt.hasKey("context" + (index * 2)))
				nbt.setInteger("context" + (index * 2), list.indexOf(ItemMultiTiles.currentContext.size + ""));
			
			contextSize = Integer.parseInt(list.get(nbt.getInteger("context" + (index * 2))));
		}
		
		LittleGridContext context = LittleGridContext.get(contextSize);
		RayTraceResult res = plr.rayTrace(6.0, (float) 0.1);
		LittleAbsoluteVec pos = new LittleAbsoluteVec(res, context);
		
		double[] posOffsetted = facingOffset(pos.getPosX(), pos.getPosY(), pos.getPosZ(), contextSize, position.facing);
		nbt.setString("x" + (index * 2), Double.toString(posOffsetted[0]));
		nbt.setString("y" + (index * 2), Double.toString(posOffsetted[1]));
		nbt.setString("z" + (index * 2), Double.toString(posOffsetted[2]));
		nbt.setString("facing" + (index * 2), position.facing.getName());
		
		writeNBTData(stack, nbt);
		PacketHandler.sendPacketToServer(new PacketUpdateNBT(stack));
		
		return false;
	}
	
	@Override
	public boolean onClickBlock(World world, EntityPlayer plr, ItemStack stack, PlacementPosition position, RayTraceResult result) {
		int index = 0;
		List<String> list = LittleGridContext.getNames();
		int contextSize = list.indexOf(ItemMultiTiles.currentContext.size + "");
		
		NBTTagCompound nbt = new NBTTagCompound();
		if (stack.hasTagCompound()) {
			nbt = getNBTData(stack);
			index = nbt.getInteger("index");
			
			if (!nbt.hasKey("context" + (index * 2)))
				nbt.setInteger("context" + (index * 2), list.indexOf(ItemMultiTiles.currentContext.size + ""));
			
			contextSize = Integer.parseInt(list.get(nbt.getInteger("context" + (index * 2))));
		}
		LittleGridContext context = LittleGridContext.get(contextSize);
		RayTraceResult res = plr.rayTrace(6.0, (float) 0.1);
		LittleAbsoluteVec pos = new LittleAbsoluteVec(res, context);
		
		double[] posOffsetted = facingOffset(pos.getPosX(), pos.getPosY(), pos.getPosZ(), contextSize, position.facing);
		
		nbt.setString("x" + ((index * 2) + 1), Double.toString(posOffsetted[0]));
		nbt.setString("y" + ((index * 2) + 1), Double.toString(posOffsetted[1]));
		nbt.setString("z" + ((index * 2) + 1), Double.toString(posOffsetted[2]));
		nbt.setString("facing" + ((index * 2) + 1), position.facing.getName());
		
		writeNBTData(stack, nbt);
		
		PacketHandler.sendPacketToServer(new PacketUpdateNBT(stack));
		
		return false;
	}
	
	public class PosData {
		public SelectLittleTile tilePosMin;
		public SelectLittleTile tilePosMax;
		public SelectLittleTile tilePosCursor;
		public RayTraceResult result;
		
		public PosData(SelectLittleTile posMin, SelectLittleTile posMax, SelectLittleTile posCursor, RayTraceResult res) {
			tilePosMin = posMin;
			tilePosMax = posMax;
			tilePosCursor = posCursor;
			result = res;
		}
	}
	
	@Override
	public void tick(EntityPlayer player, ItemStack stack, PlacementPosition position, RayTraceResult result) {
		NBTTagCompound nbt = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
		List<String> list = LittleGridContext.getNames();
		
		if (nbt.hasKey("index")) {
			int index = nbt.getInteger("index") * 2;
			int index1 = index;
			int index2 = index + 1;
			int contextSize = (nbt.hasKey("context" + index1)) ? Integer.parseInt(list.get(nbt.getInteger("context" + index1))) : ItemMultiTiles.currentContext.size;
			
			LittleAbsoluteVec pos = new LittleAbsoluteVec(result, LittleGridContext.get(contextSize));
			double[] posEdit = facingOffset(pos.getPosX(), pos.getPosY(), pos.getPosZ(), contextSize, result.sideHit);
			
			SelectLittleTile tilePosMin = new SelectLittleTile(new Vec3d(posEdit[0], posEdit[1], posEdit[2]), LittleGridContext.get(contextSize));
			SelectLittleTile tilePosMax = new SelectLittleTile(new Vec3d(posEdit[0], posEdit[1], posEdit[2]), LittleGridContext.get(contextSize));
			SelectLittleTile tilePosCursor = new SelectLittleTile(new Vec3d(posEdit[0], posEdit[1], posEdit[2]), LittleGridContext.get(contextSize));
			data = new PosData(tilePosMin, tilePosMax, tilePosCursor, result);
			
			TapeRenderer.renderCursor(nbt, index1, contextSize, tilePosCursor);
		}
	}
	
	public void onKeyPress(int pressedKey, EntityPlayer player, ItemStack stack) {
		if (pressedKey == TapeMeasureKeyEventHandler.CLEAR) {
			clear(stack, stack.getTagCompound().getInteger("index"), player);
		}
	}
	
	public static double[] facingOffset(double x, double y, double z, int contextSize, EnumFacing facing) {
		double offset = 1D / contextSize;
		switch (facing) {
		case UP:
			y -= offset;
			break;
		case EAST:
			x -= offset;
			break;
		case SOUTH:
			z -= offset;
			break;
		default:
			break;
		}
		double[] arr = { x, y, z };
		return arr;
	}
	
	@Override
	public void rotate(EntityPlayer player, ItemStack stack, Rotation rotation, boolean client) {
		NBTTagCompound nbt = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
		if (nbt.hasKey("index")) {
			int index = nbt.getInteger("index");
			
			if (rotation == Rotation.Z_CLOCKWISE)
				index--;
			if (rotation == Rotation.Z_COUNTER_CLOCKWISE)
				index++;
			
			if (index > 9)
				index = 0;
			else if (index < 0)
				index = 9;
			
			nbt.setInteger("index", index);
			
			stack.setTagCompound(nbt);
			
		}
	}
	
	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state) {
		return 0F;
	}
	
	@Override
	public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player) {
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public SubGuiConfigure getConfigureGUI(EntityPlayer player, ItemStack stack) {
		return new SubGuiTapeMeasure(stack);
	}
	
	@Override
	public SubGuiConfigure getConfigureGUIAdvanced(EntityPlayer player, ItemStack stack) {
		
		return null;
	}
	
	@Override
	public SubContainerConfigure getConfigureContainer(EntityPlayer player, ItemStack stack) {
		return new SubContainerConfigure(player, stack);
	}
	
	@Override
	public boolean hasLittlePreview(ItemStack stack) {
		return true;
	}
	
	@Override
	public LittlePreviews getLittlePreview(ItemStack stack) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void saveLittlePreview(ItemStack stack, LittlePreviews previews) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean containsIngredients(ItemStack stack) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Object[] tooltipData(ItemStack stack) {
		return new Object[] { LittleTilesClient.configure.getDisplayName(), LittleTilesClient.up.getDisplayName(), LittleTilesClient.down.getDisplayName(), ALETClient.clearMeasurment.getDisplayName() };
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMarkMode onMark(EntityPlayer player, ItemStack stack, PlacementPosition position, RayTraceResult result, PlacementPreview previews) {
		return new MarkMode(player, position, previews);
	}
	
}
