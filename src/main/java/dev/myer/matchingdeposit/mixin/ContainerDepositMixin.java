package dev.myer.matchingdeposit.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class ContainerDepositMixin {
    @Shadow protected int leftPos;
    @Shadow protected int topPos;
    @Shadow protected int imageWidth;
    @Shadow public abstract AbstractContainerMenu getMenu();
    @Shadow protected abstract <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget);

    @Inject(method = "init", at = @At("TAIL"))
    private void matchingdeposit$addButton(CallbackInfo ci) {
        AbstractContainerMenu menu = this.getMenu();

        // Restrict the button to vanilla chest-like containers. ChestMenu covers
        // chests, trapped chests, barrels, and ender chests; shulkers use their
        // own menu class.
        if (!(menu instanceof ChestMenu) && !(menu instanceof ShulkerBoxMenu)) {
            return;
        }

        int x = this.leftPos + this.imageWidth - 40;
        int y = this.topPos + 4;
        Button button = Button.builder(Component.literal("D"), ignored -> matchingdeposit$deposit(menu))
                .bounds(x, y, 18, 18)
                .build();
        this.addRenderableWidget(button);
    }

    private static void matchingdeposit$deposit(AbstractContainerMenu menu) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.gameMode == null) {
            return;
        }

        // Avoid doing automated clicks while the cursor is holding an item.
        if (!menu.getCarried().isEmpty()) {
            return;
        }

        // Standard chest/shulker menus have 36 player slots at the end.
        int containerSlotCount = menu.slots.size() - 36;
        if (containerSlotCount <= 0) {
            return;
        }

        // Iterate only the 36 normal player inventory slots. A stack is moved
        // only if an equivalent stack (same item and components) is already in
        // the container. QUICK_MOVE uses the menu's normal transfer logic, so
        // stack limits and container insertion rules remain respected.
        for (int playerSlotIndex = containerSlotCount;
             playerSlotIndex < menu.slots.size();
             playerSlotIndex++) {
            Slot playerSlot = menu.slots.get(playerSlotIndex);
            ItemStack carriedFromPlayer = playerSlot.getItem();
            if (carriedFromPlayer.isEmpty()) {
                continue;
            }

            boolean matchingStackExists = false;
            for (int containerSlotIndex = 0; containerSlotIndex < containerSlotCount; containerSlotIndex++) {
                ItemStack inContainer = menu.slots.get(containerSlotIndex).getItem();
                if (!inContainer.isEmpty()
                        && ItemStack.isSameItemSameComponents(inContainer, carriedFromPlayer)) {
                    matchingStackExists = true;
                    break;
                }
            }

            if (matchingStackExists) {
                minecraft.gameMode.handleContainerInput(
                        menu.containerId,
                        playerSlotIndex,
                        0,
                        ContainerInput.QUICK_MOVE,
                        minecraft.player
                );
            }
        }
    }
}
