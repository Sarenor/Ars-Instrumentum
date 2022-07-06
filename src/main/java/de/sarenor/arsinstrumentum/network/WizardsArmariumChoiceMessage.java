package de.sarenor.arsinstrumentum.network;

import de.sarenor.arsinstrumentum.items.curios.armarium.WizardsArmarium;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Supplier;

import static de.sarenor.arsinstrumentum.setup.Registration.WIZARDS_ARMARIUM;

@RequiredArgsConstructor
@Getter
public class WizardsArmariumChoiceMessage {

    private final int choosenSlot;

    //Decoder
    public WizardsArmariumChoiceMessage(FriendlyByteBuf buf) {
        choosenSlot = buf.readInt();
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(choosenSlot);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && CuriosApi.getCuriosHelper().findEquippedCurio(WIZARDS_ARMARIUM.get(), player).isPresent()) {
                WizardsArmarium.handleArmariumChoice(player, choosenSlot, ctx);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

