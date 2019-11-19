package carpet.utils;

import carpet.CarpetServer;
import carpet.helpers.TickSpeed;
import carpet.settings.CarpetSettings;
import carpet.settings.ParsedRule;
import io.netty.buffer.Unpooled;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PacketByteBuf;

public class ClientUtils
{
    public static int GENERAL_INFO = 0;
    
    public static void sendCarpetInfo(ServerPlayerEntity playerEntity)
    {
        CompoundTag data = new CompoundTag();
        
        data.putString("CarpetVersion", CarpetSettings.carpetVersion);
        data.putFloat("Tickrate", TickSpeed.tickrate);
        
        ListTag rulesList = new ListTag();
        for (ParsedRule<?> rule : CarpetServer.settingsManager.getRules())
        {
            CompoundTag ruleNBT = new CompoundTag();
            
            ruleNBT.putString("rule" ,rule.name);
            ruleNBT.putString("value", rule.getAsString());
            ruleNBT.putString("description", rule.description);
            ruleNBT.putString("default", rule.defaultAsString);
            ruleNBT.putString("type", rule.type.getName());
            ListTag extraList = new ListTag();
            if (rule.extraInfo != null)
            {
                for (String extra : rule.extraInfo)
                    extraList.add(new StringTag(extra));
            }
            ruleNBT.put("extraInfo", extraList);
            rulesList.add(ruleNBT);
        }
        data.put("rules", rulesList);
        PacketByteBuf packetBuf = new PacketByteBuf(Unpooled.buffer());
        packetBuf.writeVarInt(GENERAL_INFO);
        packetBuf.writeCompoundTag(data);
        playerEntity.networkHandler.sendPacket(new CustomPayloadS2CPacket(CarpetServer.CARPET_CHANNEL_NAME, packetBuf));
    }
}