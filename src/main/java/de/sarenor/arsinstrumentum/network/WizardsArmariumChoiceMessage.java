package de.sarenor.arsinstrumentum.network;

import com.hollingsworth.arsnouveau.common.network.AbstractPacket;
import de.sarenor.arsinstrumentum.ArsInstrumentum;
import de.sarenor.arsinstrumentum.items.curios.armarium.WizardsArmarium;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;

import static de.sarenor.arsinstrumentum.setup.Registration.WIZARDS_ARMARIUM;

public class WizardsArmariumChoiceMessage extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, WizardsArmariumChoiceMessage> CODEC = StreamCodec.ofMember(WizardsArmariumChoiceMessage::toBytes, WizardsArmariumChoiceMessage::new);
    public static final CustomPacketPayload.Type<WizardsArmariumChoiceMessage> TYPE = new CustomPacketPayload.Type<>(ArsInstrumentum.prefix("wizards_armarium_choice"));

    private final int choosenSlot;

    public WizardsArmariumChoiceMessage(int choosenSlot) {
        this.choosenSlot = choosenSlot;
    }

    //Decoder
    public WizardsArmariumChoiceMessage(FriendlyByteBuf buf) {
        choosenSlot = buf.readInt();
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(choosenSlot);
    }

    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
            if (player != null && CuriosApi.getCuriosInventory(player).flatMap(i -> i.findFirstCurio(WIZARDS_ARMARIUM.get())).isPresent()) {
                WizardsArmarium.handleArmariumChoice(player, choosenSlot);
            }

    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

