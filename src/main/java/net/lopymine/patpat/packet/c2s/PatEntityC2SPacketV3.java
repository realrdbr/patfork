package net.lopymine.patpat.packet.c2s;

import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import net.lopymine.patpat.extension.EntityExtension;
import net.lopymine.patpat.packet.*;
import net.lopymine.patpat.utils.RLUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

@Getter
@ExtensionMethod(EntityExtension.class)
public class PatEntityC2SPacketV3 implements C2SPatPacket<PatEntityC2SPacketV3> {

    public static final String PACKET_ID = "pat_entity_c2s_packet_v3";

    public static final PatPatPacketType<PatEntityC2SPacketV3> TYPE =
            new PatPatPacketType<>(
                    RLUtils.modId(PACKET_ID),
                    PatEntityC2SPacketV3::new,
                    PatEntityC2SPacketV3.class
            );

    private final int pattedEntityId;
    private final boolean serverSwingHandEnabled;

    public PatEntityC2SPacketV3(Entity pattedEntity, boolean serverSwingHandEnabled) {
        this.pattedEntityId = pattedEntity.getEntityIntId();
        this.serverSwingHandEnabled = serverSwingHandEnabled;
    }

    public PatEntityC2SPacketV3(FriendlyByteBuf buf) {
        this.pattedEntityId = buf.readVarInt();
        this.serverSwingHandEnabled = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.pattedEntityId);
        buf.writeBoolean(this.serverSwingHandEnabled);
    }

    @Override
    @Nullable
    public Entity getPattedEntity(ServerLevel world) {
        return world.getEntity(this.getPattedEntityId());
    }

    @Override
    public PatPatPacketType<PatEntityC2SPacketV3> getPatPatType() {
        return TYPE;
    }
}