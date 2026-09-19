package net.lopymine.patpat.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.lopymine.patpat.utils.mixin.ScreenWithPatPatKeybinding;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

	@Inject(at = @At("HEAD"), method = "keyPress")
	private void handleScreenWithPatPatKeybindings(
			//? if >=1.21.9 {
			long window, int action, net.minecraft.client.input.KeyEvent event, CallbackInfo ci
			//?} else {
			/*long window, int key, int scancode, int action, int modifiers, CallbackInfo ci
			 *///?}
	) {
		if (action != InputConstants.RELEASE) {
			return;
		}

		Screen screen = /*? if >=26.3 {*/ Minecraft.getInstance().gui.screen() /*?} else {*/ /*Minecraft.getInstance().screen *//*?}*/;

		if (screen instanceof ScreenWithPatPatKeybinding keybindingScreen) {
			keybindingScreen.patPat$onKeyReleased();
		}
	}

}