package com.alet.client;

import java.awt.GraphicsEnvironment;

import com.alet.CommonProxy;
import com.alet.common.util.shape.DragShapeTriangle;
import com.alet.gui.GuiAxisIndicatorAletControl;
import com.alet.gui.GuiDisplayMeasurements;
import com.alet.gui.SubGuiTypeWriter;
import com.alet.items.ItemTapeMeasure;
import com.alet.render.string.StringRenderer;
import com.alet.render.tapemeasure.TapeRenderer;
import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.littletiles.client.LittleTilesClient;
import com.creativemd.littletiles.client.render.overlay.OverlayControl;
import com.creativemd.littletiles.client.render.overlay.OverlayRenderer.OverlayPositionType;
import com.creativemd.littletiles.common.util.shape.DragShape;
import com.creativemd.littletiles.common.util.shape.DragShapeBox;
import com.creativemd.littletiles.server.LittleTilesServer;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ALETClient extends LittleTilesServer {

	public static final DragShape triangle = new DragShapeTriangle();
	
	@Override
	public void loadSidePre() {
		super.loadSidePre();
		MinecraftForge.EVENT_BUS.register(new TapeRenderer());		
		MinecraftForge.EVENT_BUS.register(new ItemTapeMeasure());
	}
	
	@Override
	public void loadSidePost() {
		super.loadSidePost();
		DragShape.registerDragShape(triangle);
		LittleTilesClient.overlay.add(new OverlayControl(new GuiAxisIndicatorAletControl("axis"), OverlayPositionType.CENTER).setShouldRender(() -> TapeRenderer.inInv));
		LittleTilesClient.overlay.add(new OverlayControl(new GuiDisplayMeasurements("display"), OverlayPositionType.CENTER).setShouldRender(() -> TapeRenderer.inInv));
	}
	
}
