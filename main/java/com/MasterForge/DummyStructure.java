
package com.MasterForge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.creativemd.creativecore.common.utils.type.HashMapList;
import com.creativemd.littletiles.common.action.block.LittleActionActivated;
import com.creativemd.littletiles.common.action.block.LittleActionPlaceStack;
import com.creativemd.littletiles.common.structure.connection.IStructureChildConnector;
import com.creativemd.littletiles.common.structure.exception.MissingTileEntity;
import com.creativemd.littletiles.common.structure.premade.LittleStructurePremade;
import com.creativemd.littletiles.common.structure.registry.LittleStructureType;
import com.creativemd.littletiles.common.tileentity.TileEntityLittleTiles;
import com.creativemd.littletiles.common.tiles.LittleTile;
import com.creativemd.littletiles.common.tiles.place.PlacePreviewTile;
import com.creativemd.littletiles.common.tiles.place.PlacePreviews;
import com.creativemd.littletiles.common.tiles.preview.LittlePreviews;
import com.creativemd.littletiles.common.tiles.preview.LittleTilePreview;
import com.creativemd.littletiles.common.tiles.vec.LittleTileVec;
import com.creativemd.littletiles.common.utils.grid.LittleGridContext;
import com.creativemd.littletiles.common.utils.placing.PlacementMode;
import com.creativemd.littletiles.common.utils.vec.SurroundingBox;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class DummyStructure extends LittleStructurePremade {
	
	private int seriesIndex = 2;
	private String seriesName = type.id.toString().split("_")[0];
	
	public DummyStructure(LittleStructureType type) {
		super(type);
	}
	
	@Override
	protected void loadFromNBTExtra(NBTTagCompound nbt) {}
	
	@Override
	protected void writeToNBTExtra(NBTTagCompound nbt) {}
	
	private String nextSeries() {
		int seriesAt = Integer.parseInt(type.id.toString().split("_")[1]);
		if(seriesIndex > seriesAt) {
			return seriesName + "_" + (seriesAt+1);
		}
		return "";
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, LittleTile tile, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ, LittleActionActivated action) {
		String next = nextSeries();
		if(!next.isEmpty()) {
			SurroundingBox box = new SurroundingBox(false).add(tiles.entrySet());
			long minX = box.getMinX();
			long minY = box.getMinY();
			long minZ = box.getMinZ();
			LittleGridContext context = box.getContext();
			BlockPos min = new BlockPos(context.toBlockOffset(minX), context.toBlockOffset(minY), context.toBlockOffset(minZ));
			LittleTileVec minVec = new LittleTileVec((int) (minX - (long) min.getX() * (long) context.size), (int) (minY - (long) min.getY() * (long) context.size), (int) (minZ - (long) min.getZ() * (long) context.size));

			ItemStack stack = getPremadeStack(next); // Change this line to support different states
			LittlePreviews previews = LittlePreviews.getPreview(stack, true);
			LittleTileVec previewMinVec = previews.getMinVec();
			
			for (LittleTilePreview preview : previews) {
				preview.box.sub(previewMinVec);
				preview.box.add(minVec);
			}
			
			previews.convertToSmallest();
			
			List<PlacePreviewTile> placePreviews = new ArrayList<>();
			previews.getPlacePreviews(placePreviews, null, true, LittleTileVec.ZERO);
			
			HashMap<BlockPos, PlacePreviews> splitted = LittleActionPlaceStack.getSplittedTiles(previews.context, placePreviews, min);
			//Test if the structure can be placed.
			if (LittleActionPlaceStack.canPlaceTiles(null, worldIn, splitted, PlacementMode.overwrite.getCoordsToCheck(splitted, min), PlacementMode.overwrite, (LittleTile x) -> !x.isChildOfStructure(this), false)) {
				// Remove existing structure
				this.removeStructure();
				// Places new structure
				LittleActionPlaceStack.placeTilesWithoutPlayer(worldIn, previews.context, splitted, previews.getStructure(), PlacementMode.normal, min, null, null, null, null);
			} else {
				playerIn.sendStatusMessage(new TextComponentString("Not enough space!"), true);
			}
		}
		return true;
	}

}