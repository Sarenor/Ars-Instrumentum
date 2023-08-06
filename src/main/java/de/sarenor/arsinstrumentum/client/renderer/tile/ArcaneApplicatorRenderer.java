package de.sarenor.arsinstrumentum.client.renderer.tile;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.sarenor.arsinstrumentum.blocks.tiles.ArcaneApplicatorTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;


public class ArcaneApplicatorRenderer extends GeoBlockRenderer<ArcaneApplicatorTile> {
    public static GeoModel<ArcaneApplicatorTile> arcane_applicator_model = new GenericModel<>("arcane_applicator");

    public ArcaneApplicatorRenderer() {
        super(arcane_applicator_model);
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(arcane_applicator_model);
    }


    @Override
    public void actuallyRender(PoseStack poseStack, ArcaneApplicatorTile animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        double x = animatable.getBlockPos().getX();
        double y = animatable.getBlockPos().getY();
        double z = animatable.getBlockPos().getZ();

        if (animatable.getStack() == null || animatable.getStack().isEmpty())
            return;

        poseStack.mulPose(Axis.YP.rotationDegrees((partialTick + (float) ClientInfo.ticksInGame) * 3f));
        Minecraft.getInstance().getItemRenderer().renderStatic(animatable.getStack(),
                ItemDisplayContext.FIXED,
                packedLight,
                packedOverlay,
                poseStack,
                bufferSource,
                animatable.getLevel(),
                (int) animatable.getBlockPos().asLong());

        poseStack.popPose();

    }
}
