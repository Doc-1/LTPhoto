package com.ltphoto.gui;

import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiTextBox;
import com.ltphoto.LTPhoto;

public class SubGuiErrorMessage extends SubGui {
	
	private int pixelSize;
	
	public SubGuiErrorMessage(int pixelSize) {
		super(300, 100);
		this.pixelSize = pixelSize;
	}
	
	@Override
	public void createControls() {
		controls.add(new GuiTextBox("text", "Your Image is " + pixelSize + " pixels large. It must be at most "
				+ LTPhoto.CONFIG.getMaxPixelAmount() + " pixels. You can click on \"Auto Scale Image?\" to scale"
						+ " it down to a valid" + " size.\n\nTo change the limit go to config folder and open ltphoto.json and change maxPixelAmount. Be Warned," + " as the image size increases so does the time it takes. Large photos can even freeze your game for " + "tens of minutes.", 0, 0, 294));
		
		controls.add(new GuiButton("Okay", 0, 77, 40) {
			@Override
			public void onClicked(int x, int y, int button) {
				closeGui();
			}
		});
	}
	
}
