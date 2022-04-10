package com.example.MultiBlockStructure.ReactorStructure;

import com.example.examplemod.CryIndustry;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
@SuppressWarnings("deprecation")
public class ReactorScreen extends ContainerScreen<ReactorContainer> {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(CryIndustry.MOD_ID, "textures/gui/bonemealer_gui.png");
    public ReactorScreen(ReactorContainer p_i51105_1_, PlayerInventory p_i51105_2_, ITextComponent p_i51105_3_) {
        super(p_i51105_1_, p_i51105_2_, p_i51105_3_);
        this.imageWidth = 175;
        this.imageHeight = 183;
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack stack, float p_230450_2_, int p_230450_3_, int partialTicks) {
        if (this.minecraft == null) {
            return;
        }
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bind(BACKGROUND_TEXTURE);
        int startX = (this.width - this.imageWidth) / 2;
        int startY = (this.height - this.imageHeight) / 2;
        this.blit(stack, startX, startY, 0, 0, this.imageWidth, this.imageHeight);
    }
}
