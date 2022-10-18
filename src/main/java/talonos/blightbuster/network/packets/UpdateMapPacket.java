package talonos.blightbuster.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class UpdateMapPacket implements IMessage {

	private int mapX;
	private int mapY;
	private int updateWidth;
	private int updateHeight;

	private byte[] updateData;

	public UpdateMapPacket() {

	}

	public UpdateMapPacket(int mapX, int mapY, int width, int height, byte[] data) {
		this.mapX = mapX;
		this.mapY = mapY;
		this.updateWidth = width;
		this.updateHeight = height;
		this.updateData = data;
	}

	@Override
	public void fromBytes(ByteBuf in) {
		this.mapX = in.readInt();
		this.mapY = in.readInt();
		this.updateWidth = in.readInt();
		this.updateHeight = in.readInt();
		this.updateData = new byte[updateWidth * updateHeight];
		in.readBytes(this.updateData, 0, updateData.length);
	}

	@Override
	public void toBytes(ByteBuf out) {
		out.writeInt(mapX);
		out.writeInt(mapY);
		out.writeInt(updateWidth);
		out.writeInt(updateHeight);
		out.writeBytes(updateData);
	}

	public int getMapX() { return mapX; }

	public int getMapY() { return mapY; }

	public int getUpdateHeight() { return updateHeight; }

	public int getUpdateWidth() { return updateWidth; }

	public byte[] getUpdateData() { return updateData; }
}
