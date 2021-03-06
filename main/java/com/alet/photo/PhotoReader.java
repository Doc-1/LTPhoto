package com.alet.photo;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

import com.alet.ALET;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import com.creativemd.littletiles.LittleTiles;
import com.creativemd.littletiles.common.block.BlockLittleDyeable;
import com.creativemd.littletiles.common.tile.LittleTileColored;
import com.creativemd.littletiles.common.tile.combine.BasicCombiner;
import com.creativemd.littletiles.common.tile.math.box.LittleBox;
import com.creativemd.littletiles.common.tile.math.vec.LittleVec;
import com.creativemd.littletiles.common.tile.preview.LittlePreview;
import com.creativemd.littletiles.common.tile.preview.LittlePreviews;
import com.creativemd.littletiles.common.util.grid.LittleGridContext;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class PhotoReader {
	
	private static int scaleX = 1;
	private static int scaleY = 1;
	private static boolean isRescale = false;
	private static boolean isBlock = false;
	
	private static InputStream load(String url) throws IOException {
		long requestTime = System.currentTimeMillis();
		URLConnection connection = new URL(url).openConnection();
		connection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
		return connection.getInputStream();
	}
	
	/** @param img
	 *            Image from a website or directory
	 * @param height
	 *            height of the photo
	 * @param width
	 *            width of the photo
	 * @return
	 *         New resized photo */
	private static BufferedImage resize(BufferedImage img, int height, int width) {
		Image tmp = img.getScaledInstance(width, height, Image.SCALE_FAST);
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return resized;
	}
	
	public static boolean imageExists(String input, boolean uploadOption) {
		InputStream in = null;
		File file = null;
		BufferedImage image = null;
		try {
			if (isBlock) {
				in = PhotoReader.class.getClassLoader().getResourceAsStream(input);
				image = ImageIO.read(in);
				isBlock = false;
			} else if (uploadOption) {
				in = load(input);
				image = ImageIO.read(in);
			} else {
				file = new File(input);
				image = ImageIO.read(file);
			}
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public static int getPixelWidth(String input, boolean uploadOption) {
		InputStream in = null;
		File file = null;
		BufferedImage image = null;
		
		try {
			if (isBlock) {
				in = PhotoReader.class.getClassLoader().getResourceAsStream(input);
				image = ImageIO.read(in);
				isBlock = false;
			} else if (uploadOption) {
				in = load(input);
				image = ImageIO.read(in);
			} else {
				file = new File(input);
				image = ImageIO.read(file);
			}
		} catch (IOException e) {
			return 0;
		}
		return image.getWidth();
	}
	
	public static int getPixelLength(String input, boolean uploadOption) {
		InputStream in = null;
		File file = null;
		BufferedImage image = null;
		
		try {
			if (isBlock) {
				in = PhotoReader.class.getClassLoader().getResourceAsStream(input);
				image = ImageIO.read(in);
				isBlock = false;
			} else if (uploadOption) {
				in = load(input);
				image = ImageIO.read(in);
			} else {
				file = new File(input);
				image = ImageIO.read(file);
			}
		} catch (IOException e) {
			return 0;
		}
		return image.getHeight();
	}
	
	/** @param input
	 *            The path that the player gives from the SubGuiPhotoImport
	 * @param uploadOption
	 *            True or False if using URL
	 * @param grid
	 *            The context or grid size of the tile.
	 * @return
	 *         Returns the NBT data for the structure */
	public static NBTTagCompound photoToNBT(String input, boolean uploadOption, int grid) throws IOException {
		InputStream in = null;
		File file = null;
		BufferedImage image = null;
		int color = 0;
		ColorAccuracy roundedImage = new ColorAccuracy();
		int maxPixelAmount = ALET.CONFIG.getMaxPixelAmount();
		
		try {
			
			if (isBlock) {
				in = PhotoReader.class.getClassLoader().getResourceAsStream(input);
				image = ImageIO.read(in);
				isBlock = false;
			} else if (uploadOption) {
				in = load(input);
				image = ImageIO.read(in);
			} else {
				file = new File(input);
				image = ImageIO.read(file);
			}
			
			if (isRescale) {
				if (!(scaleX < 1) || !(scaleY < 1)) {
					image = resize(image, scaleY, scaleX);
				}
				isRescale = false;
			}
			
			if (image != null) {
				int width = image.getWidth();
				int height = image.getHeight();
				
				byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
				boolean hasAlphaChannel = image.getAlphaRaster() != null;
				int[][] result = new int[height][width];
				System.out.println(width + ", " + height + ", " + maxPixelAmount);
				if (((width * height) <= maxPixelAmount)) {
					
					if (hasAlphaChannel) {
						final int pixelLength = 4;
						for (int pixel = 0, row = height - 1, col = 0; pixel + 3 < pixels.length; pixel += pixelLength) {
							int argb = 0;
							argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
							argb += ((int) pixels[pixel + 1] & 0xff); // blue
							argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
							argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
							result[row][col] = argb;
							col++;
							if (col == width) {
								col = 0;
								row--;
							}
						}
					} else {
						final int pixelLength = 3;
						for (int pixel = 0, row = height - 1, col = 0; pixel + 2 < pixels.length; pixel += pixelLength) {
							int argb = 0;
							argb += -16777216; // 255 alpha
							argb += ((int) pixels[pixel] & 0xff); // blue
							argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
							argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
							result[row][col] = argb;
							col++;
							if (col == width) {
								col = 0;
								row--;
							}
						}
					}
				}
				
				LittleGridContext context = LittleGridContext.get(grid);
				List<LittlePreview> tiles = new ArrayList<>();
				LittleTileColored colorTile;
				int expected = image.getWidth() * image.getHeight();
				for (int row = 0; row < result.length; row++)
					for (int col = 0; col < result[row].length; col++) {
						
						if (ALET.CONFIG.isColorAccuracy()) {
							color = ColorAccuracy.roundRGB(result[row][col]);
						} else {
							color = result[row][col];
						}
						
						if (!ColorUtils.isInvisible(color)) { // no need to add transparent tiles
							colorTile = new LittleTileColored(LittleTiles.dyeableBlock, BlockLittleDyeable.LittleDyeableType.CLEAN.getMetadata(), color);
							colorTile.setBox(new LittleBox(new LittleVec(col, row, 0)));
							tiles.add(colorTile.getPreviewTile());
						}
					}
				
				//BasicCombiner.combinePreviews(tiles); // minimize tiles used
				
				BasicCombiner.combine(tiles);
				ItemStack stack = new ItemStack(LittleTiles.recipeAdvanced); // create empty advanced recipe itemstack
				LittlePreviews previews = new LittlePreviews(context);
				for (LittlePreview tile : tiles) {
					previews.addWithoutCheckingPreview(tile);
				}
				LittlePreview.savePreview(previews, stack); // save tiles to itemstacks
				
				return stack.getTagCompound();
			}
			
		} catch (IOException e) {
			
		} finally {
			IOUtils.closeQuietly(in);
			
		}
		isBlock = false;
		isRescale = false;
		return null;
	}
	
	public static void setScale(int x, int y) {
		isRescale = true;
		scaleX = x;
		scaleY = y;
	}
	
	public static void printBlock() {
		isBlock = true;
	}
	
}

/*
 * 
 */