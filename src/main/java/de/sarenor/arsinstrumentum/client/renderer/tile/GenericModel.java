package de.sarenor.arsinstrumentum.client.renderer.tile;

import de.sarenor.arsinstrumentum.ArsInstrumentum;
import net.minecraft.resources.ResourceLocation;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;

public class GenericModel<T extends IAnimatable> extends com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel<T> {
    public String path;

    public GenericModel(String name) {
        super(name);
        this.modelLocation = new ResourceLocation(ArsInstrumentum.MODID, "geo/" + name + ".geo.json");
        this.textLoc = new ResourceLocation(ArsInstrumentum.MODID, "textures/" + textPathRoot + "/" + name + ".png");
        this.animationLoc = new ResourceLocation(ArsInstrumentum.MODID, "animations/" + name + "_animations.json");
    }

    public GenericModel<T> withEmptyAnim() {
        this.animationLoc = new ResourceLocation(ArsInstrumentum.MODID, "animations/empty.json");
        return this;
    }

    @Override
    public ResourceLocation getModelResource(T iAnimatable) {
        return modelLocation;
    }

    @Override
    public ResourceLocation getTextureResource(T iAnimatable) {
        return textLoc;
    }

    @Override
    public ResourceLocation getAnimationResource(T iAnimatable) {
        return animationLoc;
    }
}
