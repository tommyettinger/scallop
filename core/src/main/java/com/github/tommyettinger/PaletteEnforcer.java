package com.github.tommyettinger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.zip.CRC32;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class PaletteEnforcer extends ApplicationAdapter {
	public String[] files;

	public PaletteEnforcer(String[] filenames) {
		files = filenames;
	}

	/**
	 * Simple PNG IO from https://www.java-tips.org/java-se-tips-100019/23-java-awt-image/2283-png-file-format-decoder-in-java.html .
	 * @param inStream
	 * @return
	 * @throws IOException
	 */
	protected static LinkedHashMap<String, byte[]> readChunks(InputStream inStream) throws IOException {
		DataInputStream in = new DataInputStream(inStream);
		if(in.readLong() != 0x89504e470d0a1a0aL)
			throw  new IOException("PNG signature not found!");
		LinkedHashMap<String, byte[]> chunks = new LinkedHashMap<>(10);
		boolean trucking = true;
		while (trucking) {
			try {
				// Read the length.
				int length = in.readInt();
				if (length < 0)
					throw new IOException("Sorry, that file is too long.");
				// Read the type.
				byte[] typeBytes = new byte[4];
				in.readFully(typeBytes);
				// Read the data.
				byte[] data = new byte[length];
				in.readFully(data);
				// Read the CRC, discard it.
				int crc = in.readInt();
				String type = new String(typeBytes, StandardCharsets.UTF_8);
				chunks.put(type, data);
			} catch (EOFException eofe) {
				trucking = false;
			}
		}
		in.close();
		return chunks;
	}

	/**
	 * Simple PNG IO from https://www.java-tips.org/java-se-tips-100019/23-java-awt-image/2283-png-file-format-decoder-in-java.html .
	 * @param outStream
	 * @param chunks
	 */
	protected static void writeChunks(OutputStream outStream, LinkedHashMap<String, byte[]> chunks) {
		DataOutputStream out = new DataOutputStream(outStream);
		CRC32 crc = new CRC32();
		try {
			out.writeLong(0x89504e470d0a1a0aL);
			for (HashMap.Entry<String, byte[]> ent : chunks.entrySet()) {
				out.writeInt(ent.getValue().length);
				out.writeBytes(ent.getKey());
				crc.update(ent.getKey().getBytes(StandardCharsets.UTF_8));
				out.write(ent.getValue());
				crc.update(ent.getValue());
				out.writeInt((int) crc.getValue());
				crc.reset();
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Given a FileHandle to read from and a FileHandle to write to, duplicates the input FileHandle and changes its
	 * palette (in full and in order) to exactly match {@code palette}. This is only likely to work if the input file
	 * was written with the same palette order.
	 * @param input FileHandle to read from that should have a similar palette (and very similar order) to {@code palette}
	 * @param output FileHandle that should be writable and empty
	 * @param palette RGBA8888 color array
	 */
	public static void swapPalette(FileHandle input, FileHandle output, int[] palette)
	{
		try {
			InputStream inputStream = input.read();
			LinkedHashMap<String, byte[]> chunks = readChunks(inputStream);
			byte[] pal = chunks.get("PLTE");
			if(pal == null)
			{
				output.write(inputStream, false);
				return;
			}
			for (int i = 0, p = 0; i < palette.length && p < pal.length - 2; i++) {
				int rgba = palette[i];
				pal[p++] = (byte) (rgba >>> 24);
				pal[p++] = (byte) (rgba >>> 16);
				pal[p++] = (byte) (rgba >>> 8);
			}
			writeChunks(output.write(false), chunks);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Given a FileHandle to read from and a FileHandle to write to, duplicates the input FileHandle and changes its
	 * palette (in full and in order) to exactly match {@code palette}. This is only likely to work if the input file
	 * was written with the same palette order.
	 * @param input FileHandle to read from that should have a similar palette (and very similar order) to {@code palette}
	 * @param output FileHandle that should be writable and empty
	 */
	public static void grayPalette(FileHandle input, FileHandle output)
	{
		try {
			InputStream inputStream = input.read();
			LinkedHashMap<String, byte[]> chunks = readChunks(inputStream);
			byte[] pal = chunks.get("PLTE");
			if(pal == null)
			{
				output.write(inputStream, false);
				return;
			}
			for (int i = 0, p = 0; i < 256 && p < pal.length - 2; i++) {
				int rgba = i * 0x01010100;
				pal[p++] = (byte) (rgba >>> 24);
				pal[p++] = (byte) (rgba >>> 16);
				pal[p++] = (byte) (rgba >>> 8);
			}
			writeChunks(output.write(false), chunks);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Given a FileHandle to read from and a FileHandle to write to, duplicates the input FileHandle and changes its
	 * palette (in full and in order) to exactly match {@code palette}. This is only likely to work if the input file
	 * was written with the same palette order.
	 * @param input FileHandle to read from that should have a similar palette (and very similar order) to {@code palette}
	 * @param output FileHandle that should be writable and empty
	 */
	public static void redPalette(FileHandle input, FileHandle output)
	{
		try {
			InputStream inputStream = input.read();
			LinkedHashMap<String, byte[]> chunks = readChunks(inputStream);
			byte[] pal = chunks.get("PLTE");
			if(pal == null)
			{
				output.write(inputStream, false);
				return;
			}
			for (int i = 0, p = 0; i < 256 && p < pal.length - 2; i++) {
				pal[p++] = (byte) i;
				pal[p++] = (byte) 0;
				pal[p++] = (byte) 0;
			}
			writeChunks(output.write(false), chunks);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void create() {
		for (String s : files) {
			boolean isAbsolute = s.matches(".*[/\\\\].*");
			FileHandle fh = isAbsolute ? Gdx.files.absolute(s) : Gdx.files.local(s);
			if(isAbsolute) {
				grayPalette(fh, Gdx.files.absolute(fh.parent().path() + "/grayscale-" + fh.name()));
				redPalette(fh, Gdx.files.absolute(fh.parent().path() + "/redscale-" + fh.name()));
			}
			else {

				grayPalette(fh, Gdx.files.local(fh.parent().path() + "/grayscale-" + fh.name()));
				redPalette(fh, Gdx.files.local(fh.parent().path() + "/redscale-" + fh.name()));
			}
		}
		Gdx.app.exit();
	}
}
