package com.ltphoto.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.List;

import org.lwjgl.util.Color;

import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiColorPicker;
import com.creativemd.creativecore.common.gui.controls.gui.GuiComboBox;
import com.creativemd.creativecore.common.gui.controls.gui.GuiTextfield;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import com.creativemd.littletiles.LittleTiles;
import com.creativemd.littletiles.common.item.ItemMultiTiles;
import com.creativemd.littletiles.common.util.grid.LittleGridContext;
import com.ltphoto.LTPhoto;
import com.ltphoto.font.FontReader;

import net.minecraft.nbt.NBTTagCompound;

public class SubGuiTypeWriter extends SubGui {
	
	public GuiTextfield textfield;
	public static List<String> names = LTPhoto.fontTypeNames;
	
	public int BLACK = ColorUtils.BLACK;
	
	@Override
	public void createControls() {
		
		Color color = ColorUtils.IntToRGBA(BLACK);
		controls.add(new GuiColorPicker("picker", 0, 40, color, LittleTiles.CONFIG.isTransparencyEnabled(getPlayer()), LittleTiles.CONFIG.getMinimumTransparency(getPlayer())));
		
		GuiComboBox contextBox = new GuiComboBox("grid", 155, 20, 15, LittleGridContext.getNames());
		contextBox.select(ItemMultiTiles.currentContext.size + "");
		controls.add(contextBox);
		
		textfield = new GuiTextfield("input", "", 20, 20, 100, 14);
		controls.add(textfield);
		
		GuiTextfield fontSize = new GuiTextfield("fontSize", "48", 128, 20, 20, 14);
		controls.add(fontSize);
		
		GuiComboBox fontType = new GuiComboBox("fontType", 20, 0, 150, names);
		int index = names.indexOf(fontType.caption);
		fontType.select(names.get(index));
		controls.add(fontType);
		
		controls.add(new GuiButton("Paste", 142, 62) {
			
			@Override
			public void onClicked(int x, int y, int button) {
				StringSelection stringSelection = new StringSelection(textfield.text);
				Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable t = clpbrd.getContents(this);
				if (t == null)
					return;
				try {
					textfield.text = (String) t.getTransferData(DataFlavor.stringFlavor);
				} catch (Exception e) {
					
				}
			}
		});
		
		controls.add(new GuiButton("Print ", 142, 41) {
			
			@Override
			public void onClicked(int x, int y, int button) {
				
				GuiColorPicker picker = (GuiColorPicker) get("picker");
				int color = ColorUtils.RGBAToInt(picker.color);
				
				GuiTextfield contextField = (GuiTextfield) get("fontSize");
				int fontSize = Integer.parseInt(contextField.text);
				
				GuiComboBox contextBox = (GuiComboBox) get("fontType");
				String font = contextBox.caption;
				
				GuiComboBox contextBox_2 = (GuiComboBox) get("grid");
				int grid = Integer.parseInt(contextBox_2.caption);
				
				try {
					NBTTagCompound nbt = FontReader.photoToNBT(textfield.text, font, grid, fontSize, color);
					sendPacketToServer(nbt);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		});
	}
	
}
