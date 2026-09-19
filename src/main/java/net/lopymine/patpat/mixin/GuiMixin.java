package net.lopymine.patpat.mixin;

//? if >=26.3 {

import net.lopymine.patpat.client.keybinding.PatPatClientKeybindingManager;
import net.lopymine.patpat.client.keybinding.PatPatKeybinding;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

	@Inject(at = @At("HEAD"), method = "setScreen")
	private void clearPatPatKeybinding(Screen screen, CallbackInfo ci) {
		PatPatKeybinding patKeybinding = PatPatClientKeybindingManager.getPatKeybinding();
		if (patKeybinding == null) {
			return;
		}
		patKeybinding.refreshPressedState();
	}

}
//?}
