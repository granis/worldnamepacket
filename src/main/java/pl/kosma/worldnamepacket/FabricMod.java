package pl.kosma.worldnamepacket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;


public class FabricMod implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger();
    
    @Override
    public void onInitialize() {
    	ServerPlayNetworking.registerGlobalReceiver(new Identifier(WorldNamePacket.CHANNEL_NAME_VOXELMAP),
    			(server, player, handler, buf, responseSender) -> { sendResponse(player, WorldNamePacket.CHANNEL_NAME_VOXELMAP); });
    }

    /**
     * Xaero's Map requires the world name to be send unprompted upon world join/change.
     */
    static public void onServerWorldInfo(ServerPlayerEntity player)
    {
    	sendResponse(player, WorldNamePacket.CHANNEL_NAME_XAEROMAP);
    }

    static private void sendResponse(ServerPlayerEntity player, String channel)
    {
    	ServerWorld serverWorld = player.getServerWorld();
    	MinecraftDedicatedServer dedicatedServer = (MinecraftDedicatedServer) serverWorld.getServer(); 
    	String levelName = dedicatedServer.getLevelName();   
    	FabricMod.LOGGER.info("WorldNamePacket: ["+channel+"] sending levelName: " + levelName);
    	
    	PacketByteBuf response = PacketByteBufs.create();
    	response.writeByte(WorldNamePacket.PACKET_ID);
    	response.writeByteArray(levelName.getBytes());
    	ServerPlayNetworking.send(player, new Identifier(channel), response);
    }
}
