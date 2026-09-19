package net.lopymine.patpat.client.keybinding;

import lombok.*;
import net.lopymine.patpat.utils.TextUtils;
import net.minecraft.client.*;
import net.minecraft.network.chat.*;

//? if >=26.3 {
import org.lwjgl.sdl.SDLMouse;
//?} else {
/*import org.lwjgl.glfw.GLFW;*/
//?}

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.*;

import net.lopymine.patpat.PatPat;
import net.lopymine.patpat.client.config.PatPatClientConfig;
import net.lopymine.patpat.client.manager.PatPatClientManager;

import java.util.*;
import org.jetbrains.annotations.NotNull;

public class PatPatKeybinding extends KeyMapping {

	public static final KeybindingCombination DEFAULT_COMBINATION = getDefaultCombination();

	private static @NotNull KeybindingCombination getDefaultCombination() {
		return new KeybindingCombination(
				/*? if >=26.3 {*/ Type.KEYBOARD /*?} else {*/ /*Type.KEYSYM *//*?}*/.getOrCreate(InputConstants.KEY_LSHIFT),
				Type.MOUSE.getOrCreate(InputConstants.MOUSE_BUTTON_RIGHT)
		);
	}

	@Getter
	private final PressableKeybindingCombination combination = new PressableKeybindingCombination();
	@Getter
	private boolean binding;
	@Getter
	private boolean canStartBinding = true;

	public PatPatKeybinding(KeybindingCombination patCombination) {
		super(
				"patpat.keybinding.pat",
				/*? if >=26.3 {*/
				InputConstants.UNKNOWN.getValue(),
				/*?} else {*/
				/*-1,
				 *//*?}*/
				/*? if <1.21.9 {*//*PatPat.MOD_NAME*//*?} else {*/
				PatPatClientKeybindingManager.CATEGORY
				/*?}*/
		);

		this.combination.setAttributeKey(patCombination.getAttributeKey());
		this.combination.setKey(patCombination.getKey());
	}

	public void startBinding() {
		this.combination.setAttributeKey(null);
		this.combination.setKey(null);
		this.binding = true;
		this.canStartBinding = false;
	}

	public boolean addBindingKey(InputConstants.Key key) {
		if (this.isCanStartBinding()) {
			this.startBinding();
		}

		if (!this.isBinding()) {
			return true;
		}

		if (key.getValue() == InputConstants.KEY_ESCAPE) {
			this.refreshPressedState();
			this.combination.setKey(null);
			this.combination.setAttributeKey(null);
			return true;
		}

		if (KeybindingCombination.isAttributeKey(key.getValue())) {
			if (this.combination.isComplete()) {
				return true;
			}

			this.combination.setAttributeKey(key);
			return this.combination.isComplete();
		} else {
			this.combination.setKey(key);
			return true;
		}
	}

	public void sendBindingKeys() {
		this.saveCombination();
		this.combination.setAll(false);
		this.binding = false;
		this.canStartBinding = true;
	}

	@Override
	public void setKey(InputConstants.Key boundKey) {
		if (boundKey.equals(this.getDefaultKey())) {
			this.refreshPressedState();
			this.combination.setAttributeKey(DEFAULT_COMBINATION.getAttributeKey());
			this.combination.setKey(DEFAULT_COMBINATION.getKey());
			this.saveCombination();
		}
	}

	private void saveCombination() {
		PatPatClientConfig config = PatPatClientConfig.getInstance();
		config.getMainConfig().setPatCombination(this.combination);
		config.saveAsync();
	}

	public boolean onKeyAction(InputConstants.Key key, boolean pressed) {
		if (!this.combination.contains(key)) {
			return false;
		}

		if (this.combination.onlyOneKey()) {
			this.combination.set(key, pressed);
			this.setDown(pressed);

			if (!pressed) {
				PatPatClientManager.setPatCooldown(0);
			}

			return this.isDown();
		}

		if (!pressed) {
			this.combination.set(key, false);
			this.setDown(false);
			PatPatClientManager.setPatCooldown(0);
			return false;
		}

		this.combination.set(key, true);

		boolean allPressedState = this.combination.allPressed();
		this.setDown(allPressedState);

		return allPressedState
				&& !KeybindingCombination.isAttributeKey(key.getValue());
	}

	@Override
	public boolean isDefault() {
		return this.combination.equals(DEFAULT_COMBINATION);
	}

	@Override
	@NotNull
	public Component getTranslatedKeyMessage() {
		//? if >=1.19 {
		return this.getFullTranslatedKeyMessage();
		//?} else {
                /*if (this.combination.onlyOneKey()) {
                        return this.getFullTranslatedKeyMessage();
                } else {
                        return TextUtils.literal(this.isSelected() ? "..." : "< ... >");
                }*/
		//?}
	}

	public boolean isSelected() {
		return /*? if >=26.3 {*/ Minecraft.getInstance().gui.screen() /*?} else {*/ /*Minecraft.getInstance().screen *//*?}*/
				instanceof net.minecraft.client.gui.screens./*? if >=1.21 {*/options./*?}*/
				controls.KeyBindsScreen screen
				&& screen.selectedKey == this;
	}

	@NotNull
	public Component getFullTranslatedKeyMessage() {
		return this.combination.getCombinationLocalizedComponent(!this.isBinding());
	}

	public void refreshPressedState() {
		List<Key> keys = this.combination.getKeys();

		//? if >=26.3 {
		int mouseState = SDLMouse.SDL_GetMouseState(null, null);
		//?}

		keys.forEach(key -> {
			if (key.getType() == /*? if >=26.3 {*/ Type.KEYBOARD /*?} else {*/ /*Type.KEYSYM *//*?}*/) {

				//? if >=26.3 {
				this.combination.set(
						key,
						InputConstants.isKeyDown(key.getValue())
				);
				//?} else if <=1.21.8 {
				/*this.combination.set(key, InputConstants.isKeyDown(
						Minecraft.getInstance().getWindow().getWindow(), key.getValue()));
				*///?} else {
				/*this.combination.set(key, InputConstants.isKeyDown(
						Minecraft.getInstance().getWindow(), key.getValue()));
				*/
				//?}

			} else {

				//? if >=26.3 {
				int buttonMask = 1 << (key.getValue() - 1);

				this.combination.set(
						key,
						(mouseState & buttonMask) != 0
				);
				//?} else if <=1.21.8 {
				/*this.combination.set(key, GLFW.glfwGetMouseButton(
						Minecraft.getInstance().getWindow().getWindow(), key.getValue()) == GLFW.GLFW_PRESS);
				*///?} else {
				/*this.combination.set(key, GLFW.glfwGetMouseButton(
						Minecraft.getInstance().getWindow().handle(), key.getValue()) == GLFW.GLFW_PRESS);
				*/
				//?}
			}
		});

		boolean allPressed = this.combination.allPressed();
		this.setDown(allPressed);
	}
}