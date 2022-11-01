package de.sarenor.arsinstrumentum.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import de.sarenor.arsinstrumentum.blocks.tiles.ArcaneApplicatorTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import software.bernie.ars_nouveau.geckolib3.model.AnimatedGeoModel;
import software.bernie.ars_nouveau.geckolib3.renderers.geo.GeoBlockRenderer;

public class ArcaneApplicatorRenderer extends GeoBlockRenderer<ArcaneApplicatorTile> {
    public static AnimatedGeoModel<ArcaneApplicatorTile> arcane_applicator_model = new GenericModel<ArcaneApplicatorTile>("arcane_applicator");

    public ArcaneApplicatorRenderer(BlockEntityRendererProvider.Context p_i226006_1_) {
        super(p_i226006_1_, arcane_applicator_model);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(arcane_applicator_model);
    }

    @Override
    public void render(ArcaneApplicatorTile tileEntityIn, float partialTicks, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int packedLightIn) {
        super.render(tileEntityIn, partialTicks, matrixStack, iRenderTypeBuffer, packedLightIn);

        double x = tileEntityIn.getBlockPos().getX();
        double y = tileEntityIn.getBlockPos().getY();
        double z = tileEntityIn.getBlockPos().getZ();

        if (tileEntityIn.getStack() == null || tileEntityIn.getStack().isEmpty())
            return;

        if (tileEntityIn.entity == null || !ItemStack.matches(tileEntityIn.entity.getItem(), tileEntityIn.getStack())) {
            tileEntityIn.entity = new ItemEntity(tileEntityIn.getLevel(), x, y, z, tileEntityIn.getStack());
        }

        ItemEntity entityItem = tileEntityIn.entity;
        matrixStack.pushPose();
        tileEntityIn.frames += 1.5f * Minecraft.getInstance().getDeltaFrameTime();
        entityItem.setYHeadRot(tileEntityIn.frames);
        entityItem.age = (int) tileEntityIn.frames;
        Minecraft.getInstance().getEntityRenderDispatcher().render(entityItem, 0.5, 1.2, 0.5,
                entityItem.yRot, 2.0f, matrixStack, iRenderTypeBuffer, packedLightIn);
        matrixStack.popPose();
    }
}
