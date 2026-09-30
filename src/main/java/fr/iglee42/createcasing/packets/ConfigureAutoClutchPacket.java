package fr.iglee42.createcasing.packets;

import com.zurrtum.create.content.kinetics.RotationPropagator;
import fr.iglee42.createcasing.CreateCasing;
import fr.iglee42.createcasing.blockEntities.AutoClutchBlockEntity;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

/**
 * Sent by the automatic clutch screen when it closes. Upstream extended Create's
 * {@code BlockEntityConfigurationPacket}; this is a Fabric payload with the same range check.
 */
public record ConfigureAutoClutchPacket(BlockPos pos, int stress, int mode, int operation) implements CustomPacketPayload {
    public static final Type<ConfigureAutoClutchPacket> TYPE = new Type<>(CreateCasing.asResource("auto_clutch_configure"));

    public static final StreamCodec<ByteBuf, ConfigureAutoClutchPacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, ConfigureAutoClutchPacket::pos,
        ByteBufCodecs.INT, ConfigureAutoClutchPacket::stress,
        ByteBufCodecs.INT, ConfigureAutoClutchPacket::mode,
        ByteBufCodecs.INT, ConfigureAutoClutchPacket::operation,
        ConfigureAutoClutchPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(TYPE, STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(TYPE, (packet, context) -> packet.handle(context.player()));
    }

    private void handle(ServerPlayer player) {
        Level level = player.level();
        if (!level.isLoaded(pos) || !player.isWithinBlockInteractionRange(pos, 20))
            return;
        if (!(level.getBlockEntity(pos) instanceof AutoClutchBlockEntity be))
            return;
        be.setConfiguredValue(stress);
        be.setMode(AutoClutchBlockEntity.Mode.byId(mode));
        be.setOperation(AutoClutchBlockEntity.Operation.byId(operation));
        RotationPropagator.handleAdded(level, pos, be);
        be.sendData();
        be.setChanged();
    }
}
