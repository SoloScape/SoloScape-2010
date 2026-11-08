package net.scapeemulator.game.msg;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.inventory.Inventory;
import net.scapeemulator.game.model.inventory.InventoryInfo;
import net.scapeemulator.game.model.player.Appearance;
import net.scapeemulator.game.model.player.Gender;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.update.player.block.AppearancePlayerBlock;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CharacterAppearanceBlockTest {
	@Test
	public void femaleCreationIncludesSecondaryTorsoKitInSlotEleven() {
		Inventory.addDetails(new InventoryInfo(Inventory.EQUIPMENT, "equipment", 387, 28, 14,
				Inventory.StackMode.STACKABLE_ONLY));
		Player player = new Player();
		player.setUsername("female");
		player.setAppearance(new Appearance(Gender.FEMALE,
				new int[] {223, 1000, 57, 65, 68, 77, 80},
				new int[] {3, 16, 16, 0, 0}));
		ByteBuf allocation = Unpooled.buffer();
		Packet packet = null;
		try {
			PacketBuilder builder = new PacketBuilder(allocation.alloc(), 0);
			new AppearancePlayerBlock(player).encode(null, builder);
			packet = builder.toPacket();
			ByteBuf payload = packet.getPayload();
			payload.skipBytes(5);
			int finalKit = -1;
			for (int slot = 0; slot <= 11; slot++) {
				int encoded = payload.readUnsignedByte();
				if (encoded != 0) encoded = (encoded << 8) | payload.readUnsignedByte();
				if (slot == 11) finalKit = encoded - 256;
			}
			assertEquals(1000, finalKit);
		} finally {
			if (packet != null) packet.getPayload().release();
			allocation.release();
		}
	}

	@Test
	public void creationHairKitsAbove255SurviveTheClientWireOffset() {
		Inventory.addDetails(new InventoryInfo(Inventory.EQUIPMENT, "equipment", 387, 28, 14,
				Inventory.StackMode.STACKABLE_ONLY));
		for (int hair : new int[] {0, 302, 697, 741}) {
			Player player = new Player();
			player.setUsername("appearance");
			player.setAppearance(new Appearance(Gender.MALE, new int[] {hair, 14, 111, 105, 34, 36, 42},
					new int[] {20, 24, 24, 0, 7}));
			ByteBuf allocation = Unpooled.buffer();
			Packet packet = null;
			try {
				PacketBuilder builder = new PacketBuilder(allocation.alloc(), 0);
				new AppearancePlayerBlock(player).encode(null, builder);
				packet = builder.toPacket();
				ByteBuf payload = packet.getPayload();
				payload.skipBytes(5); // Appearance length, flags, title, skull, prayer icon.
				for (int slot = 0; slot <= 8; slot++) {
					int encoded = payload.readUnsignedByte();
					if (encoded != 0) encoded = (encoded << 8) | payload.readUnsignedByte();
					if (slot == 8) assertEquals(hair, encoded - 256);
				}
			} finally {
				if (packet != null) packet.getPayload().release();
				allocation.release();
			}
		}
	}
}
